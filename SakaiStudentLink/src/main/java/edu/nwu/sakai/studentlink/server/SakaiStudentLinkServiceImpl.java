package edu.nwu.sakai.studentlink.server;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.nwu.sakai.studentlink.client.IntegrationException;
import edu.nwu.sakai.studentlink.client.ModuleOffering;
import edu.nwu.sakai.studentlink.client.SakaiStudentLinkService;
import edu.nwu.sakai.studentlink.client.SearchCriteria;
import edu.nwu.sakai.studentlink.client.User;

@SuppressWarnings("serial")
public class SakaiStudentLinkServiceImpl extends RemoteServiceServlet
        implements SakaiStudentLinkService {

    private static final Logger log = Logger.getLogger(SakaiStudentLinkServiceImpl.class);
    
    public List<ModuleOffering> searchModules(Map<SearchCriteria, String> criteria)
            throws IntegrationException {
        List<ModuleOffering> modules = null;
        try {
            ModuleLink moduleLink = ModuleLink.getInstance();

            String methodOfDeliveryCode = criteria.get(SearchCriteria.METHOD_OF_DEL);
            String presentationCategoryCode = criteria.get(SearchCriteria.PRESENT_CAT);
            
            List<ModuleOffering> linkedModules = moduleLink.getAllModulesLinkedToLecturer(criteria);
            ModuleSearch searchModule = ModuleSearch.getInstance();
            modules = searchModule.findSearchModule(criteria);
            for (ModuleOffering module : modules) {
                if (linkedModules.contains(module) && (methodOfDeliveryCode == null ? true : methodOfDeliveryCode.equals(module.getMethodOfDeliveryCode())) 
                		&& (presentationCategoryCode == null ? true : presentationCategoryCode.equals(module.getPresentationCategoryCode()))
                		&& StringUtils.isNotBlank(module.getLinkedByLecturer())) {
                    module.setLinkedToLecturer(true);
                } else {
                    module.setLinkedToLecturer(false);
                }
            }
        }
        catch (IntegrationException e) {
            log.error("Could not successfully search for modules.", e);
            throw e;
        }
        return modules;
    }

    public User validateLogin(User login) throws Exception {
        User user = null;
    	LDAPAuthenticator auth = null;        
        try {
            user = new User();
            if ("1".equals(SettingsProperties.getProperty("ldap.ignore.all", "0"))) {
                user.setUserName(login.getUserName());
                user.setFirstName(login.getUserName());
                user.setSurname("Surname");
                user.setEmail("dummy@email.com");
                user.setValid(true);
                user.setAdminUser(true);
                return user;
            }
			auth = new LDAPAuthenticator();
			auth.configure(SettingsProperties.getSettingProperties());
            if ("1".equals(SettingsProperties.getProperty("ldap.ignore.password", "0"))) {
                user.setValid(true);
            }
            else {
                user.setValid(auth.isValidLogon(login.getUserName(), login.getPassword()));
            }
			String userNames = SettingsProperties
					.getProperty("admin.users", "");
			if (StringUtils.isNotBlank(userNames)) {
				for (String userName : userNames.split(",")) {
					if (StringUtils.isNotBlank(userName) && userName.equals(login.getUserName())) {					
						user.setAdminUser(true);
						break;
					}
				}
			}            
            user.setUserName(login.getUserName());
            user.setFirstName(auth.getUserAttribute(login.getUserName(),
                SettingsProperties.getProperty("ldap.firstname", "initials")));
            user.setSurname(auth.getUserAttribute(login.getUserName(),
                SettingsProperties.getProperty("ldap.surname", "sn")));
            user.setEmail(auth.getUserAttribute(login.getUserName(),
                SettingsProperties.getProperty("ldap.email", "mail")));
        }
        catch (Exception e) {
            log.error("Could not validate the user login attempt.", e);
            throw e;
        }
        finally {
            if (auth != null && auth.getBaseCtx() != null) {
                auth.getBaseCtx().close();
            }
        }
        return user;
    }

    public void linkInstructorToModules(List<ModuleOffering> modules,
            Map<SearchCriteria, String> criteria) {
        ModuleLink.getInstance().linkInstructorToModules(modules, criteria);
    }

    public void unlinkInstructorFromModules(List<ModuleOffering> modules,
            Map<SearchCriteria, String> criteria) {
        ModuleLink.getInstance().unlinkInstructorFromModules(modules, criteria);
    }

	public User becomeUser(String userName) throws Exception {
		LDAPAuthenticator auth = null;
		try {
			auth = new LDAPAuthenticator();
			auth.configure(SettingsProperties.getSettingProperties());
			return auth.getPopulatedUser(userName);
		} catch (Exception e) {
			//Become user username entered, not valid / not found
			return null;
		} finally {
			if (auth != null && auth.getBaseCtx() != null) {
				auth.getBaseCtx().close();
			}
		}
	}
}