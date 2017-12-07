package za.ac.nwu.jobs;

import java.util.Calendar;
import java.util.Set;

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
import org.sakaiproject.coursemanagement.api.exception.IdNotFoundException;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.user.api.UserDirectoryService;

import za.ac.nwu.model.Lecturer;
import za.ac.nwu.model.Module;
import za.ac.nwu.sql.ConnectionManager;
import za.ac.nwu.sql.DataManager;

public class RemoveLecturersJob implements Job {

    private static final Log log = LogFactory.getLog(RemoveLecturersJob.class);

    private static final String CLASS_NAME = "RemoveLecturersJob";

    private SessionManager sessionManager;

    private SecurityService securityService;

    private CourseManagementAdministration cmAdmin;

    private CourseManagementService cmService;

    private UserDirectoryService userDirectoryService;

    private ServerConfigurationService serverConfigurationService;

    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info("Start the Remove Lecturers Job");
        if (Utility.isSuperUser(sessionManager, securityService, CLASS_NAME)) {
            Calendar cal = Calendar.getInstance();
            int year = serverConfigurationService.getInt("nwu.cm.lecturer.year", 0);
            year = year != 0 ? year : cal.get(Calendar.YEAR);
            final ConnectionManager connectionManager = new ConnectionManager(
                    serverConfigurationService);
            final DataManager dataManager = new DataManager(connectionManager);
            final Set<Module> modules = dataManager.getAllCMModulesForLecturerRemove(year);
            String membershipStatus = serverConfigurationService.getString(
                "nwu.cm.CourseOffering.Membership.status", "Active");
            String lecturerRole = serverConfigurationService.getString(
                "nwu.cm.CourseOffering.lecturer.role", "I");
            //removeCourseSetMemberships
            removeCourseSetMemberships(modules);
            //removeCourseOfferingMemberships
            removeCourseOfferingMemberships(modules, membershipStatus, lecturerRole);
            //removeSectionMemberships
            removeSectionMemberships(modules, membershipStatus, lecturerRole);
        }
        log.info("Remove Lecturers Job has finished successfully.");
    }

    private void removeCourseSetMemberships(final Set<Module> modules) {
        for (String courseSetId : Utility.getCourseSetIds(modules)) {
            //remove CourseSet memberships
            for (String lecturerUserName : Utility.getLecturersLinkedToCourseSet(courseSetId,
                modules)) {
                try {
                    Set<Membership> courseSetMemberships = cmService
                            .getCourseSetMemberships(courseSetId);
                    log.info("Retrieved CourseSet Memberships with id " + courseSetId);
                    for (Membership membership : courseSetMemberships) {
                        if (membership.getUserId() != null
                                && membership.getUserId().equals(lecturerUserName)) {
                            cmAdmin.removeCourseSetMembership(lecturerUserName, courseSetId);
                        }
                    }
                }
                catch (IdNotFoundException e) {
                    log.warn(this
                            + ":removeCourseSetMemberships: cannot find CourseSet Memberships with id = "
                            + courseSetId);
                }
            }
        }
    }

    private void removeCourseOfferingMemberships(final Set<Module> modules,
            String membershipStatus, String lecturerRole) {
        for (Module module : modules) {
            for (Lecturer lecturer : module.getLinkedLecturers()) {
                try {
                    Set<Membership> courseOfferingMemberships = cmService
                            .getCourseOfferingMemberships(module.getCourseOfferingReference());
                    log.info("Retrieved CourseOffering Memberships with id "
                            + module.getCourseOfferingReference());
                    for (Membership membership : courseOfferingMemberships) {
                        if (membership.getUserId() != null
                                && membership.getUserId().equals(lecturer.getUserName())) {
                            cmAdmin.removeCourseOfferingMembership(lecturer.getUserName(),
                                module.getCourseOfferingReference());
                        }
                    }
                }
                catch (IdNotFoundException e) {
                    log.warn(this
                            + ":removeCourseOfferingMemberships: cannot find CourseOffering Memberships with id = "
                            + module.getCourseOfferingReference());
                }
            }
        }
    }

    private void removeSectionMemberships(final Set<Module> modules, String membershipStatus,
            String lecturerRole) {
        for (Module module : modules) {
            for (Lecturer lecturer : module.getLinkedLecturers()) {
                try {
                    Set<Membership> sectionMemberships = cmService.getSectionMemberships(module
                            .getCourseOfferingReference());
                    log.info("Retrieved Section Memberships with id "
                            + module.getCourseOfferingReference());
                    for (Membership membership : sectionMemberships) {
                        if (membership.getUserId() != null
                                && membership.getUserId().equals(lecturer.getUserName())) {
                            cmAdmin.removeSectionMembership(lecturer.getUserName(),
                                module.getCourseOfferingReference());
                        }
                    }
                }
                catch (IdNotFoundException e) {
                    log.warn(this
                            + ":removeSectionMemberships: cannot find Section Memberships with id = "
                            + module.getCourseOfferingReference());
                }
            }
        }
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
