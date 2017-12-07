package edu.nwu.sakai.studentlink.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;

import edu.nwu.sakai.studentlink.resources.SakaiStudentLinkBundle;
import edu.nwu.sakai.studentlink.resources.SakaiStudentLinkMessages;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class SakaiStudentLinkGWT implements EntryPoint {

    private final DockLayoutPanel dockLayoutPanel = new DockLayoutPanel(Unit.EM);

    private final LoginScreen loginScreen = new LoginScreen();

    private SearchModulePanel searchModule;

    private HeaderPanel headerPanel = null;

    private User user;

    private final SakaiStudentLinkServiceAsync sakaiStudentLinkService = GWT
            .create(SakaiStudentLinkService.class);

    private final List<ModuleOffering> linkedModules = new ArrayList<ModuleOffering>();

    private final List<ModuleOffering> unlinkedModules = new ArrayList<ModuleOffering>();

    /**
     * Get a cell value from a record.
     * 
     * @param <C>
     *            the cell type
     */
    private static interface GetValue<C> {

        C getValue(ModuleOffering module);
    }

    private static SakaiStudentLinkGWT singleton;

    public static SakaiStudentLinkGWT get() {
        return singleton;
    }

    /**
     * This is the entry point method.
     */
    public void onModuleLoad() {
        SakaiStudentLinkBundle.INSTANCE.css().ensureInjected();
        singleton = this;
        createJQueryMaskDefinitions();
        searchModule = new SearchModulePanel();
        RootPanel.get().add(loginScreen);
    }

    private void createJQueryMaskDefinitions() {
        JQueryMaskUtility.createMaskDefinition("~", "%*A-Za-z");
        JQueryMaskUtility.createMaskDefinition("#", "%*0-9");
    }

    public void loadUserScreen() {
        RootPanel.get().clear();
        RootLayoutPanel.get().clear();
        searchModule.clearPanel();
        dockLayoutPanel.clear();
        headerPanel = new HeaderPanel(getUser().isAdminUser());
        dockLayoutPanel.addNorth(headerPanel, 10);
        dockLayoutPanel.add(searchModule);
        RootLayoutPanel.get().add(dockLayoutPanel);
        RootPanel.get().add(RootLayoutPanel.get());//this line is needed if performing 2+ logins during the same session
        headerPanel.setUserName(user.getFirstName() + " " + user.getSurname());
    }

    void saveAndExit() {
        //Save
        if (!linkedModules.isEmpty()) {
            linkInstructorToModules();
        }
        if (!unlinkedModules.isEmpty()) {
            unlinkInstructorFromModules();
        }
        //Exit
        RootPanel.get().clear();
        RootLayoutPanel.get().clear();
        loginScreen.clearPanel();
        RootPanel.get().add(loginScreen);
    }

    public void initModuleResults(CellTable<ModuleOffering> cellTable) {

        // CheckboxCell.
        EnabledCheckboxCell linkedToInstrChbx = new EnabledCheckboxCell();
        addColumn(linkedToInstrChbx, SakaiStudentLinkMessages.INSTANCE.linkedToInstructor(),
            new GetValue<Boolean>() {

                public Boolean getValue(ModuleOffering module) {
                    return module.isLinkedToLecturer();
                }
            }, new FieldUpdater<ModuleOffering, Boolean>() {

                public void update(int index, ModuleOffering module, Boolean value) {
                    //The check box is ON
                    if (value) {
                        if (module.isLinkedToLecturer()) {
                            unlinkedModules.remove(module);
                        }
                        else {
                            linkedModules.add(module);
                        }
                    }
                    //The check box is OFF
                    else {
                        if (module.isLinkedToLecturer()) {
                            unlinkedModules.add(module);
                        }
                        else {
                            linkedModules.remove(module);
                        }
                    }
                }
            }, cellTable);
        TextColumn<ModuleOffering> linkedByLecturerColumn = new TextColumn<ModuleOffering>() {

            public String getValue(ModuleOffering module) {
                return module.getLinkedByLecturer();
            }
        };
        cellTable.addColumn(linkedByLecturerColumn,
            SakaiStudentLinkMessages.INSTANCE.linkedByLecturer());
        
        TextColumn<ModuleOffering> campusColumn = new TextColumn<ModuleOffering>() {

            public String getValue(ModuleOffering module) {
                return module.getModulePresentingEngCampusName();
            }
        };
        TextColumn<ModuleOffering> moduleColumn = new TextColumn<ModuleOffering>() {

            public String getValue(ModuleOffering module) {
                StringBuilder sb = new StringBuilder(module.getModuleSubjectCode());
                sb.append(" ");
                sb.append(module.getModuleNumber());
                return sb.toString();
            }
        };
        cellTable.addColumn(campusColumn, SakaiStudentLinkMessages.INSTANCE.campus(""));
        cellTable.addColumn(moduleColumn, SakaiStudentLinkMessages.INSTANCE.module(""));
        
        TextColumn<ModuleOffering> methodOfDeliveryColumn = new TextColumn<ModuleOffering>() {

            public String getValue(ModuleOffering module) {
                return module.getMethodOfDeliveryName();
            }
        };
        cellTable.addColumn(methodOfDeliveryColumn, SakaiStudentLinkMessages.INSTANCE.methodOfDelivery(""));
        TextColumn<ModuleOffering> presentationCategoryColumn = new TextColumn<ModuleOffering>() {

            public String getValue(ModuleOffering module) {
                return module.getPresentationCategoryName();
            }
        };
        cellTable.addColumn(presentationCategoryColumn, SakaiStudentLinkMessages.INSTANCE.presentationCategory(""));        
        cellTable.setRowData(0, new ArrayList<ModuleOffering>());
        cellTable.setRowCount(0);
        cellTable.setPageSize(15);
        cellTable.setWidth("100%");
        //cellTable.addStyleName("moduleResult");
    }

    private <C> void addColumn(final Cell<C> cell, String headerText, final GetValue<C> getter,
            FieldUpdater<ModuleOffering, C> fieldUpdater, CellTable<ModuleOffering> cellTable) {
        Column<ModuleOffering, C> column = new Column<ModuleOffering, C>(cell) {

            @Override
            public C getValue(ModuleOffering object) {
                if (cell instanceof EnabledCheckboxCell) {
                    EnabledCheckboxCell enabledCheckboxCell = (EnabledCheckboxCell) cell;
                    if (object.getLinkedByLecturer() == null
                            || "".equals(object.getLinkedByLecturer())
                            || user.getUserName().equals(object.getLinkedByLecturer())) {
                        enabledCheckboxCell.setDisabled(false);
                    }
                    else {
                        enabledCheckboxCell.setDisabled(true);
                    }
                }
                return getter.getValue(object);
            }
        };
        column.setFieldUpdater(fieldUpdater);
        cellTable.addColumn(column, headerText);
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    private void linkInstructorToModules() {
        AsyncCallback<Void> callBack = new AsyncCallback<Void>() {

            public void onFailure(Throwable caught) {
                MessageBox msg = new MessageBox(SakaiStudentLinkMessages.INSTANCE.error(),
                        caught.getMessage());
                msg.show();
            }

            public void onSuccess(Void result) {
                linkedModules.clear();
                //for (ModuleOffering module : modules) {
                //    module.setLinkedToLecturer(true);
                //}
            }
        };
        sakaiStudentLinkService.linkInstructorToModules(linkedModules,
            searchModule.buildModuleSearchCriteria(), callBack);
    }

    private void unlinkInstructorFromModules() {
        AsyncCallback<Void> callBack = new AsyncCallback<Void>() {

            public void onFailure(Throwable caught) {
                MessageBox msg = new MessageBox(SakaiStudentLinkMessages.INSTANCE.error(),
                        caught.getMessage());
                msg.show();
            }

            public void onSuccess(Void result) {
                unlinkedModules.clear();
            }
        };
        sakaiStudentLinkService.unlinkInstructorFromModules(unlinkedModules,
            searchModule.buildModuleSearchCriteria(), callBack);
    }
}
