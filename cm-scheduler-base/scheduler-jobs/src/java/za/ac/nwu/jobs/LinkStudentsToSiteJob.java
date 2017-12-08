package za.ac.nwu.jobs;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.sakaiproject.authz.api.AuthzGroupService;
import org.sakaiproject.authz.api.Member;
import org.sakaiproject.authz.api.SecurityService;
import org.sakaiproject.component.api.ServerConfigurationService;
import org.sakaiproject.exception.IdUnusedException;
import org.sakaiproject.exception.PermissionException;
import org.sakaiproject.site.api.Site;
import org.sakaiproject.site.api.SiteService;
import org.sakaiproject.site.api.SiteService.SelectionType;
import org.sakaiproject.site.api.SiteService.SortType;
import org.sakaiproject.tool.api.Session;
import org.sakaiproject.tool.api.SessionManager;
import org.sakaiproject.user.api.User;
import org.sakaiproject.user.api.UserDirectoryService;
import org.sakaiproject.user.api.UserNotDefinedException;

import za.ac.nwu.sql.ConnectionManager;
import za.ac.nwu.sql.DataManager;

public class LinkStudentsToSiteJob implements Job {

	private static final Log log = LogFactory
			.getLog(LinkStudentsToSiteJob.class);

	private static final String CLASS_NAME = "LinkStudentsToSiteJob";

	private UserDirectoryService userDirectoryService;
	private SiteService siteService;
	private AuthzGroupService authzGroupService;
	private SessionManager sessionManager;
    private SecurityService securityService;
	private ServerConfigurationService serverConfigurationService;
	private String iniFilePath;
	private String userRole;
	private String year;

	public User getUser(Site site, String eid) {
		User user = null;
		try {
			user = userDirectoryService.getUserByEid(eid);
		} catch (UserNotDefinedException e) {
			log.error("User with eid " + eid + " not found: " + e.toString());
		}
		return user;
	}

	@SuppressWarnings("unchecked")
	public void mapSite(String subject, String siteTitle, String campus)
			throws Exception {

		List<Site> sites = siteService.getSites(SelectionType.ANY, null,
				siteTitle, null, SortType.NONE, null);
		if (sites.size() == 0) {
			log.warn("No site with title " + siteTitle + " found.");
			return;
		}
		ConnectionManager connectionManager = new ConnectionManager(
                serverConfigurationService);
        DataManager dataManager = new DataManager(connectionManager);
        List<String> users = dataManager.getUsers(subject, campus, getYear());
        if (users.size() == 0) {
			log.warn("No students found for " + subject + ".");
			return;
		}
		for (Site site : sites) {
			if (site.getTitle().equalsIgnoreCase(siteTitle)) {
				log.debug("Found site " + site.getTitle());
				for (String usereid : users) {
					User u = getUser(site, usereid);
					if (u != null) {	
						if(isAlreadyMemberOfSite(site, usereid)){
							log.warn("User with eid " + usereid + " is already a member of Site " + site.getId());
						} else {
							site.addMember(u.getId(), userRole, true, false);
							log.info("User " + u.getEid() + " (" + u.getId()
									+ ") added to site " + siteTitle + " with "
									+ userRole + " access");
							try {
								siteService.saveSiteMembership(site);
							} catch (IdUnusedException e) {
								log.error("IdUnusedException: " + e.toString(), e);
							} catch (PermissionException e) {
								log.error("PermissionException: " + e.toString(), e);
							}
						}
					} else {
						log.warn("UserDirectory did not return a user for "
								+ usereid);
					}
				}
			}
		}
	}
	

	private boolean isAlreadyMemberOfSite(Site site, String userEid) {
		Set<Member> siteMembers = site.getMembers();
		for (Member member : siteMembers) {
			if (userEid.equals(member.getUserEid())) {
				return true;
			}
		}
		return false;
	}

	public void execute(JobExecutionContext jobExecutionContext)
			throws JobExecutionException {
		log.info(CLASS_NAME + " will now start.");
		Session session = sessionManager.getCurrentSession();
		session.clear();
		session.setUserEid(UserDirectoryService.ADMIN_EID);
		session.setUserId(UserDirectoryService.ADMIN_ID);
		session.setAttribute("LinkStudentsToSiteJob", "true");
		authzGroupService.refreshUser(UserDirectoryService.ADMIN_ID);
		Calendar cal = Calendar.getInstance();
		int year = serverConfigurationService.getInt(
					"nwu.cm.lecturer.year", 0);
		setYear(Integer.toString(year != 0 ? year : cal.get(Calendar.YEAR)));
		readIniFile();	
		session.removeAttribute("LinkStudentsToSiteJob");
		log.info(CLASS_NAME + " has finished.");
	}

	private void readIniFile() {
		log.info("Reading site list from " + iniFilePath);
		try {			
			FileReader fileReader = new FileReader(iniFilePath);
			BufferedReader br = new BufferedReader(fileReader);
			String line = br.readLine();
			while (line != null) {
				log.debug("Line: " + line);
				if (!line.startsWith("#")) {
					String[] data = line.split("\\t+");
					if (data.length >= 3) {
						String subject = data[0];
						String site = data[1];
						String campus = data[2];
						log.info("Found subject " + subject + " site "
								+ site + " campus " + campus);
						mapSite(subject, site, campus);
					}
				}
				line = br.readLine();
			}
			br.close();
			fileReader.close();

		} catch (FileNotFoundException e) {
			log.error("File not found: " + iniFilePath, e);
		} catch (IOException ioe) {
			log.error("IO Error in reading file " + iniFilePath, ioe);
		} catch (Exception ex) {
			log.error("Unhancled exception: " + ex.toString(), ex);
		}
	}

	public UserDirectoryService getUserDirectoryService() {
		return userDirectoryService;
	}

	public void setUserDirectoryService(
			UserDirectoryService userDirectoryService) {
		this.userDirectoryService = userDirectoryService;
	}

	public SiteService getSiteService() {
		return siteService;
	}

	public void setSiteService(SiteService siteService) {
		this.siteService = siteService;
	}

	public AuthzGroupService getAuthzGroupService() {
		return authzGroupService;
	}

	public void setAuthzGroupService(AuthzGroupService authzGroupService) {
		this.authzGroupService = authzGroupService;
	}

	public SessionManager getSessionManager() {
		return sessionManager;
	}

	public void setSessionManager(SessionManager sessionManager) {
		this.sessionManager = sessionManager;
	}

    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }

	public void setServerConfigurationService(
			ServerConfigurationService serverConfigurationService) {
		this.serverConfigurationService = serverConfigurationService;
	}

	public String getIniFilePath() {
		return iniFilePath;
	}

	public void setIniFilePath(String iniFilePath) {
		this.iniFilePath = iniFilePath;
	}

	public String getUserRole() {
		return userRole;
	}

	public void setUserRole(String userRole) {
		this.userRole = userRole;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}
}
