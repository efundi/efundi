package edu.nwu.sakai.studentlink.server;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import edu.nwu.sakai.studentlink.client.Campus;
import edu.nwu.sakai.studentlink.client.IntegrationError;
import edu.nwu.sakai.studentlink.client.IntegrationException;
import edu.nwu.sakai.studentlink.client.ModuleOffering;
import edu.nwu.sakai.studentlink.client.SearchCriteria;
import za.ac.nwu.wsdl.CourseOfferingTracs.AcademicPeriodInfo;
import za.ac.nwu.wsdl.CourseOfferingTracs.ContextInfo;
import za.ac.nwu.wsdl.CourseOfferingTracs.CourseOfferingTracsServiceProxy;
import za.ac.nwu.wsdl.CourseOfferingTracs.DoesNotExist;
import za.ac.nwu.wsdl.CourseOfferingTracs.InvalidParameter;
import za.ac.nwu.wsdl.CourseOfferingTracs.MissingParameter;
import za.ac.nwu.wsdl.CourseOfferingTracs.ModuleOfferingTracsInfo;
import za.ac.nwu.wsdl.CourseOfferingTracs.ModuleOfferingTracsSearchCriteriaInfo;
import za.ac.nwu.wsdl.CourseOfferingTracs.OperationFailed;
import za.ac.nwu.wsdl.CourseOfferingTracs.PermissionDenied;

public class ModuleSearch {

    private static final Logger log = Logger.getLogger(ModuleSearch.class);

    private static ModuleSearch instance;

    private ModuleSearch() {
    }

    public static ModuleSearch getInstance() {
        if (instance == null) {
            instance = new ModuleSearch();
        }
        return instance;
    }

    public List<ModuleOffering> findSearchModule(Map<SearchCriteria, String> criteria)
            throws IntegrationException {

        List<ModuleOffering> modules = new ArrayList<ModuleOffering>();
        TreeSet<ModuleOffering> moduleSet = new TreeSet<ModuleOffering>();
        Calendar calendar = Calendar.getInstance();
        
        ModuleOfferingTracsSearchCriteriaInfo searchCriteria = new ModuleOfferingTracsSearchCriteriaInfo();
        searchCriteria.setAcademicPeriod(new AcademicPeriodInfo(Integer.toString(calendar.get(Calendar.YEAR)), Integer.toString(calendar.get(Calendar.YEAR))));
        String courseCode = criteria.get(SearchCriteria.COURSE_CODE);
        if(courseCode != null){
            searchCriteria.setModuleSubjectCode(criteria.get(SearchCriteria.COURSE_CODE)); 
        }       
        String moduleNumber = criteria.get(SearchCriteria.COURSE_LEVEL) == null ? null : criteria.get(SearchCriteria.COURSE_LEVEL) + criteria.get(SearchCriteria.COURSE_MODULE);
        if(moduleNumber != null){
            searchCriteria.setModuleNumber(moduleNumber);  
        }     
        searchCriteria.setModuleSite(criteria.get(SearchCriteria.CAMPUS));        
        String methodOfDel = criteria.get(SearchCriteria.METHOD_OF_DEL) == null ? null : criteria.get(SearchCriteria.METHOD_OF_DEL);
        if(methodOfDel != null){
            searchCriteria.setMethodOfDeliveryTypeKey("vss.code.ENROLCAT." + methodOfDel);  
        }         
        String modeOfDel = criteria.get(SearchCriteria.PRESENT_CAT) == null ? null : criteria.get(SearchCriteria.PRESENT_CAT);
        if(modeOfDel != null){
            searchCriteria.setModeOfDeliveryTypeKey("vss.code.PRESENTCAT." + modeOfDel);  
        } 
        
        String webserviceUrl = SettingsProperties
                .getProperty("ws.module.url", "http://workflowprd.nwu.ac.za/student-tracs-v3/CourseOfferingTracsService/OfferingTracsService");
        
        CourseOfferingTracsServiceProxy proxy = new CourseOfferingTracsServiceProxy(webserviceUrl);   
        
		try {
			ModuleOfferingTracsInfo[] result = proxy.getModuleOfferingBySearchCriteria(searchCriteria, new ContextInfo("s1", "s1", calendar, null));
			
			for (ModuleOfferingTracsInfo moduleOfferingTracsInfo : result) {
				
				ModuleOffering moduleOffering = new ModuleOffering();
				moduleOffering.setModuleSubjectCode(moduleOfferingTracsInfo.getModuleSubjectCode());
				
				moduleOffering.setModuleNumber(moduleOfferingTracsInfo.getModuleNumber());
				moduleOffering.setModuleSite(moduleOfferingTracsInfo.getModuleSite());
				moduleOffering.setMethodOfDeliveryTypeKey(moduleOfferingTracsInfo.getMethodOfDeliveryTypeKey());
				moduleOffering.setModeOfDeliveryTypeKey(moduleOfferingTracsInfo.getModeOfDeliveryTypeKey());
				moduleOffering.setLanguageTypeKey(moduleOfferingTracsInfo.getLanguageTypeKey());			
				moduleOffering.setTermTypeKey(moduleOfferingTracsInfo.getTermTypeKey());
				moduleOffering.setStartDate(moduleOfferingTracsInfo.getModuleSubjectCode());
				moduleOffering.setEndDate(moduleOfferingTracsInfo.getModuleSubjectCode());
				
				String methodOfDelResult = moduleOfferingTracsInfo.getMethodOfDeliveryTypeKey();
	            if(methodOfDelResult != null && methodOfDelResult.length() != 0){
	            	methodOfDelResult = methodOfDelResult.replace("vss.code.ENROLCAT.", "");
	            	methodOfDelResult = "vss.code.ENROLCAT." +  methodOfDelResult.substring(0, methodOfDelResult.indexOf("."));
	            }
	            String modeOfDelResult = moduleOfferingTracsInfo.getModeOfDeliveryTypeKey();
	            if(modeOfDelResult != null && modeOfDelResult.length() != 0){
	            	modeOfDelResult = modeOfDelResult.replace("vss.code.PRESENTCAT.", "");
	            	modeOfDelResult = "vss.code.PRESENTCAT." +  modeOfDelResult.substring(0, modeOfDelResult.indexOf("."));
	            }
				
				moduleOffering.setLinkedByLecturer(getLinkedByLecturer(calendar.get(Calendar.YEAR), 
						moduleOfferingTracsInfo.getModuleSubjectCode(), moduleOfferingTracsInfo.getModuleNumber().substring(0, 1), moduleOfferingTracsInfo.getModuleNumber().substring(1, 3),
						Campus.getCampus(moduleOfferingTracsInfo.getModuleSite()).getNumber(), methodOfDelResult, modeOfDelResult));
                moduleSet.add(moduleOffering);
			}

		} catch (InvalidParameter e) {
			log.error("OfferingTracsService - InvalidParameter: ", e);

			IntegrationException ie = new IntegrationException("Could not retrieve the modules ", e);
			IntegrationError error = new IntegrationError();
			error.setErrorMessage(e.getFaultString());
			ie.addError(error);
			throw ie;
		} catch (DoesNotExist e) {
			log.error("OfferingTracsService - DoesNotExist: ", e);

			IntegrationException ie = new IntegrationException("Could not retrieve the modules ", e);
			IntegrationError error = new IntegrationError();
			error.setErrorMessage(e.getFaultString());
			ie.addError(error);
			throw ie;
		} catch (OperationFailed e) {
			log.error("OfferingTracsService - OperationFailed: ", e);

			IntegrationException ie = new IntegrationException("Could not retrieve the modules ", e);
			IntegrationError error = new IntegrationError();
			error.setErrorMessage(e.getFaultString());
			ie.addError(error);
			throw ie;
		} catch (MissingParameter e) {
			log.error("OfferingTracsService - MissingParameter: ", e);

			IntegrationException ie = new IntegrationException("Could not retrieve the modules ", e);
			IntegrationError error = new IntegrationError();
			error.setErrorMessage(e.getFaultString());
			ie.addError(error);
			throw ie;
		} catch (PermissionDenied e) {
			log.error("OfferingTracsService - PermissionDenied: ", e);

			IntegrationException ie = new IntegrationException("Could not retrieve the modules ", e);
			IntegrationError error = new IntegrationError();
			error.setErrorMessage(e.getFaultString());
			ie.addError(error);
			throw ie;
		} catch (RemoteException e) {
			log.error("OfferingTracsService - RemoteException: ", e);

			IntegrationException ie = new IntegrationException("Could not retrieve the modules ", e);
			IntegrationError error = new IntegrationError();
			error.setErrorMessage(e.getMessage());
			ie.addError(error);
			throw ie;
		}
       
        modules.addAll(moduleSet);
        return modules;
    }

    private String getLinkedByLecturer(int year, String courseCode, String courseLevel, 
            String courseModule, Integer campus, String methodOfDeliveryCode, String presentationCategoryCode) {
        log.info("Performing ModuleSearch.getLinkedByLecturer ");
        PreparedStatement pstmt = null;
        Connection conn = null;
        ResultSet rset = null;
        String lecturerName = "";
        try {
            conn = ConnectionManager.getCourseManagementConnection();
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT l.username ");
            sql.append("FROM CM_MODULES m, ");
            sql.append("CM_LECTURER l, ");
            sql.append("CM_YEAR_CAMPUS c ");
            sql.append("WHERE  ");
            sql.append("m.lecturer_f_id = l.lecturer_id ");
            sql.append("AND l.year_campus_f_id = c.year_campus_id ");
            sql.append("AND c.year = ? ");
            sql.append("AND m.course_code = ? ");
            sql.append("AND m.course_level = ? ");
            sql.append("AND m.course_module = ? ");
            if (campus.intValue() != 0) {
                sql.append("AND c.campus_code = ? ");
            }
            sql.append("and m.method_of_del = ? ");
            sql.append("and m.present_cat = ?");
            pstmt = conn.prepareStatement(sql.toString());
            int cnt = 1;
            pstmt.setInt(cnt++, year);
            pstmt.setString(cnt++, courseCode);
            pstmt.setString(cnt++, courseLevel);
            pstmt.setString(cnt++, courseModule);
            if (campus.intValue() != 0) {
                pstmt.setInt(cnt++, campus.intValue());
            }
            pstmt.setString(cnt++, methodOfDeliveryCode);
            pstmt.setString(cnt++, presentationCategoryCode);
            rset = pstmt.executeQuery();
            while (rset.next()) {
                lecturerName = rset.getString(1);
                break;
            }
        }
        catch (ConnectionNotEstablishedException e) {
            log.error(
                "A SQL Connection could not be established while performing findSearchModuleJDBC",
                e);
        }
        catch (SQLException e) {
            log.error("SQL error occured when performing ModuleSearch.getLinkedByLecturer", e);
        }
        finally {
            try {
                ConnectionManager.close(rset, pstmt, conn);
            }
            catch (Exception e) {
                log.error("Error closing connection", e);
            }
        }
        return lecturerName;
    }
}