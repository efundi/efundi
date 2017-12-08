package org.sakaiproject.cmprovider;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import javax.naming.NamingException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.authz.api.AuthzGroupService;
import org.sakaiproject.cmprovider.utility.LDAPRetrieval;
import org.sakaiproject.cmprovider.utility.RosterUser;
import org.sakaiproject.component.api.ServerConfigurationService;
import org.sakaiproject.coursemanagement.api.AcademicSession;
import org.sakaiproject.coursemanagement.api.CourseManagementAdministration;
import org.sakaiproject.coursemanagement.api.CourseManagementService;
import org.sakaiproject.coursemanagement.api.CourseOffering;
import org.sakaiproject.coursemanagement.api.EnrollmentSet;
import org.sakaiproject.coursemanagement.api.SectionCategory;
import org.sakaiproject.coursemanagement.api.exception.IdNotFoundException;
import org.sakaiproject.entitybroker.DeveloperHelperService;
import org.sakaiproject.entitybroker.EntityView;
import org.sakaiproject.entitybroker.entityprovider.annotations.EntityCustomAction;
import org.sakaiproject.entitybroker.entityprovider.capabilities.ActionsExecutable;
import org.sakaiproject.entitybroker.entityprovider.capabilities.AutoRegisterEntityProvider;
import org.sakaiproject.entitybroker.entityprovider.capabilities.Describeable;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.user.api.UserDirectoryService;

public class NWUCmEntityProvider implements AutoRegisterEntityProvider, ActionsExecutable, Describeable {

	public final static String PREFIX = "nwu-cm";

	private static Log log = LogFactory.getLog(NWUCmEntityProvider.class);

	protected CourseManagementService cmService;

	protected CourseManagementAdministration cmAdmin;

	protected DeveloperHelperService developerService;

	private UserDirectoryService userDirectoryService;

	private ServerConfigurationService serverConfigurationService;

	protected AuthzGroupService authzGroupService;

	protected SessionManager sessionManager;

	private LDAPRetrieval ldapRetrieval;	
	
	public void init() {
    }

	@Override
	public String getEntityPrefix() {
		return PREFIX;
	}

	/**
	 * Insert the Sakai Course Management Data
	 * 
	 * @param year
	 *            The year for which to insert the data
	 * @param courseCode
	 *            The course code of the module. (eg. AFNL)
	 * @param canonicalCourseRef
	 *            The canonical course reference of the module (eg. AFNL 111)
	 * @param courseOfferingRef
	 *            The course offering reference of the module (eg. AFNL 111 P 2011)
	 * @param enrollmentSetRef
	 *            The course offering enrollment set reference of the module (eg. AFNL 111 P 2011 ES)
	 * @param lecturerUserName
	 *            The lecturer user name linked to this module.
	 * @param studentUserNames
	 *            A comma delimited string of the student user names linked to this module.
	 * @return A string indicating whether this call was successful.
	 */
	@EntityCustomAction(action = "insert-cm-data", viewKey = "")
	public String handleinsertCMData(EntityView view, Map<String, Object> params) {
		log.info("SakaiCM REST Web Service - performing insertCMData");
		
		String yearStr = (String) params.get("year");
		if (StringUtils.isBlank(yearStr)) {
			throw new IllegalArgumentException("year must be set in order to create CM data, via the URL /nwu-cm/insert-cm-data");
		}

		int year;
		try {
			year = Integer.parseInt(yearStr);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("year must be a numeric parseable value, via the URL /nwu-cm/insert-cm-data");
		}

		String courseCode = (String) params.get("courseCode");
		if (StringUtils.isBlank(courseCode)) {
			throw new IllegalArgumentException(
					"courseCode must be set in order to create CM data, via the URL /nwu-cm/insert-cm-data");
		}
		String canonicalCourseRef = (String) params.get("canonicalCourseRef");
		if (StringUtils.isBlank(canonicalCourseRef)) {
			throw new IllegalArgumentException(
					"canonicalCourseRef must be set in order to create CM data, via the URL /nwu-cm/insert-cm-data");
		}
		String courseOfferingRef = (String) params.get("courseOfferingRef");
		if (StringUtils.isBlank(courseOfferingRef)) {
			throw new IllegalArgumentException(
					"courseOfferingRef must be set in order to create CM data, via the URL /nwu-cm/insert-cm-data");
		}
		String enrollmentSetRef = (String) params.get("enrollmentSetRef");
		if (StringUtils.isBlank(enrollmentSetRef)) {
			throw new IllegalArgumentException(
					"enrollmentSetRef must be set in order to create CM data, via the URL /nwu-cm/insert-cm-data");
		}
		String lecturerUserName = (String) params.get("lecturerUserName");
		if (StringUtils.isBlank(lecturerUserName)) {
			throw new IllegalArgumentException(
					"lecturerUserName must be set in order to create CM data, via the URL /nwu-cm/insert-cm-data");
		}
		String studentUserNames = (String) params.get("studentUserNames");

		List<String> studentUserNamesList = new ArrayList<String>();
		Scanner scanner = new Scanner(studentUserNames);
		scanner.useDelimiter(",");
		while (scanner.hasNext()) {
			studentUserNamesList.add(scanner.next());
		}
		AcademicSession academicSession = createAcademicSession(year);
		createCourseSets(courseCode, lecturerUserName);
		createCanonicalCourses(canonicalCourseRef);
		createCourseOfferingRelatedData(academicSession, courseCode, canonicalCourseRef, courseOfferingRef, enrollmentSetRef,
				lecturerUserName, studentUserNamesList);
		// Insert/Update students/lecturers
		createSakaiUsers(lecturerUserName, studentUserNamesList);
		log.info("SakaiCM REST Web Service - insertCMData ended successfully");

		return "success";
	}

	private void createSakaiUsers(String lecturerUserName, List<String> studentUserNames) {
		if (serverConfigurationService.getBoolean("nwu.cm.users.create", false)) {
			try {
				// Lecturers
				String lecturerFilter = serverConfigurationService.getString("ldap.lecturer.filter",
						"(memberOf=cn=efundi-instructors,ou=orgunits,ou=groups,o=nwu)");
				RosterUser lecturer = new RosterUser(lecturerUserName);
				getLDAPRetrieval().setRosterUserDetails(lecturer, lecturerFilter);
				createRosterSakaiUser(lecturer);
				// Students
				List<RosterUser> students = new ArrayList<RosterUser>();
				for (String studentUserName : studentUserNames) {
					students.add(new RosterUser(studentUserName));
				}
				String studentFilter = serverConfigurationService.getString("ldap.student.filter",
						"(memberOf=cn=s,ou=orgunits,ou=groups,o=nwu)");
				if (students != null && !students.isEmpty()) {
					getLDAPRetrieval().setRosterUserDetails(students, studentFilter);
					createRosterSakaiUsers(students);
				}
			} catch (Exception e) {
				log.error(
						"SakaiCM REST Web Service - Could not create Sakai Users for Course Management. See previous log entries for more details.");
			} finally {
				try {
					getLDAPRetrieval().getContext().close();
				} catch (NamingException e) {
					log.warn("SakaiCM REST Web Service - Error closing LDAPRetrieval Context", e);
				}
			}
		}
	}

	/**
	 * Create the AcademicSession for the year and set it active/current
	 */
	private AcademicSession createAcademicSession(int year) {
		Calendar start = Calendar.getInstance();
		start.set(Calendar.YEAR, year);
		start.set(Calendar.MONTH, 0);
		start.set(Calendar.DAY_OF_MONTH, 1);
		Calendar end = Calendar.getInstance();
		end.set(Calendar.YEAR, year);
		end.set(Calendar.MONTH, 11);
		end.set(Calendar.DAY_OF_MONTH, 31);
		String title = MessageFormat.format(
				serverConfigurationService.getString("nwu.cm.AcademicSession.title", "Year {0,number,####}"),
				start.get(Calendar.YEAR));
		String description = MessageFormat.format(serverConfigurationService.getString("nwu.cm.AcademicSession.description",
				"Academic Session for Year {0,number,####}"), start.get(Calendar.YEAR));
		AcademicSession academicSession = null;
		try {
			academicSession = cmService.getAcademicSession(title);
			log.info("SakaiCM REST Web Service - Retrieved AcademicSession with id " + title);
		} catch (IdNotFoundException e) {
			// If AcademicSession do not exist, create it.
			academicSession = cmAdmin.createAcademicSession(title, title, description, start.getTime(), end.getTime());
			log.info("SakaiCM REST Web Service - Inserted AcademicSession with id " + title);
		}
		List<String> acadSessionIds = new ArrayList<String>();
		List<AcademicSession> allAcadSessions = cmService.getAcademicSessions();
		for (AcademicSession availableAcadSession : allAcadSessions) {
			acadSessionIds.add(availableAcadSession.getEid());
		}
		cmAdmin.setCurrentAcademicSessions(acadSessionIds);
		return academicSession;
	}

	/**
	 * Create Course Sets for all the subject codes. These are shown in the 'Subject' droplist of Sakai's course site setup.
	 */
	private void createCourseSets(String courseCode, String lecturerUserName) {
		String category = serverConfigurationService.getString("nwu.cm.CourseSet.category", "category");
		if (!cmService.isCourseSetDefined(courseCode)) {
			cmAdmin.createCourseSet(courseCode, courseCode, courseCode, category, null);
			log.info("SakaiCM REST Web Service - Inserted CourseSet with id " + courseCode);
		}
	}

	/**
	 * These abstract records are not shown on screen.
	 */
	private void createCanonicalCourses(String canonicalCourseRef) {
		if (!cmService.isCanonicalCourseDefined(canonicalCourseRef)) {
			cmAdmin.createCanonicalCourse(canonicalCourseRef, canonicalCourseRef, canonicalCourseRef);
			log.info("SakaiCM REST Web Service - Inserted CanonicalCourse with id " + canonicalCourseRef);
		}
	}

	/**
	 * The Course Offering records are shown in the 'Course' droplist of Sakai's course site setup.
	 */
	private void createCourseOfferingRelatedData(AcademicSession academicSession, String courseCode, String canonicalCourseRef,
			String courseOfferingRef, String enrollmentSetRef, String lecturerUserName, List<String> studentUserNames) {
		String status = serverConfigurationService.getString("nwu.cm.CourseOffering.status", "Active");
		String enrollmentSetCategory = serverConfigurationService.getString("nwu.cm.EnrollmentSet.category", "category");
		String enrollmentSetCredits = serverConfigurationService.getString("nwu.cm.EnrollmentSet.credits", "0");
		String enrollmentStatus = serverConfigurationService.getString("nwu.cm.Enrollment.status", "enrolled");
		String enrollmentCredits = serverConfigurationService.getString("nwu.cm.Enrollment.credits", "0");
		String gradingScheme = serverConfigurationService.getString("nwu.cm.Enrollment.gradingScheme", "standard");
		String sectionCategoryCode = createSectionCategory();
		String sectionStatus = serverConfigurationService.getString("nwu.cm.Section.Membership.status", "Active");
		String sectionLecturerRole = serverConfigurationService.getString("nwu.cm.Section.lecturer.role", "I");
		String sectionStudentRole = serverConfigurationService.getString("nwu.cm.Section.student.role", "S");
		if (!cmService.isCourseOfferingDefined(courseOfferingRef)) {
			// Give Canonical Course's Eid as title for Course Offering
			cmAdmin.createCourseOffering(courseOfferingRef, canonicalCourseRef, courseOfferingRef, status,
					academicSession.getEid(), canonicalCourseRef, academicSession.getStartDate(), academicSession.getEndDate());
			log.info("SakaiCM REST Web Service - Inserted CourseOffering with id " + courseOfferingRef);
		}
		addCourseOfferingsToCourseSets(courseOfferingRef, courseCode);
		// Add EnrollmentSets
		createEnrollmentSets(courseOfferingRef, enrollmentSetCategory, enrollmentSetCredits, enrollmentSetRef, lecturerUserName);
		// Add Enrollments
		createEnrollment(enrollmentSetRef, studentUserNames, enrollmentStatus, enrollmentCredits, gradingScheme);
		// Add Sections
		createSections(courseOfferingRef, lecturerUserName, studentUserNames, sectionCategoryCode, sectionStatus,
				sectionLecturerRole, sectionStudentRole, enrollmentSetRef);
	}

	private void addCourseOfferingsToCourseSets(String courseOfferingRef, String courseSetId) {
		boolean linkExists = false;
		// Check if Offering is already linked to CourseSet
		Set<CourseOffering> linkedOfferings = cmService.getCourseOfferingsInCourseSet(courseSetId);
		for (CourseOffering linkedOffering : linkedOfferings) {
			if (isEqual(linkedOffering.getEid(), courseOfferingRef)) {
				log.info("SakaiCM REST Web Service - CourseOffering with id '" + courseOfferingRef
						+ "' is already linked to CourseSet with id '" + courseSetId + "'.");
				linkExists = true;
				break;
			}
		}
		if (!linkExists) {
			// Add if Offering is not already linked.
			cmAdmin.addCourseOfferingToCourseSet(courseSetId, courseOfferingRef);
			log.info("SakaiCM REST Web Service - Added CourseOffering (" + courseOfferingRef + ") to CourseSet (" + courseSetId + ")");
		}
	}

	/**
	 * Arbitrary section category title.
	 */
	private String createSectionCategory() {
		String sectionCategoryCode = null;
		String category = serverConfigurationService.getString("nwu.cm.SectionCategory.category", "LCT");
		String catDesc = serverConfigurationService.getString("nwu.cm.SectionCategory.description", "Lecture");
		boolean exists = false;
		for (String categoryCode : cmService.getSectionCategories()) {
			if (category.equals(categoryCode)) {
				if (catDesc.equals(cmService.getSectionCategoryDescription(category))) {
					sectionCategoryCode = categoryCode;
					log.info("SakaiCM REST Web Service - Section Category '" + category + "' already exists.");
					exists = true;
					break;
				}
			}
		}
		if (!exists) {
			SectionCategory sectionCategory = cmAdmin.addSectionCategory(category, catDesc);
			sectionCategoryCode = sectionCategory.getCategoryCode();
			log.info("SakaiCM REST Web Service - Section Category (" + category + " - " + catDesc + ") successfully inserted.");
		}
		return sectionCategoryCode;
	}

	/**
	 * Creates the EnrollmentSets per CourseOffering/Class Group.
	 */
	private void createEnrollmentSets(String courseOfferingRef, String enrollmentSetCategory, String enrollmentSetCredits,
			String enrollmentSetRef, String lecturerUserName) {
		if (!cmService.isEnrollmentSetDefined(enrollmentSetRef)) {
			Set<String> lecturers = new HashSet<String>();
			lecturers.add(lecturerUserName);
			cmAdmin.createEnrollmentSet(enrollmentSetRef, enrollmentSetRef, enrollmentSetRef, enrollmentSetCategory,
					enrollmentSetCredits, courseOfferingRef, lecturers);
			log.info("SakaiCM REST Web Service - Inserted EnrollmentSet with id " + enrollmentSetRef);
		}
	}

	/**
	 * Only students should have Enrollments. Lecturers are added to the EnrollmentSet.
	 */
	private void createEnrollment(String enrollmentSetRef, List<String> studentUserNames, String enrollmentStatus,
			String enrollmentCredits, String gradingScheme) {
		for (String studentUserName : studentUserNames) {
			cmAdmin.addOrUpdateEnrollment(studentUserName, enrollmentSetRef, enrollmentStatus, enrollmentCredits, gradingScheme);
			log.info("SakaiCM REST Web Service - Added/Updated Enrollment for user id " + studentUserName);
		}
	}

	/**
	 * The Section's description is displayed (with checkbox) on the Course/Section Information screen of Sakai's course site
	 * setup.
	 */
	private void createSections(String courseOfferingRef, String lecturerUserName, List<String> studentUserNames,
			String sectionCategoryCode, String sectionStatus, String sectionLecturerRole, String sectionStudentRole,
			String enrollmentSetRef) {
		// Section's make use of the same eids as CourseOfferings.
		if (!cmService.isSectionDefined(courseOfferingRef)) {
			cmAdmin.createSection(courseOfferingRef, courseOfferingRef, courseOfferingRef + " Lecture", sectionCategoryCode, null,
					courseOfferingRef, enrollmentSetRef);
			log.info("SakaiCM REST Web Service - Inserted Section with id " + courseOfferingRef);
		}
		// Add Section Memberships
		createSectionMemberships(courseOfferingRef, lecturerUserName, studentUserNames, sectionStatus, sectionLecturerRole,
				sectionStudentRole);
	}

	private void createSectionMemberships(String courseOfferingRef, String lecturerUserName, List<String> studentUserNames,
			String sectionStatus, String sectionLecturerRole, String sectionStudentRole) {
		// Students
		for (String studentUserName : studentUserNames) {
			cmAdmin.addOrUpdateSectionMembership(studentUserName, sectionStudentRole, courseOfferingRef, sectionStatus);
			log.info("SakaiCM REST Web Service - Added/Updated SectionMembership - " + studentUserName + " to " + courseOfferingRef);
		}
	}

	private LDAPRetrieval getLDAPRetrieval() {
		if (ldapRetrieval == null) {
			ldapRetrieval = new LDAPRetrieval(serverConfigurationService);
		}
		return ldapRetrieval;
	}

	private void createRosterSakaiUser(RosterUser rosterUser) {
		List<RosterUser> rosterUsers = new ArrayList<RosterUser>();
		rosterUsers.add(rosterUser);
		createRosterSakaiUsers(rosterUsers);
	}

	private void createRosterSakaiUsers(List<RosterUser> rosterUsers) {
		try {
			for (RosterUser rosterUser : rosterUsers) {
				String userId = null;
				try {
					userId = userDirectoryService.getUserId(rosterUser.getUserName());
				} catch (Exception e1) {
					userId = "";
				}
				boolean userExists = userId != null && !userId.equals("");
				if (userExists) {
					log.info("SakaiCM REST Web Service - User " + rosterUser.getUserName() + " already exists.");
				} else {
					userDirectoryService.addUser(null, rosterUser.getUserName(), rosterUser.getFirstName(),
							rosterUser.getSurname(), rosterUser.getEmail(), rosterUser.getPassword(), "maintain", null);
					log.info("SakaiCM REST Web Service - Added user " + rosterUser.getUserName());
				}
			}
		} catch (Exception e) {
			log.error("SakaiCM REST Web Service - createRosterSakaiUsers - Exception occurred: ", e);
		}
	}

	/**
	 * Delete the Sakai Course Management data.
	 * 
	 * @param year
	 *            The year for which to delete the CM data
	 * @param courseCode
	 *            The course code of the module. (eg. AFNL)
	 * @param canonicalCourseRef
	 *            The canonical course reference of the module (eg. AFNL 111)
	 * @param courseOfferingRef
	 *            The course offering reference of the module (eg. AFNL 111 P 2011)
	 * @param enrollmentSetRef
	 *            The enrollment set reference of the module (eg. AFNL 111 P 2011 ES)
	 * @param lecturerUserName
	 *            The lecturer user name that will be unlinked from this module.
	 * @param studentUserNames
	 *            A comma delimited string of student user names that will be unlinked from this module.
	 * @param courseSetExists
	 *            A boolean indicating whether a non deleted course set (eg. AFNL) exists. (any lecturer + in any year).
	 * @param canonicalCourseExists
	 *            A boolean indicating whether a non deleted canonical course (eg. AFNL 111) exists. (any lecturer + in any year).
	 * @param courseOfferingExists
	 *            A boolean indicating whether a non deleted course offering (eg. AFNL 111 P 2011) exists. (for other lecturers).
	 * @param onlyDeletedModulesExist
	 *            A boolean indicating whether only deleted modules (modules with deleted status) exists for the year.
	 * @return A string indicating whether this call was successful.
	 */
	
	@EntityCustomAction(action = "delete-cm-data", viewKey = "")
	public String handledeleteCMData(EntityView view, Map<String, Object> params) {
		log.info("SakaiCM REST Web Service - performing deleteCMData");

		String yearStr = (String) params.get("year");
		if (StringUtils.isBlank(yearStr)) {
			throw new IllegalArgumentException("year must be set in order to create CM data, via the URL /nwu-cm/insert-cm-data");
		}

		int year;
		try {
			year = Integer.parseInt(yearStr);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("year must be a numeric parseable value, via the URL /nwu-cm/insert-cm-data");
		}

		String courseCode = (String) params.get("courseCode");
		if (StringUtils.isBlank(courseCode)) {
			throw new IllegalArgumentException(
					"courseCode must be set in order to create CM data, via the URL /nwu-cm/insert-cm-data");
		}
		String canonicalCourseRef = (String) params.get("canonicalCourseRef");
		if (StringUtils.isBlank(canonicalCourseRef)) {
			throw new IllegalArgumentException(
					"canonicalCourseRef must be set in order to create CM data, via the URL /nwu-cm/insert-cm-data");
		}
		String courseOfferingRef = (String) params.get("courseOfferingRef");
		if (StringUtils.isBlank(courseOfferingRef)) {
			throw new IllegalArgumentException(
					"courseOfferingRef must be set in order to create CM data, via the URL /nwu-cm/insert-cm-data");
		}
		String enrollmentSetRef = (String) params.get("enrollmentSetRef");
		if (StringUtils.isBlank(enrollmentSetRef)) {
			throw new IllegalArgumentException(
					"enrollmentSetRef must be set in order to create CM data, via the URL /nwu-cm/insert-cm-data");
		}
		String lecturerUserName = (String) params.get("lecturerUserName");
		if (StringUtils.isBlank(lecturerUserName)) {
			throw new IllegalArgumentException(
					"lecturerUserName must be set in order to create CM data, via the URL /nwu-cm/insert-cm-data");
		}
		String studentUserNames = (String) params.get("studentUserNames");		
		String courseSetExistsObj = (String) params.get("courseSetExists");		
		boolean courseSetExists = Boolean.getBoolean(courseSetExistsObj);
		
		String canonicalCourseExistsObj = (String) params.get("canonicalCourseExists");
		boolean canonicalCourseExists  = Boolean.getBoolean(canonicalCourseExistsObj);
		
		String courseOfferingExistsObj = (String) params.get("courseOfferingExists");
		boolean courseOfferingExists = Boolean.getBoolean(courseOfferingExistsObj);
		
		String onlyDeletedModulesExistObj = (String) params.get("onlyDeletedModulesExist");
		boolean onlyDeletedModulesExist = Boolean.getBoolean(onlyDeletedModulesExistObj);						
		
		// Remove in reverse order to avoid foreign key data problems
		List<String> studentUserNamesList = new ArrayList<String>();
		Scanner scanner = new Scanner(studentUserNames);
		scanner.useDelimiter(",");
		while (scanner.hasNext()) {
			studentUserNamesList.add(scanner.next());
		}
		deleteCourseOfferingRelatedData(courseCode, courseOfferingRef, enrollmentSetRef, lecturerUserName,
				courseOfferingExists, studentUserNamesList);
		deleteCanonicalCourses(canonicalCourseRef, canonicalCourseExists);
		deleteCourseSets(courseCode, courseSetExists);
		// If only deleted data exists for this year, delete the AcademicSession.
		if (onlyDeletedModulesExist) {
			deleteAcademicSession(year);
		}
		log.info("SakaiCM REST Web Service - deleteCMData has finished successfully");
		return "success";
	}

	private void deleteCourseOfferingRelatedData(String courseCode, String courseOfferingRef, String enrollmentSetRef,
			String lecturerUserName, boolean courseOfferingExists, List<String> studentUserNames) {
		// Lecturers shouldn't have enrollments. They are linked to the EnrollmentSet.
		// Remove lecturer from EnrollmentSet
		if (cmService.isEnrollmentSetDefined(enrollmentSetRef)) {
			EnrollmentSet enrollmentSet = cmService.getEnrollmentSet(enrollmentSetRef);
			Set<String> instructors = enrollmentSet.getOfficialInstructors();
			for (String instructor : instructors) {
				if (isEqual(instructor, lecturerUserName)) {
					instructors.remove(instructor);
					break;
				}
			}
			enrollmentSet.setOfficialInstructors(instructors);
			cmAdmin.updateEnrollmentSet(enrollmentSet);
			log.info("SakaiCM REST Web Service - Removed Lecturer from EnrollmentSet: " + lecturerUserName + " - " + enrollmentSetRef);
		}
		// Students' membership/enrollment should only be removed when no same CourseOffering exists for another lecturer.
		if (!courseOfferingExists) {
			// Students
			for (String studentUserName : studentUserNames) {
				// Section Memberships
				cmAdmin.removeSectionMembership(studentUserName, courseOfferingRef);
				log.info("SakaiCM REST Web Service - Removed Student Membership from Section: " + studentUserName + " - "
						+ courseOfferingRef);
				// Enrollment
				cmAdmin.removeEnrollment(studentUserName, enrollmentSetRef);
				log.info("SakaiCM REST Web Service - Removed Student from Enrollment: " + studentUserName + " - " + enrollmentSetRef);
			}
			cmAdmin.removeCourseOfferingFromCourseSet(courseCode, courseOfferingRef);
			log.info("SakaiCM REST Web Service - Removed CourseOffering from CourseSet: " + courseOfferingRef + " - " + courseCode);
			// This also removes Sections and EnrollmentSets linked to the CourseOffering.
			cmAdmin.removeCourseOffering(courseOfferingRef);
			log.info("SakaiCM REST Web Service - Removed CourseOffering: " + courseOfferingRef);
		}
	}

	private void deleteCanonicalCourses(String canonicalCourseRef, boolean canonicalCourseExists) {
		// Canonical Courses are not year driven - must check db for test
		if (!canonicalCourseExists) {
			cmAdmin.removeCanonicalCourse(canonicalCourseRef);
			log.info("SakaiCM REST Web Service - Removed CanonicalCourse: " + canonicalCourseRef);
		}
	}

	private void deleteCourseSets(String courseCode, boolean courseSetExists) {
		// Course Sets are not year driven - must check db for test
		if (!courseSetExists) {
			cmAdmin.removeCourseSet(courseCode);
			log.info("SakaiCM REST Web Service - Removed CourseSet: " + courseCode);
		}
	}

	private void deleteAcademicSession(final int year) {
		String title = MessageFormat
				.format(serverConfigurationService.getString("nwu.cm.AcademicSession.title", "Year {0,number,####}"), year);
		if (!cmService.isAcademicSessionDefined(title)) {
			cmAdmin.removeAcademicSession(title);
			log.info("SakaiCM REST Web Service - Removed AcademicSession: " + title);
		}
	}

	public static boolean isEqual(String a, String b) {
		return a == null ? b == null : a.equals(b);
	}
	
	public void setCmAdmin(CourseManagementAdministration admin) {
		cmAdmin = admin;
	}

	public void setCmService(CourseManagementService service) {
		cmService = service;
	}

	public void setAuthzGroupService(AuthzGroupService service) {
		authzGroupService = service;
	}

	public void setDeveloperHelperService(DeveloperHelperService service) {
		developerService = service;
	}
	
	public void setUserDirectoryService(UserDirectoryService userDirectoryService) {
        this.userDirectoryService = userDirectoryService;
    }

    public void setServerConfigurationService(ServerConfigurationService serverConfigurationService) {
        this.serverConfigurationService = serverConfigurationService;
    }

	public void setSessionManager(SessionManager manager) {
		sessionManager = manager;
	}
}