package edu.nwu.sakai.studentlink.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface SakaiStudentLinkBundle extends ClientBundle {

    public static final SakaiStudentLinkBundle INSTANCE = GWT.create(SakaiStudentLinkBundle.class);

    @Source("SakaiStudentLink.css")
    SakaiStudentLinkCSS css();

    @Source("wait.gif")
    ImageResource waitImg();
}