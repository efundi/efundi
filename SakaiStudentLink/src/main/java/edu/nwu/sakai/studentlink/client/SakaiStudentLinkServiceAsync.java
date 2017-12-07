package edu.nwu.sakai.studentlink.client;

import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface SakaiStudentLinkServiceAsync {

    void searchModules(Map<SearchCriteria, String> criteria,
            AsyncCallback<List<ModuleOffering>> callback);

    void validateLogin(User login, AsyncCallback<User> callback);

    void linkInstructorToModules(List<ModuleOffering> modules, Map<SearchCriteria, String> criteria,
            AsyncCallback<Void> callback);

    void unlinkInstructorFromModules(List<ModuleOffering> modules,
            Map<SearchCriteria, String> criteria, AsyncCallback<Void> callback);

	void becomeUser(String userName, AsyncCallback<User> callback);
}
