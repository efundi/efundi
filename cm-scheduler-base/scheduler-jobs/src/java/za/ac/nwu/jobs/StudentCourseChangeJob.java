package za.ac.nwu.jobs;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import javax.naming.NamingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.sakaiproject.authz.api.SecurityService;
import org.sakaiproject.component.api.ServerConfigurationService;
import org.sakaiproject.coursemanagement.api.CourseManagementAdministration;
import org.sakaiproject.coursemanagement.api.CourseManagementService;
import org.sakaiproject.coursemanagement.api.Membership;
import org.sakaiproject.coursemanagement.api.SectionCategory;
import org.sakaiproject.coursemanagement.api.exception.IdNotFoundException;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.user.api.UserDirectoryService;

import za.ac.nwu.ldap.LDAPRetrieval;
import za.ac.nwu.model.Module;
import za.ac.nwu.model.Student;
import za.ac.nwu.sql.ConnectionManager;
import za.ac.nwu.sql.DataManager;

public class StudentCourseChangeJob implements Job {

    private static final Log log = LogFactory.getLog(StudentCourseChangeJob.class);

    private static final String CLASS_NAME = "StudentCourseChangeJob";

    private SessionManager sessionManager;

    private SecurityService securityService;

    private CourseManagementAdministration cmAdmin;

    private CourseManagementService cmService;

    private UserDirectoryService userDirectoryService;

    private ServerConfigurationService serverConfigurationService;

    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info("Start the Student Course Change Job");
        if (Utility.isSuperUser(sessionManager, securityService, CLASS_NAME)) {
            Calendar cal = Calendar.getInstance();
            int year = serverConfigurationService.getInt("nwu.cm.lecturer.year", 0);
            year = year != 0 ? year : cal.get(Calendar.YEAR);
            String enrollmentStatus = serverConfigurationService.getString(
                "nwu.cm.Enrollment.status", "enrolled");
            String enrollmentCredits = serverConfigurationService.getString(
                "nwu.cm.Enrollment.credits", "0");
            String gradingScheme = serverConfigurationService.getString(
                "nwu.cm.Enrollment.gradingScheme", "standard");
            String sectionStatus = serverConfigurationService.getString(
                "nwu.cm.Section.Membership.status", "Active");
            String sectionStudentRole = serverConfigurationService.getString(
                "nwu.cm.Section.student.role", "S");
            final ConnectionManager connectionManager = new ConnectionManager(
                    serverConfigurationService);
            final DataManager dataManager = new DataManager(connectionManager);
            Set<Module> modules = dataManager.getAllCMModules(year);
            addNewStudents(modules, enrollmentStatus, enrollmentCredits, gradingScheme,
                sectionStatus, sectionStudentRole);
            removeOldStudents(modules, sectionStatus, sectionStudentRole);
        }
        log.info("Student Course Change Job has finished successfully.");
    }

    private void removeOldStudents(Set<Module> modules, String sectionStatus,
            String sectionStudentRole) {
        boolean foundStudent = false;
        for (Module module : modules) {
            try {
                Set<Membership> memberships = cmService.getSectionMemberships(module
                        .getCourseOfferingReference());
                log.info("Retrieved Section Memberships with id "
                        + module.getCourseOfferingReference());
                for (Membership membership : memberships) {
                    for (Student student : module.getLinkedStudents()) {
                        if (student.getUserName().equals(membership.getUserId())
                                && membership.getRole().equals(sectionStudentRole)
                                && membership.getStatus().equals(sectionStatus)) {
                            foundStudent = true;
                            break;
                        }
                    }
                    if (!foundStudent) {
                        //Section Memberships
                        cmAdmin.removeSectionMembership(membership.getUserId(),
                            module.getCourseOfferingReference());
                        log.info("Removed Student Membership from Section: "
                                + membership.getUserId()
                                + " - "
                                + module.getCourseOfferingReference());
                        //Enrollment
                        cmAdmin.removeEnrollment(membership.getUserId(),
                            module.getEnrollmentSetReference());
                        log.info("Removed Student from Enrollment: "
                                + membership.getUserId()
                                + " - "
                                + module.getEnrollmentSetReference());
                    }
                    foundStudent = false;
                }
            }
            catch (IdNotFoundException e) {
                log.warn(this
                        + ":removeOldStudents: cannot find Section Memberships with id = "
                        + module.getCourseOfferingReference());
            }
        }
    }

    private void addNewStudents(Set<Module> modules, String enrollmentStatus,
            String enrollmentCredits, String gradingScheme, String sectionStatus,
            String sectionStudentRole) {
        Set<Student> newStudents = new HashSet<Student>();
        boolean foundStudent = false;
        for (Module module : modules) {
            newStudents.clear();
            for (Student student : module.getLinkedStudents()) {
                try {
                    Set<Membership> memberships = cmService.getSectionMemberships(module
                            .getCourseOfferingReference());
                    for (Membership membership : memberships) {
                        if (student.getUserName().equals(membership.getUserId())
                                && membership.getRole().equals(sectionStudentRole)
                                && membership.getStatus().equals(sectionStatus)) {
                            foundStudent = true;
                            break;
                        }
                    }
                    if (!foundStudent) {
                        newStudents.add(student);
                        //Enrollment
                        cmAdmin.addOrUpdateEnrollment(student.getUserName(),
                            module.getEnrollmentSetReference(), enrollmentStatus,
                            enrollmentCredits, gradingScheme);
                        log.info("Added/Updated Enrollment for user id " + student.getUserName());
                        //Section Memberships
                        cmAdmin.addOrUpdateSectionMembership(student.getUserName(),
                            sectionStudentRole, module.getCourseOfferingReference(), sectionStatus);
                        log.info("Added/Updated SectionMembership - "
                                + student.getUserName()
                                + " to "
                                + module.getCourseOfferingReference());
                    }
                    foundStudent = false;
                }
                catch (IdNotFoundException e) {
                    log.warn(this
                            + ":addNewStudents: cannot find Section Memberships with id = "
                            + module.getCourseOfferingReference());
                }
            }
            if (module.getLinkedStudents() != null && !module.getLinkedStudents().isEmpty()) {
                if (serverConfigurationService.getBoolean("nwu.cm.users.create", false)) {
                    LDAPRetrieval ldap = Utility.getLDAPRetrieval(serverConfigurationService);
                    try {
                        if (newStudents != null && !newStudents.isEmpty()) {
                            ldap.setStudentDetails(newStudents);
                            createStudentSakaiUsers(newStudents);
                        }
                    }
                    catch (Exception e) {
                        log.error("Could not create Sakai Users for Course Management. See previous log entries for more details.");
                    }
                    finally {
                        try {
                            ldap.getContext().close();
                        }
                        catch (NamingException e) {
                            log.warn("Error closing LDAPRetrieval Context", e);
                        }
                    }
                }
            }
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
                    log.info("Section Category '" + category + "' already exists.");
                    exists = true;
                    break;
                }
            }
        }
        if (!exists) {
            SectionCategory sectionCategory = cmAdmin.addSectionCategory(category, catDesc);
            sectionCategoryCode = sectionCategory.getCategoryCode();
            log.info("Section Category (" + category + " - " + catDesc + ") successfully inserted.");
        }
        return sectionCategoryCode;
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
                    log.info("Student " + student.getUserName() + " already exists.");
                }
                else {
                    userDirectoryService.addUser(null, student.getUserName(),
                        student.getFirstName(), student.getSurname(), student.getEmail(),
                        student.getPassword(), "student", null);
                    log.info("Added user " + student.getUserName());
                }
            }
        }
        catch (Exception e2) {
            log.error("StudentCourseChangeJob - createStudentSakaiUsers - Exception occured: ", e2);
        }
    }

    private boolean isCourseSite(Site site) {
        if (serverConfigurationService.getString("courseSiteType", "course").equals(site.getType())) {
            return true;
        }
        return false;
    }

    public void setSessionManager(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }

    public void setCmAdmin(CourseManagementAdministration cmAdmin) {
        this.cmAdmin = cmAdmin;
    }

    public void setCmService(CourseManagementService cmService) {
        this.cmService = cmService;
    }

    public void setUserDirectoryService(UserDirectoryService userDirectoryService) {
        this.userDirectoryService = userDirectoryService;
    }

    public void setServerConfigurationService(ServerConfigurationService serverConfigurationService) {
        this.serverConfigurationService = serverConfigurationService;
    }
}
