package edu.nwu.sakai.studentlink.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;

import edu.nwu.sakai.studentlink.resources.SakaiStudentLinkBundle;
import edu.nwu.sakai.studentlink.resources.SakaiStudentLinkMessages;
import edu.nwu.sakai.studentlink.shared.LoginException;

public class LoginScreen extends Composite implements KeyDownHandler {

    private TextBox txtLogin = new TextBox();

    private PasswordTextBox txtPassword = new PasswordTextBox();

    private FlexTable headerLayout = new FlexTable();

    private SakaiStudentLinkServiceAsync sakaiStudentLinkService = GWT
            .create(SakaiStudentLinkService.class);

    private DialogBox waitDialog = new DialogBox();

    private Button btnLogin = new Button(SakaiStudentLinkMessages.INSTANCE.login());

    private Label lblError = new Label();

    public LoginScreen() {
        initHeader();
        FlexTable mainGrid = new FlexTable();
        FlexCellFormatter mainGridFormatter = mainGrid.getFlexCellFormatter();
        mainGrid.setHeight("100%");
        mainGrid.setWidth("100%");
        mainGridFormatter.setHorizontalAlignment(1, 0, HasHorizontalAlignment.ALIGN_CENTER);
        mainGridFormatter.setVerticalAlignment(1, 0, HasVerticalAlignment.ALIGN_MIDDLE);
        FlexTable grid = new FlexTable();
        grid.setWidget(1, 0, new Label(SakaiStudentLinkMessages.INSTANCE.userName()));
        grid.setWidget(1, 1, txtLogin);
        txtLogin.setText("");
        addEnterKeyPressedListener(txtLogin);
        grid.setWidget(2, 0, new Label(SakaiStudentLinkMessages.INSTANCE.password()));
        grid.setWidget(2, 1, txtPassword);
        txtPassword.setText("");
        addEnterKeyPressedListener(txtPassword);
        grid.setWidget(3, 1, btnLogin);
        grid.setWidget(4, 1, lblError);
        mainGrid.setWidget(0, 0, headerLayout);
        mainGrid.setWidget(1, 0, grid);
        btnLogin.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                checkLogin(txtLogin.getText(), txtPassword.getText());
            }
        });
        waitDialog.setText(SakaiStudentLinkMessages.INSTANCE.connecting());
        Panel waitWrapper = new FlowPanel();
        waitWrapper.addStyleName(SakaiStudentLinkBundle.INSTANCE.css().centerImg());
        Image waitImg = new Image(SakaiStudentLinkBundle.INSTANCE.waitImg());
        waitWrapper.add(waitImg);
        waitDialog.setWidget(waitWrapper);
        waitDialog.setGlassEnabled(true);
        waitDialog.setAnimationEnabled(true);
        waitDialog.setModal(true);
        initWidget(mainGrid);
    }

    private void addEnterKeyPressedListener(FocusWidget focusWidget) {
        focusWidget.addKeyPressHandler(new KeyPressHandler() {

            public void onKeyPress(KeyPressEvent event) {
                if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
                    btnLogin.click();
                }
            }
        });
    }

    private void initHeader() {
        FlexCellFormatter headerCellFormatter = headerLayout.getFlexCellFormatter();
        headerCellFormatter.setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_CENTER);
        headerCellFormatter.setWidth(0, 0, "100%");
        headerLayout.setHTML(0, 0, "<h2>"
                + SakaiStudentLinkMessages.INSTANCE.sakaiCourseLink()
                + "</h2>");
        headerLayout.addStyleName(SakaiStudentLinkBundle.INSTANCE.css().headerTable());
    }

    /**
     * This method is called when the button is clicked
     */
    private void checkLogin(String userName, String password) {
        if (sakaiStudentLinkService == null) {
            sakaiStudentLinkService = GWT.create(SakaiStudentLinkService.class);
        }
        // Set up the call back object.
        AsyncCallback<User> callback = new AsyncCallback<User>() {

            public void onFailure(Throwable caught) {
                if (caught instanceof LoginException) {
                    lblError.setText(caught.getMessage()
                            + " "
                            + SakaiStudentLinkMessages.INSTANCE.pleaseTryAgain());
                }
                else {
                    lblError.setText(SakaiStudentLinkMessages.INSTANCE.errorsOccurred()
                            + caught.getMessage());
                }
                waitDialog.hide();
            }

            public void onSuccess(User result) {
                if (result.isValid()) {
                    SakaiStudentLinkGWT.get().setUser(result);
                    SakaiStudentLinkGWT.get().loadUserScreen();
                    lblError.setText("");
                }
                else {
                    lblError.setText(SakaiStudentLinkMessages.INSTANCE.invalidLogin());
                }
                waitDialog.hide();
            }
        };
        waitDialog.showRelativeTo(btnLogin);
        //		waitDialog.center();
        User loginAttempt = new User();
        loginAttempt.setUserName(txtLogin.getText());
        loginAttempt.setPassword(txtPassword.getText());
        sakaiStudentLinkService.validateLogin(loginAttempt, callback);
    }

    public void clearPanel() {
        lblError.setText("");
        txtLogin.setText("");
        txtPassword.setText("");
    }

    public void onKeyDown(KeyDownEvent event) {
        if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
            btnLogin.click();
        }
    }
}