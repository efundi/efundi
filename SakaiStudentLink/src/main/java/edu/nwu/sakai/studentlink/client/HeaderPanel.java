package edu.nwu.sakai.studentlink.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;

import edu.nwu.sakai.studentlink.resources.SakaiStudentLinkBundle;
import edu.nwu.sakai.studentlink.resources.SakaiStudentLinkMessages;

public class HeaderPanel extends Composite {	

    private SakaiStudentLinkServiceAsync sakaiStudentLinkService = GWT
            .create(SakaiStudentLinkService.class);

    private DialogBox waitDialogPopup = new DialogBox();    

    private DialogBox invalidUserPopup = new DialogBox();
    
    private FlexTable headerLayout = new FlexTable();
    
    public HeaderPanel(boolean isAdminUser) {
        FlexCellFormatter headerCellFormatter = headerLayout.getFlexCellFormatter();

        waitDialogPopup.setText(SakaiStudentLinkMessages.INSTANCE.connecting());
        Panel waitWrapper = new FlowPanel();
        waitWrapper.addStyleName(SakaiStudentLinkBundle.INSTANCE.css().centerImg());
        Image waitImg = new Image(SakaiStudentLinkBundle.INSTANCE.waitImg());
        waitWrapper.add(waitImg);
        waitDialogPopup.setWidget(waitWrapper);
        waitDialogPopup.setGlassEnabled(true);
        waitDialogPopup.setAnimationEnabled(true);
        waitDialogPopup.setModal(true);
        
        //logged in
        headerCellFormatter.setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_RIGHT);
        headerCellFormatter.setWidth(0, 0, "100%");
        headerCellFormatter.setVerticalAlignment(0, 0, HasVerticalAlignment.ALIGN_TOP);
        headerCellFormatter.setWordWrap(0, 0, false);
        //save and exit button
        headerCellFormatter.setWidth(0, 1, "100%");
        headerCellFormatter.setVerticalAlignment(0, 1, HasVerticalAlignment.ALIGN_TOP);
        headerCellFormatter.setWordWrap(0, 1, false);
        Button saveAndExitButton = new Button(SakaiStudentLinkMessages.INSTANCE.saveAndExit());
        saveAndExitButton.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                SakaiStudentLinkGWT.get().saveAndExit();
            }
        });
        headerLayout.setWidget(0, 1, saveAndExitButton);
        
        int row = 1;        
        if(isAdminUser){

            HorizontalPanel horizontalPanel2 = new HorizontalPanel(); 
            //Become user
            headerCellFormatter.setHorizontalAlignment(row, 0, HasHorizontalAlignment.ALIGN_RIGHT);
            headerCellFormatter.setWidth(row, 0, "100%");
            headerCellFormatter.setVerticalAlignment(row, 0, HasVerticalAlignment.ALIGN_TOP);
            headerCellFormatter.setWordWrap(row, 0, false);
            Label becomeUserLbl = new Label(SakaiStudentLinkMessages.INSTANCE.user());
            horizontalPanel2.add(becomeUserLbl);
            //become user text
            final TextBox becomeUserTxt = new TextBox();      
            horizontalPanel2.add(becomeUserTxt);   
            headerLayout.setWidget(row, 0, horizontalPanel2);  
            headerCellFormatter.setHorizontalAlignment(row, 1, HasHorizontalAlignment.ALIGN_RIGHT);
            headerCellFormatter.setWidth(row, 1, "100%");
            headerCellFormatter.setVerticalAlignment(row, 1, HasVerticalAlignment.ALIGN_TOP);
            headerCellFormatter.setWordWrap(row, 1, false);
            //become user button
            Button becomeUserButton = new Button(SakaiStudentLinkMessages.INSTANCE.becomeUser());
            becomeUserButton.addClickHandler(new ClickHandler() {

                public void onClick(ClickEvent event) {
                	becomeUser(becomeUserTxt);
                }
            }); 
                
            headerLayout.setWidget(row, 1, becomeUserButton);
            row = row + 1;
        }
        
        //header
        headerCellFormatter.setHorizontalAlignment(row, 0, HasHorizontalAlignment.ALIGN_CENTER);
        headerCellFormatter.setWidth(row, 0, "100%");
        headerCellFormatter.setColSpan(row, 0, 2);
        headerLayout.setHTML(row, 0, "<h2>"
                + SakaiStudentLinkMessages.INSTANCE.sakaiCourseLink()
                + "</h2>");
        
        headerLayout.addStyleName(SakaiStudentLinkBundle.INSTANCE.css().headerTable());
        initWidget(headerLayout);
    }

    public void setUserName(String name) {
        headerLayout.setHTML(0, 0, SakaiStudentLinkMessages.INSTANCE.loggedIn() + name);
    }
    
    private void becomeUser(final TextBox becomeUserTxt){
    	 if (sakaiStudentLinkService == null) {
             sakaiStudentLinkService = GWT.create(SakaiStudentLinkService.class);
         }
         // Set up the call back object.
         AsyncCallback<User> callback = new AsyncCallback<User>() {

            public void onFailure(Throwable caught) {
                waitDialogPopup.hide();
                MessageBox msg = new MessageBox(SakaiStudentLinkMessages.INSTANCE.error(),
                        caught.getMessage());
                msg.show();
            }

			public void onSuccess(User result) {
				if(result == null){
					waitDialogPopup.hide();
					showInvalidUserPopup(becomeUserTxt.getText());
				} else {
					waitDialogPopup.hide();
                    SakaiStudentLinkGWT.get().setUser(result);
                    setUserName(result.getFirstName() + " " + result.getSurname());
                    becomeUserTxt.setText("");
				}
			}
        };
        waitDialogPopup.center();
        sakaiStudentLinkService.becomeUser(becomeUserTxt.getText(), callback);
    }

	private void showInvalidUserPopup(String text) {
        VerticalPanel verticalPanel = new VerticalPanel();
        FlexTable grid = new FlexTable();		
    	invalidUserPopup = new DialogBox();
    	invalidUserPopup.setGlassEnabled(true);
    	invalidUserPopup.setModal(true);
        Label invalidUserLbl = new Label();
        invalidUserLbl.setText(text + " " + SakaiStudentLinkMessages.INSTANCE.isInvalidUser());        
        grid.setWidget(0, 0, invalidUserLbl);    	
    	Button okButton = new Button(SakaiStudentLinkMessages.INSTANCE.okButton(), new ClickHandler() {

            public void onClick(ClickEvent event) {
            	invalidUserPopup.hide();
            }
        });    	
        grid.setWidget(1, 0, okButton);
        grid.getCellFormatter().setHorizontalAlignment(1, 0, HasAlignment.ALIGN_CENTER);        
    	verticalPanel.add(grid);
    	invalidUserPopup.add(verticalPanel);
    	invalidUserPopup.center();
    }    
}
