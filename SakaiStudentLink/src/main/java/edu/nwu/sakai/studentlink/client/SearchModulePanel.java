package edu.nwu.sakai.studentlink.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;

import edu.nwu.sakai.studentlink.resources.SakaiStudentLinkBundle;
import edu.nwu.sakai.studentlink.resources.SakaiStudentLinkMessages;

public class SearchModulePanel extends Composite {

    private FlexTable searchLayout = new FlexTable();

    private SakaiStudentLinkServiceAsync sakaiStudentLinkService = GWT
            .create(SakaiStudentLinkService.class);

    private DialogBox waitDialog = new DialogBox();

    private Button butSearchModule = new Button(SakaiStudentLinkMessages.INSTANCE.search());

    private ScrollPanel scroller = new ScrollPanel();

    private CellTable<ModuleOffering> moduleResults = new CellTable<ModuleOffering>();

    private SimplePager pager;

    private TextBox moduleBox, yearBox;

    private ListBox campusListBox;
    private ListBox methodOfDeliveryListBox;
    private ListBox presentationCategoryListBox;

    private static final String MODULEBOX_ELEMENT_ID = "moduleBox";

    private static final String MODULEBOX_MASK = "~?~~~ # ##";

    public SearchModulePanel() {
        waitDialog.setText(SakaiStudentLinkMessages.INSTANCE.searching());
        Panel waitWrapper = new FlowPanel();
        waitWrapper.addStyleName(SakaiStudentLinkBundle.INSTANCE.css().centerImg());
        Image waitImg = new Image(SakaiStudentLinkBundle.INSTANCE.waitImg());
        waitWrapper.add(waitImg);
        waitDialog.setWidget(waitWrapper);
        waitDialog.setGlassEnabled(true);
        waitDialog.setAnimationEnabled(true);
        waitDialog.setModal(true);
        searchLayout.setCellSpacing(10);
        searchLayout.setWidth("100%");
        // Add a title to the form
        FlexCellFormatter cellFormatter = searchLayout.getFlexCellFormatter();
        cellFormatter.setColSpan(0, 0, 10);
        cellFormatter.setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_CENTER);
        cellFormatter.setColSpan(3, 0, 10);
        cellFormatter.setHorizontalAlignment(3, 0, HasHorizontalAlignment.ALIGN_CENTER);
        cellFormatter.setColSpan(4, 0, 10);
        cellFormatter.setHorizontalAlignment(4, 0, HasHorizontalAlignment.ALIGN_CENTER);
        cellFormatter.setColSpan(5, 0, 10);
        cellFormatter.setHorizontalAlignment(5, 0, HasHorizontalAlignment.ALIGN_CENTER);
        // Add some standard form options
        searchLayout.setHTML(0, 0, "<h1>"
                + SakaiStudentLinkMessages.INSTANCE.searchForModules()
                + "</h1>");
        searchLayout.setHTML(1, 0, SakaiStudentLinkMessages.INSTANCE.year());
        yearBox = new TextBox();
        Date currentDate = new Date();
        @SuppressWarnings("deprecation")
        int currentYear = currentDate.getYear() + 1900;
        yearBox.setText(String.valueOf(currentYear));
        yearBox.setReadOnly(true);
        searchLayout.setWidget(1, 1, yearBox);
        searchLayout.setHTML(1, 2, SakaiStudentLinkMessages.INSTANCE.campus(":"));
        campusListBox = new ListBox();
        campusListBox.addItem(SakaiStudentLinkMessages.INSTANCE.selectCampus(), "0");
        campusListBox.addItem(SakaiStudentLinkMessages.INSTANCE.campus1(), "1");
        campusListBox.addItem(SakaiStudentLinkMessages.INSTANCE.campus2(), "2");
        campusListBox.addItem(SakaiStudentLinkMessages.INSTANCE.campus9(), "9");
        searchLayout.setWidget(1, 3, campusListBox);
        searchLayout.setHTML(1, 4, SakaiStudentLinkMessages.INSTANCE.module(":"));
        moduleBox = new TextBox();
        moduleBox.getElement().setId(MODULEBOX_ELEMENT_ID);
        moduleBox.addBlurHandler(new BlurHandler() {

            public void onBlur(BlurEvent event) {
                String value = moduleBox.getValue();
                String wildCardValue = value.replace('_', '*');
                moduleBox.setValue(wildCardValue);
            }
        });
        searchLayout.setWidget(1, 5, moduleBox);
        searchLayout.setHTML(1, 6, SakaiStudentLinkMessages.INSTANCE.methodOfDelivery(":"));
        methodOfDeliveryListBox = new ListBox();
        methodOfDeliveryListBox.addItem(SakaiStudentLinkMessages.INSTANCE.selectMethodOfDelivery(), "0");
        methodOfDeliveryListBox.setEnabled(false); 
        searchLayout.setWidget(1, 7, methodOfDeliveryListBox);
        searchLayout.setHTML(1, 8, SakaiStudentLinkMessages.INSTANCE.presentationCategory(":"));
        presentationCategoryListBox = new ListBox();
        presentationCategoryListBox.addItem(SakaiStudentLinkMessages.INSTANCE.selectPresentationCategory(), "0");
        presentationCategoryListBox.setEnabled(false);   
        searchLayout.setWidget(1, 9, presentationCategoryListBox);        
        HorizontalPanel buttonPanel = new HorizontalPanel();
        buttonPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        buttonPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
        buttonPanel.setSpacing(10);
        Button clearButton = new Button(SakaiStudentLinkMessages.INSTANCE.clear());
        clearButton.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                clearPanel();
            }
        });
        butSearchModule.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                String validationMsg = getValidationErrorMsg();
                if ("".equals(validationMsg)) {
                    searchModules();
                }
                else {
                    MessageBox validationDialog = new MessageBox(SakaiStudentLinkMessages.INSTANCE
                            .validationError(), validationMsg);
                    validationDialog.setGlassEnabled(true);
                    validationDialog.setAnimationEnabled(true);
                    validationDialog.setModal(true);
                    validationDialog.showRelativeTo(butSearchModule);
                }
            }
        });
        buttonPanel.add(butSearchModule);
        buttonPanel.add(clearButton);
        searchLayout.setWidget(3, 0, buttonPanel);
        VerticalPanel resultPanel = new VerticalPanel();
        SakaiStudentLinkGWT.get().initModuleResults(moduleResults);
        moduleResults.addStyleName(SakaiStudentLinkBundle.INSTANCE.css().cellTable());
        SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
        pager = new SimplePager(TextLocation.CENTER, pagerResources, false, 0, true);
        pager.setDisplay(moduleResults);
        pager.setWidth("100%");
        pager.addStyleName(SakaiStudentLinkBundle.INSTANCE.css().cellTable());
        resultPanel.add(pager);
        resultPanel.add(moduleResults);
        resultPanel.setWidth("100%");
        resultPanel.setHeight("100%");
        searchLayout.setWidget(4, 0, resultPanel);
        scroller = new ScrollPanel(searchLayout);
        scroller.setWidth("100%");
        scroller.setHeight("100%");
        scroller.addStyleName(SakaiStudentLinkBundle.INSTANCE.css().menuTable());
        initWidget(scroller);
    }

    private String getValidationErrorMsg() {
        String campus = campusListBox.getValue(campusListBox.getSelectedIndex());
        if ("0".equals(campus)) {
            return SakaiStudentLinkMessages.INSTANCE.campusValidation();
        }
        String moduleValue = moduleBox.getValue();
        if (!"".equals(moduleValue)) {
//            return SakaiStudentLinkMessages.INSTANCE.moduleValidation();
            if(moduleValue.substring(0, 4).contains("*")) {
        		return SakaiStudentLinkMessages.INSTANCE.moduleSubjectCodeValidation();
        	} else if(!moduleValue.substring(5, 6).equals("*") && moduleValue.substring(7).contains("*")) {
        		return SakaiStudentLinkMessages.INSTANCE.moduleNumberValidation();
        	}
        }
        return "";
    }

    public void searchModules() {
        if (sakaiStudentLinkService == null) {
            sakaiStudentLinkService = GWT.create(SakaiStudentLinkService.class);
        }
        // Set up the call back object.
        AsyncCallback<List<ModuleOffering>> callback = new AsyncCallback<List<ModuleOffering>>() {

            public void onFailure(Throwable caught) {
                if (caught instanceof IntegrationException) {
                    String message = SakaiStudentLinkMessages.INSTANCE.errorsOccurred();
                    for (IntegrationError error : ((IntegrationException) caught).getErrors()) {
                        message = message.concat("\n" + error.getErrorMessage());
                    }
                    waitDialog.hide();
                    MessageBox msg = new MessageBox(SakaiStudentLinkMessages.INSTANCE.error(),
                            message);
                    msg.showRelativeTo(butSearchModule);
                }
                else {
                    waitDialog.hide();
                    MessageBox msg = new MessageBox(SakaiStudentLinkMessages.INSTANCE.error(),
                            caught.getMessage());
                    msg.showRelativeTo(butSearchModule);
                }
            }

            public void onSuccess(List<ModuleOffering> result) {
                populateModuleResults(result);
            }
        };
        waitDialog.showRelativeTo(butSearchModule);
        sakaiStudentLinkService.searchModules(buildModuleSearchCriteria(), callback);
    }

    public HashMap<SearchCriteria, String> buildModuleSearchCriteria() {
        HashMap<SearchCriteria, String> criteria = new HashMap<SearchCriteria, String>();
        final String moduleValue = moduleBox.getValue();
        assert moduleValue.length() == 9 : "Module search value length is not correct for parsing purposes";
        
        if(!moduleValue.substring(0, 4).contains("*")){
            criteria.put(SearchCriteria.COURSE_CODE, moduleValue.substring(0, 4).toUpperCase());
            
            if(!moduleValue.substring(5).contains("*")){

            	criteria.put(SearchCriteria.COURSE_LEVEL, moduleValue.substring(5, 6));
                criteria.put(SearchCriteria.COURSE_MODULE, moduleValue.substring(7));            	
            }
        }
        
        criteria.put(SearchCriteria.YEAR, yearBox.getText());
        criteria.put(SearchCriteria.CAMPUS,
            campusListBox.getValue(campusListBox.getSelectedIndex()));
        
        String methodOfDelivery = methodOfDeliveryListBox.getValue(methodOfDeliveryListBox.getSelectedIndex());
        if (!"0".equals(methodOfDelivery)) {
            criteria.put(SearchCriteria.METHOD_OF_DEL,
            		methodOfDeliveryListBox.getValue(methodOfDeliveryListBox.getSelectedIndex()));	
        } 
        String presentationCategory = presentationCategoryListBox.getValue(presentationCategoryListBox.getSelectedIndex());
        if (!"0".equals(presentationCategory)) {
        	criteria.put(SearchCriteria.PRESENT_CAT,
            		presentationCategoryListBox.getValue(presentationCategoryListBox.getSelectedIndex()));
        }
        criteria.put(SearchCriteria.USER_NAME, SakaiStudentLinkGWT.get().getUser().getUserName());
        return criteria;
    }

    public void populateModuleResults(List<ModuleOffering> result) {
        moduleResults.setRowCount(result.size(), true);
        ListDataProvider<ModuleOffering> listViewAdapter = new ListDataProvider<ModuleOffering>();
        listViewAdapter.setList(result);
        listViewAdapter.addDataDisplay(moduleResults);        
        populateMethodOfDeliveryList(result);
        populatePresentationCategoryList(result);        
        // Push the data into the widget.
        moduleResults.setRowData(0, result);
        waitDialog.hide();
    }

	private void populateMethodOfDeliveryList(List<ModuleOffering> result) {		
		int selectedIndex = methodOfDeliveryListBox.getSelectedIndex();
		String selectedMethodOfDelivery = methodOfDeliveryListBox.getValue(selectedIndex);		
		methodOfDeliveryListBox.clear();
        methodOfDeliveryListBox.addItem(SakaiStudentLinkMessages.INSTANCE.selectMethodOfDelivery(), "0");
		Map<String, String> uniqueResultMap = new HashMap<String, String>();
		if(!result.isEmpty()){
			methodOfDeliveryListBox.setEnabled(true);
		} else {
			methodOfDeliveryListBox.setEnabled(false);
		}
		for (ModuleOffering moduleDetail : result) {   
			uniqueResultMap.put(moduleDetail.getMethodOfDeliveryCode(), moduleDetail.getMethodOfDeliveryName());
		}
		for (Map.Entry<String, String> entry : uniqueResultMap.entrySet()){
			methodOfDeliveryListBox.addItem(entry.getValue(), entry.getKey());
			if(entry.getKey().equals(selectedMethodOfDelivery)){
				methodOfDeliveryListBox.setSelectedIndex(selectedIndex);
			}
		}
	}

    private void populatePresentationCategoryList(List<ModuleOffering> result) {	
		int selectedIndex = presentationCategoryListBox.getSelectedIndex();
		String selectedPresentationCategory = presentationCategoryListBox.getValue(selectedIndex);		
    	presentationCategoryListBox.clear();
        presentationCategoryListBox.addItem(SakaiStudentLinkMessages.INSTANCE.selectPresentationCategory(), "0");
		Map<String, String> uniqueResultMap = new HashMap<String, String>();
		if(!result.isEmpty()){
			presentationCategoryListBox.setEnabled(true);
		} else {
			presentationCategoryListBox.setEnabled(false);
		}
		for (ModuleOffering moduleDetail : result) {   
			uniqueResultMap.put(moduleDetail.getPresentationCategoryCode(), moduleDetail.getPresentationCategoryName());
		}
		for (Map.Entry<String, String> entry : uniqueResultMap.entrySet()){
			presentationCategoryListBox.addItem(entry.getValue(), entry.getKey());	
			if(entry.getKey().equals(selectedPresentationCategory)){
				presentationCategoryListBox.setSelectedIndex(selectedIndex);
			}
		}
	}
    
    public void clearPanel() {
        for (int row = 0; row < searchLayout.getRowCount(); row++) {
            for (int col = 0; col < searchLayout.getCellCount(row); col++) {
                Widget widget = searchLayout.getWidget(row, col);
                if (widget instanceof TextBox
                        && !((TextBox) widget).isReadOnly()
                        && ((TextBox) widget).isEnabled()) {
                    ((TextBox) widget).setText("");
                }
                else if (widget instanceof ListBox) {
                    ((ListBox) widget).setSelectedIndex(0);
                }
            }
        }
        moduleResults.setRowData(0, new ArrayList<ModuleOffering>());
        moduleResults.setRowCount(0);
    }

    @Override
    protected void onLoad() {
        JQueryMaskUtility.mask(MODULEBOX_ELEMENT_ID, MODULEBOX_MASK);
        campusListBox.setFocus(true);
    }
}
