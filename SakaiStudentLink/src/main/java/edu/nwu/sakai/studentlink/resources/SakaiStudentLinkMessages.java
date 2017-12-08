package edu.nwu.sakai.studentlink.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Messages;

public interface SakaiStudentLinkMessages extends Messages {

    public static final SakaiStudentLinkMessages INSTANCE = GWT
            .create(SakaiStudentLinkMessages.class);

    String login();

    String userName();

    String password();

    String sakaiCourseLink();

    String pleaseTryAgain();

    String errorsOccurred();

    String invalidLogin();

    String connecting();

    String search();

    String searching();

    String searchForModules();

    String year();

    String campus(String separator);

    String selectCampus();

    String campus1();

    String campus2();

    String campus9();

    String module(@Optional String separator);
    
    String methodOfDelivery(String separator);

    String selectMethodOfDelivery();
    
    String presentationCategory(String separator);
    
    String selectPresentationCategory();

    String clear();

    String validationError();

    String campusValidation();

    String moduleValidation();

    String error();

    String saveAndExit();

    String loggedIn();

    String close();

    String name();

    String startDate();

    String endDate();

    String linkedToInstructor();

    String linkedByLecturer();
    
    String user();

    String becomeUser();

	String isInvalidUser();

	String okButton();

	String moduleSubjectCodeValidation();

	String moduleNumberValidation();
}
