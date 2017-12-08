package za.ac.nwu.jobs;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.component.api.ServerConfigurationService;
import org.sakaiproject.coursemanagement.api.AcademicSession;
import org.sakaiproject.coursemanagement.api.CourseManagementAdministration;
import org.sakaiproject.coursemanagement.api.CourseManagementService;
import org.sakaiproject.coursemanagement.api.CourseOffering;
import org.sakaiproject.coursemanagement.api.SectionCategory;
import org.sakaiproject.coursemanagement.api.exception.IdNotFoundException;
import org.sakaiproject.user.api.UserDirectoryService;

import za.ac.nwu.ldap.LDAPRetrieval;
import za.ac.nwu.model.Lecturer;
import za.ac.nwu.model.Module;
import za.ac.nwu.model.Status;
import za.ac.nwu.model.Student;
import za.ac.nwu.sql.DataManager;

public class InsertProcess {

    private static final Log LOG = LogFactory.getLog(InsertProcess.class);

    private DataManager dataManager;

    private CourseManagementAdministration cmAdmin;

    private CourseManagementService cmService;

    private UserDirectoryService userDirectoryService;

    private ServerConfigurationService serverConfigurationService;

    public InsertProcess(final DataManager dataManager,
            final CourseManagementAdministration cmAdmin, final CourseManagementService cmService,
            final UserDirectoryService userDirectoryService,
            final ServerConfigurationService serverConfigurationService) {
        this.dataManager = dataManager;
        this.cmAdmin = cmAdmin;
        this.cmService = cmService;
        this.userDirectoryService = userDirectoryService;
        this.serverConfigurationService = serverConfigurationService;
    }

    public void insert(final int year) {
        final Set<Module> modules = dataManager.getModules(year, true, Status.INSERTED);
        final AcademicSession academicSession = createAcademicSession(year);
        createCourseSets(modules);
        createCanonicalCourses(Utility.getCanonicalCourseIds(modules));
        createCourseOfferingRelatedData(academicSession, modules);
        //Insert/Update students/lecturers
        if (serverConfigurationService.getBoolean("nwu.cm.users.create", false)) {
            LDAPRetrieval ldap = Utility.getLDAPRetrieval(serverConfigurationService);
            try {
                final Set<Lecturer> lecturers = Utility.getAllLecturers(modules);
                if (lecturers != null && !lecturers.isEmpty()) {
                    ldap.setLecturerDetails(lecturers);
                    createLecturerSakaiUsers(lecturers);
                }
                final Set<Student> students = Utility.getAllStudents(modules);
                if (students != null && !students.isEmpty()) {
                    ldap.setStudentDetails(students);
                    createStudentSakaiUsers(students);
                }
            }
            catch (Exception e) {
                LOG.error("Could not create Sakai Users for Course Management. See previous log entries for more details.");
            }
            finally {
                try {
                    ldap.getContext().close();
                }
                catch (NamingException e) {
                    LOG.warn("Error closing LDAPRetrieval Context", e);
                }
            }
        }
        //Update CM Link records
        dataManager.updateInsertedDataStatus(year);
    }

    /**
     * Create the AcademicSession for the year and set it active/current
     */
    private AcademicSession createAcademicSession(final int year) {
        Calendar start = Calendar.getInstance();
        start.set(year, Calendar.JANUARY, 1);
        Calendar end = Calendar.getInstance();
        end.set(year, Calendar.DECEMBER, 31);
        String title = MessageFormat.format(serverConfigurationService.getString(
            "nwu.cm.AcademicSession.title", "Year {0,number,####}"), year);
        String description = MessageFormat.format(serverConfigurationService.getString(
            "nwu.cm.AcademicSession.description", "Academic Session for Year {0,number,####}"),
            year);
        AcademicSession academicSession = null;
        try {
            academicSession = cmService.getAcademicSession(title);
            LOG.info("Retrieved AcademicSession with id " + title);
        }
        catch (IdNotFoundException e) {
            //If AcademicSession do not exist, create it.
            academicSession = cmAdmin.createAcademicSession(title, title, description,
                start.getTime(), end.getTime());
            LOG.info("Inserted AcademicSession with id " + title);
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
     * Create Course Sets for all the subject codes.
     * These are shown in the 'Subject' droplist of Sakai's course site setup.
     */
    private void createCourseSets(final Set<Module> modules) {
        String category = serverConfigurationService.getString("nwu.cm.CourseSet.category",
            "category");
        for (String courseSetId : Utility.getCourseSetIds(modules)) {
            if (!cmService.isCourseSetDefined(courseSetId)) {
                cmAdmin.createCourseSet(courseSetId, courseSetId, courseSetId, category, null);
                LOG.info("Inserted CourseSet with id " + courseSetId);
            }
        }
    }

    /**
     * These abstract records are not shown on screen.
     */
    private void createCanonicalCourses(final Set<String> canonicalCourseIds) {
        for (String canonicalCourseId : canonicalCourseIds) {
            if (!cmService.isCanonicalCourseDefined(canonicalCourseId)) {
                cmAdmin.createCanonicalCourse(canonicalCourseId, canonicalCourseId,
                    canonicalCourseId);
                LOG.info("Inserted CanonicalCourse with id " + canonicalCourseId);
            }
        }
    }

    /**
     * The Course Offering records are shown in the 'Course' droplist of Sakai's course site setup.
     */
    private void createCourseOfferingRelatedData(final AcademicSession academicSession,
            final Set<Module> modules) {
        String status = serverConfigurationService.getString("nwu.cm.CourseOffering.status",
            "Active");
        String enrollmentSetCategory = serverConfigurationService.getString(
            "nwu.cm.EnrollmentSet.category", "category");
        String enrollmentSetCredits = serverConfigurationService.getString(
            "nwu.cm.EnrollmentSet.credits", "0");
        String enrollmentStatus = serverConfigurationService.getString("nwu.cm.Enrollment.status",
            "enrolled");
        String enrollmentCredits = serverConfigurationService.getString(
            "nwu.cm.Enrollment.credits", "0");
        String gradingScheme = serverConfigurationService.getString(
            "nwu.cm.Enrollment.gradingScheme", "standard");
        String sectionCategoryCode = createSectionCategory();
        String sectionStatus = serverConfigurationService.getString(
            "nwu.cm.Section.Membership.status", "Active");
        String sectionLecturerRole = serverConfigurationService.getString(
            "nwu.cm.Section.lecturer.role", "I");
        String sectionStudentRole = serverConfigurationService.getString(
            "nwu.cm.Section.student.role", "S");
        for (Module module : modules) {
            if (!cmService.isCourseOfferingDefined(module.getCourseOfferingReference())) {
                //Give Canonical Course's Eid as title for Course Offering
                cmAdmin.createCourseOffering(module.getCourseOfferingReference(),
                    module.getCanonicalCourseReference(), module.getCourseOfferingReference(),
                    status, academicSession.getEid(), module.getCanonicalCourseReference(),
                    academicSession.getStartDate(), academicSession.getEndDate());
                LOG.info("Inserted CourseOffering with id " + module.getCourseOfferingReference());
            }
            addCourseOfferingsToCourseSets(module.getCourseOfferingReference(),
                module.getCourseCode());
            //Add EnrollmentSets
            createEnrollmentSets(module, enrollmentSetCategory, enrollmentSetCredits);
            //Add Enrollments
            createEnrollment(module.getEnrollmentSetReference(), module, enrollmentStatus,
                enrollmentCredits, gradingScheme);
            //Add Sections
            createSections(module, sectionCategoryCode, sectionStatus, sectionLecturerRole,
                sectionStudentRole);
        }
    }

    private void addCourseOfferingsToCourseSets(final String courseOfferingId,
            final String courseSetId) {
        boolean linkExists = false;
        //Check if Offering is already linked to CourseSet
        Set<CourseOffering> linkedOfferings = cmService.getCourseOfferingsInCourseSet(courseSetId);
        for (CourseOffering linkedOffering : linkedOfferings) {
            if (Utility.equals(linkedOffering.getEid(), courseOfferingId)) {
                LOG.info("CourseOffering with id '"
                        + courseOfferingId
                        + "' is already linked to CourseSet with id '"
                        + courseSetId
                        + "'.");
                linkExists = true;
                break;
            }
        }
        if (!linkExists) {
            //Add if Offering is not already linked.
            cmAdmin.addCourseOfferingToCourseSet(courseSetId, courseOfferingId);
            LOG.info("Added CourseOffering ("
                    + courseOfferingId
                    + ") to CourseSet ("
                    + courseSetId
                    + ")");
        }
    }

    /**
     * Arbitrary section category title.
     */
    private String createSectionCategory() {
        String sectionCategoryCode = null;
        String category = serverConfigurationService.getString("nwu.cm.SectionCategory.category",
            "LCT");
        String catDesc = serverConfigurationService.getString("nwu.cm.SectionCategory.description",
            "Lecture");
        boolean exists = false;
        for (String categoryCode : cmService.getSectionCategories()) {
            if (category.equals(categoryCode)) {
                if (catDesc.equals(cmService.getSectionCategoryDescription(category))) {
                    sectionCategoryCode = categoryCode;
                    LOG.info("Section Category '" + category + "' already exists.");
                    exists = true;
                    break;
                }
            }
        }
        if (!exists) {
            SectionCategory sectionCategory = cmAdmin.addSectionCategory(category, catDesc);
            sectionCategoryCode = sectionCategory.getCategoryCode();
            LOG.info("Section Category (" + category + " - " + catDesc + ") successfully inserted.");
        }
        return sectionCategoryCode;
    }

    /**
     * Creates the EnrollmentSets per CourseOffering/Class Group.
     */
    private void createEnrollmentSets(final Module module, final String enrollmentSetCategory,
            final String enrollmentSetCredits) {
        if (!cmService.isEnrollmentSetDefined(module.getEnrollmentSetReference())) {
            cmAdmin.createEnrollmentSet(module.getEnrollmentSetReference(),
                module.getEnrollmentSetReference(), module.getEnrollmentSetReference(),
                enrollmentSetCategory, enrollmentSetCredits, module.getCourseOfferingReference(),
                Utility.getLecturerUserNames(module));
            LOG.info("Inserted EnrollmentSet with id " + module.getEnrollmentSetReference());
        }
    }

    /**
     * Only students should have Enrollments. Lecturers are added to the EnrollmentSet.
     */
    private void createEnrollment(final String enrollmentSetId, final Module module,
            final String enrollmentStatus, final String enrollmentCredits,
            final String gradingScheme) {
        for (String studentUserName : Utility.getStudentUserNames(module)) {
            cmAdmin.addOrUpdateEnrollment(studentUserName, enrollmentSetId, enrollmentStatus,
                enrollmentCredits, gradingScheme);
            LOG.info("Added/Updated Enrollment for user id " + studentUserName);
        }
    }

    /**
     * The Section's description is displayed (with checkbox) on the Course/Section Information screen of Sakai's course site setup.
     */
    private void createSections(final Module module, final String sectionCategoryCode,
            final String sectionStatus, final String sectionLecturerRole,
            final String sectionStudentRole) {
        //Section's make use of the same eids as CourseOfferings.
        if (!cmService.isSectionDefined(module.getCourseOfferingReference())) {
            cmAdmin.createSection(module.getCourseOfferingReference(),
                module.getCourseOfferingReference(), module.getCourseOfferingReference()
                        + " Lecture", sectionCategoryCode, null,
                module.getCourseOfferingReference(), module.getEnrollmentSetReference());
            LOG.info("Inserted Section with id " + module.getCourseOfferingReference());
        }
        //Add Section Memberships
        createSectionMemberships(module, sectionStatus, sectionLecturerRole, sectionStudentRole);
    }

    private void createSectionMemberships(final Module module, final String sectionStatus,
            final String sectionLecturerRole, final String sectionStudentRole) {
        //Students
        for (Student student : module.getLinkedStudents()) {
            cmAdmin.addOrUpdateSectionMembership(student.getUserName(), sectionStudentRole,
                module.getCourseOfferingReference(), sectionStatus);
            LOG.info("Added/Updated SectionMembership - "
                    + student.getUserName()
                    + " to "
                    + module.getCourseOfferingReference());
        }
    }

    private void createLecturerSakaiUsers(Set<Lecturer> lecturers) {
        try {
            for (Lecturer lecturer : lecturers) {
                String lecturerUserId = null;
                try {
                    lecturerUserId = userDirectoryService.getUserId(lecturer.getUserName());
                }
                catch (Exception e1) {
                    lecturerUserId = "";
                }
                boolean lecturerExists = lecturerUserId != null && !lecturerUserId.equals("");
                if (lecturerExists) {
                    LOG.info("Lecturer " + lecturer.getUserName() + " already exists.");
                }
                else {
                    userDirectoryService.addUser(null, lecturer.getUserName(),
                        lecturer.getFirstName(), lecturer.getSurname(), lecturer.getEmail(),
                        lecturer.getPassword(), "maintain", null);
                    LOG.info("Added user " + lecturer.getUserName());
                }
            }
        }
        catch (Exception e2) {
            LOG.error("CourseMgmtPopulationJob - createLecturerSakaiUsers - Exception occured: ",
                e2);
        }
    }

    private void createStudentSakaiUsers(Set<Student> students) {
        try {
            for (Student student : students) {
                String studentUserId = null;
                try {
                    studentUserId = userDirectoryService.getUserId(student.getUserName());
                }
                catch (Exception e) {
                    studentUserId = "";
                }
                boolean studentExists = studentUserId != null && !studentUserId.equals("");
                if (studentExists) {
                    LOG.info("Student " + student.getUserName() + " already exists.");
                }
                else {
                    userDirectoryService.addUser(null, student.getUserName(),
                        student.getFirstName(), student.getSurname(), student.getEmail(),
                        student.getPassword(), "student", null);
                    LOG.info("Added user " + student.getUserName());
                }
            }
        }
        catch (Exception e2) {
            LOG.error("CourseMgmtPopulationJob - createStudentSakaiUsers - Exception occured: ", e2);
        }
    }
}