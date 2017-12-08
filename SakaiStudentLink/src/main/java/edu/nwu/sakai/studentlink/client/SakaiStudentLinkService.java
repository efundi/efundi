package edu.nwu.sakai.studentlink.client;

import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("link")
public interface SakaiStudentLinkService extends RemoteService {

    public List<ModuleOffering> searchModules(Map<SearchCriteria, String> criteria)
            throws IntegrationException;

    public User validateLogin(User login) throws Exception;

    public void linkInstructorToModules(List<ModuleOffering> modules,
            Map<SearchCriteria, String> criteria);

    public void unlinkInstructorFromModules(List<ModuleOffering> module,
            Map<SearchCriteria, String> criteria);

    public User becomeUser(String userName) throws Exception;

}
