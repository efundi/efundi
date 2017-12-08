package za.ac.nwu.jobs;

import java.util.Calendar;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.sakaiproject.authz.api.SecurityService;
import org.sakaiproject.component.api.ServerConfigurationService;
import org.sakaiproject.coursemanagement.api.CourseManagementAdministration;
import org.sakaiproject.coursemanagement.api.CourseManagementService;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.user.api.UserDirectoryService;

import za.ac.nwu.model.Status;
import za.ac.nwu.sql.ConnectionManager;
import za.ac.nwu.sql.DataManager;

/**
 * This is a quartz job that will populate NWU's Sakai Course Management data.
 */
public class CourseMgmtPopulationJob implements Job {

    private static final Log LOG = LogFactory.getLog(CourseMgmtPopulationJob.class);

    private static final String CLASS_NAME = "CourseMgmtPopulationJob";

    private SessionManager sessionManager;

    private SecurityService securityService;

    private CourseManagementAdministration cmAdmin;

    private CourseManagementService cmService;

    private UserDirectoryService userDirectoryService;

    private ServerConfigurationService serverConfigurationService;

    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        LOG.info("CourseMgmtPopulationJob will now start.");
        if (Utility.isSuperUser(sessionManager, securityService, CLASS_NAME)) {
            Calendar cal = Calendar.getInstance();
            int year = serverConfigurationService.getInt("nwu.cm.lecturer.year", 0);
            year = year != 0 ? year : cal.get(Calendar.YEAR);
            final ConnectionManager connectionManager = new ConnectionManager(
                    serverConfigurationService);
            final DataManager dataManager = new DataManager(connectionManager);
            final Map<Status, Integer> statusMap = dataManager.getNumOfLinkedModulesPerStatus(year);
            if (statusMap.get(Status.INSERTED) > 0) {
                InsertProcess insertProcess = new InsertProcess(dataManager, cmAdmin, cmService,
                        userDirectoryService, serverConfigurationService);
                insertProcess.insert(year);
            }
            if (statusMap.get(Status.DELETED) > 0) {
                DeleteProcess deleteProcess = new DeleteProcess(dataManager, cmAdmin, cmService,
                        serverConfigurationService);
                deleteProcess.delete(year);
            }
        }
        LOG.info("CourseMgmtPopulationJob has finished successfully.");
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