package edu.nwu.sakai.studentlink.server;

import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;

import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringUtils;
import org.apache.cxf.helpers.IOUtils;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.log4j.Logger;

import edu.nwu.sakai.studentlink.client.ModuleOffering;
import za.ac.nwu.wsdl.studentacademicregistration.AcademicPeriodInfo;
import za.ac.nwu.wsdl.studentacademicregistration.ModuleOfferingSearchCriteriaInfo;
import za.ac.nwu.wsdl.studentacademicregistration.StudentAcademicRegistrationService;
import za.ac.nwu.wsdl.studentacademicregistration.StudentacademicregistrationserviceClientEp;

public class CourseManagementWSHandler {

    private static CourseManagementWSHandler instance;

    private CourseManagementWSHandler() {
    }

    public static CourseManagementWSHandler getInstance() {
        if (instance == null) {
            instance = new CourseManagementWSHandler();
        }
        return instance;
    }

    private static final Logger log = Logger.getLogger(CourseManagementWSHandler.class);

    private static final String SAKAI_WEB_SERVICE_SUCCESS = "success";

    /**
     * Insert the Sakai Course Management data through a web service call.
     * @param year The year for which to insert the Course Management data.
     * @param module The module for which to insert the Course Management data.
     * @param lecturerUserName The lecturer linked to the specified module.
     */
    public void insertSakaiCMData(int year, ModuleOffering module, String lecturerUserName) {
        try {
            log.info("Get the current Sakai session with Sakai Web Services");
            String sessionId = createSakaiSession();
            if(StringUtils.isEmpty(sessionId)){
            	 log.error("An error occurred while creating a Sakai admin session Id");
            	 return;
            }
            log.info("Insert Course Management data with Sakai Web Services");
            String wsResult = callInsertCMDataWS(sessionId, year, module.getModuleSubjectCode(),
                module.getCanonicalCourseReference(), module.getCourseOfferingReference(year),
                module.getEnrollmentSetReference(year), lecturerUserName,
                getDelimitedStudentUserNames(year, module));
            if (SAKAI_WEB_SERVICE_SUCCESS.equals(wsResult)) {
                updateInsertedDataStatus(module, year, lecturerUserName);
            } else {
           	 	log.error("An error occurred while creating Sakai CM data. West service call did not return successful.");
            }
        }
        catch (Exception e) {
            log.error("An error occurred while inserting the Sakai CM data using the Sakai REST Web service", e);
        }
    }

    /**
     * Perform Sakai authentication and return the session id.
     * @return The active Sakai session id.
     * @throws Exception
     */
    private String createSakaiSession() throws Exception {
        String username = SettingsProperties.getProperty("sakai.user");
        String password = SettingsProperties.getProperty("sakai.password");

		WebClient client = WebClient.create(SettingsProperties.getProperty("sakai.rest.address") + "/session");
		client = client.accept("text/xml");
		client.query("_username", username);
		client.query("_password", password);

		Response response = client.post(null);

		int statusCode = response.getStatus();
		if (statusCode == 201) { // CREATED

			String value = IOUtils.toString((InputStream) response.getEntity());
			return value;
		}
		return null;
    }

    /**
     * Insert the Sakai Course Management Data.
     * @param sessionId The active Sakai session id
     * @param courseCode The course code of the module (Example: AFNL)
     * @param canonicalCourseRef The canonical course reference (Example: AFNL 111)
     * @param courseOfferingRef The course offering reference (Example: AFNL 111 P 2011)
     * @param enrollmentSetRef The enrollment set reference (Example: AFNL 111 P 2011 ES)
     * @param lecturerUserName The lecturer name linked to this module.
     * @param studentUserNames A list of student user names linked to this module.
     * @return The result string. Only when the string equals "success" has the call been successful.
     * @throws Exception
     */
    private String callInsertCMDataWS(String sessionId, int year, String courseCode,
            String canonicalCourseRef, String courseOfferingRef, String enrollmentSetRef,
            String lecturerUserName, String studentUserNames) throws Exception {
		WebClient client = WebClient.create(SettingsProperties.getProperty("sakai.rest.address") + "/nwu-cm/insert-cm-data");
		client = client.accept("application/json").type("application/json");
		client.query("sakai.session", sessionId);
		client.query("year", "" + year);
		client.query("courseCode", courseCode);
		client.query("canonicalCourseRef", canonicalCourseRef);
		client.query("courseOfferingRef", courseOfferingRef);
		client.query("enrollmentSetRef", enrollmentSetRef);
		client.query("lecturerUserName", lecturerUserName);
		client.query("studentUserNames", studentUserNames);
		Response response = client.post(null);
		int statusCode = response.getStatus();
		String results = null;
		if (statusCode == 200) {
			results = IOUtils.toString((InputStream) response.getEntity());
		}
        return results;
    }

    /**
     * Returns a comma delimited list of all the students linked to the specified module. (per year)
     */
    private String getDelimitedStudentUserNames(int year, ModuleOffering moduleDetail) throws Exception {
    	
        StringBuilder students = new StringBuilder();
        Calendar calendar = Calendar.getInstance();
        
        ModuleOfferingSearchCriteriaInfo searchCriteria = new ModuleOfferingSearchCriteriaInfo();

        AcademicPeriodInfo academicPeriodInfo = new AcademicPeriodInfo();
        academicPeriodInfo.setAcadPeriodtTypeKey("vss.code.AcademicPeriod.Year");
        academicPeriodInfo.setAcadPeriodValue(Integer.toString(calendar.get(Calendar.YEAR)));
        
        searchCriteria.setAcademicPeriod(academicPeriodInfo);
        searchCriteria.setModuleSubjectCode(moduleDetail.getModuleSubjectCode().toUpperCase());
        searchCriteria.setModuleNumber(moduleDetail.getModuleNumber());       
        searchCriteria.setModuleSite(moduleDetail.getModuleSite());
        searchCriteria.setMethodOfDeliveryTypeKey(moduleDetail.getMethodOfDeliveryCodeParam());   
        searchCriteria.setModeOfDeliveryTypeKey(moduleDetail.getModeOfDeliveryCodeParam());  

        String webserviceUrl = SettingsProperties
                .getProperty("ws.student.url", "http://v-soatst-ap-lnx1.nwu.ac.za:8001/soa-infra/services/default/StudentAcademicRegistrationByModuleOffering/StudentAcademicRegistration?wsdl");
        StudentacademicregistrationserviceClientEp clientService = new StudentacademicregistrationserviceClientEp(new URL(webserviceUrl));
        StudentAcademicRegistrationService service = clientService.getStudentAcademicRegistrationServicePt();
        List<String> studentUserNames = service.getStudentAcademicRegistrationByModuleOffering(searchCriteria, null);
        
        for (int j = 0; j < studentUserNames.size(); j++) {
        	String studentUserName = studentUserNames.get(j);
        	students.append(studentUserName);
            if (j != studentUserNames.size() - 1) {
                students.append(",");
            }
		}        

        return students.toString();
    }

    /**
     * Change 'Inserted' status to 'Done' for the Module record.
     */
    private void updateInsertedDataStatus(ModuleOffering moduleDetail, int year,
            String lecturerUserName) throws Exception {
        Connection connection = null;
        PreparedStatement pstmt = null;
        try {
            connection = ConnectionManager.getCourseManagementConnection();
            StringBuilder sql = new StringBuilder();
            sql.append("UPDATE CM_MODULES ");
            sql.append("SET STATUS = ? ");
            sql.append("WHERE STATUS = ? ");
            sql.append("AND lecturer_f_id = ");
            sql.append("(SELECT l.lecturer_id ");
            sql.append("FROM CM_LECTURER l, ");
            sql.append("CM_YEAR_CAMPUS c ");
            sql.append("WHERE l.year_campus_f_id = c.year_campus_id ");
            sql.append("AND c.year = ? ");
            sql.append("AND l.username = ?) ");
            sql.append("AND course_code = ? ");
            sql.append("AND course_level = ? ");
            sql.append("AND course_module = ?");
            sql.append("AND method_of_del = ? ");
            sql.append("AND present_cat = ?");
            pstmt = connection.prepareStatement(sql.toString());
            pstmt.setString(1, ModuleLinkStatus.DONE.toString());
            pstmt.setString(2, ModuleLinkStatus.INSERTED.toString());
            pstmt.setInt(3, year);
            pstmt.setString(4, lecturerUserName);
            pstmt.setString(5, moduleDetail.getModuleSubjectCode());
            pstmt.setString(6, moduleDetail.getCourseLevel());
            pstmt.setString(7, moduleDetail.getCourseModule());
            pstmt.setString(8, moduleDetail.getMethodOfDeliveryCodeParam());
            pstmt.setString(9, moduleDetail.getModeOfDeliveryCodeParam());
            pstmt.executeUpdate();
        }
        catch (ConnectionNotEstablishedException e) {
            log.error("A SQL Connection could not be established while performing updateInsertedDataStatus", e);
            throw new ConnectionNotEstablishedException("A SQL Connection could not be established while performing updateInsertedDataStatus", e);
        }
        catch (SQLException e) {
            log.error("SQL error occurred while performing updateInsertedDataStatus", e);
            throw new SQLException("SQL error occurred while performing updateInsertedDataStatus", e);
        }
        finally {
            ConnectionManager.close(null, pstmt, connection);
        }
    }

    /**
     * Delete the Sakai Course Management data through a web service call.
     * @param year The year for which to delete the Sakai Course Management data.
     * @param modules The module for which to delete the Sakai Course Management data.
     * @param lecturerUserName The lecturer user name from which this module will be unlinked.
     */
    public void deleteSakaiCMData(int year, List<ModuleOffering> modules, String lecturerUserName) {

        for (ModuleOffering module : modules) {
            try {
                log.info("Get the current Sakai session with Sakai Web Services");
                String sessionId = createSakaiSession();
                if(StringUtils.isEmpty(sessionId)){
                	 log.error(
                             "An error occurred while creating a Sakai admin session Id");
                	 return;
                }
                log.info("Delete Course Management data with Sakai Web Services");
                boolean courseSetExists = isCourseSetExists(module);
                boolean canonicalCourseExists = isCanonicalCourseExists(module);
                boolean courseOfferingExists = isCourseOfferingExists(module, year);
                boolean onlyDeletedModulesExist = isOnlyDeletedModulesExist(year);
                String wsResult = callDeleteCMDataWS(sessionId, year, module.getModuleSubjectCode(),
                    module.getCanonicalCourseReference(), module.getCourseOfferingReference(year),
                    module.getEnrollmentSetReference(year), lecturerUserName,
                    getDelimitedStudentUserNames(year, module), courseSetExists,
                    canonicalCourseExists, courseOfferingExists, onlyDeletedModulesExist);
                if (SAKAI_WEB_SERVICE_SUCCESS.equals(wsResult)) {
                    deleteDeletedDataStatus(year);
                }
            }
            catch (Exception e) {
                log.error("An error occurred while deleting the Sakai CM data using the Sakai REST Web service", e);
            }
        }
    }

    /**
     * Returns a boolean whether a non deleted Course Set exists. (in any year)
     */
    private boolean isCourseSetExists(ModuleOffering module) throws Exception {
        boolean exists = false;
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        try {
            connection = ConnectionManager.getCourseManagementConnection();
            StringBuilder sql = new StringBuilder();
            sql.append("select count(*) ");
            sql.append("from CM_MODULES m ");
            sql.append("where m.course_code = ? ");
            sql.append("and m.status <> ? ");
            pstmt = connection.prepareStatement(sql.toString());
            pstmt.setString(1, module.getModuleSubjectCode());
            pstmt.setString(2, ModuleLinkStatus.DELETED.toString());
            rset = pstmt.executeQuery();
            if (rset.next()) {
                exists = rset.getInt(1) > 0;
            }
        }
        catch (ConnectionNotEstablishedException e) {
            log.error(
                "A SQL Connection could not be established while performing isCourseSetExists", e);
            throw new ConnectionNotEstablishedException("A SQL Connection could not be established while performing isCourseSetExists", e);
        }
        catch (SQLException e) {
            log.error("SQL error occurred while performing isCourseSetExists", e);
            throw new SQLException("SQL error occurred while performing isCourseSetExists", e);
        }
        finally {
            ConnectionManager.close(rset, pstmt, connection);
        }
        return exists;
    }

    /**
     * Returns a boolean to indicate whether a non deleted CanonicalCourse exists. (in any year)
     */
    private boolean isCanonicalCourseExists(ModuleOffering module) throws Exception {
        boolean exists = false;
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        try {
            connection = ConnectionManager.getCourseManagementConnection();
            StringBuilder sql = new StringBuilder();
            sql.append("select count(*) ");
            sql.append("from CM_MODULES m ");
            sql.append("where m.course_code = ? ");
            sql.append("and m.course_level = ? ");
            sql.append("and m.course_module = ? ");
            sql.append("and m.status <> ? ");
            pstmt = connection.prepareStatement(sql.toString());
            pstmt.setString(1, module.getModuleSubjectCode());
            pstmt.setString(2, module.getCourseLevel());
            pstmt.setString(3, module.getCourseModule());
            pstmt.setString(4, ModuleLinkStatus.DELETED.toString());
            rset = pstmt.executeQuery();
            if (rset.next()) {
                exists = rset.getInt(1) > 0;
            }
        }
        catch (ConnectionNotEstablishedException e) {
            log.error(
                "A SQL Connection could not be established while performing isCanonicalCourseExists", e);
            throw new ConnectionNotEstablishedException("A SQL Connection could not be established while performing isCanonicalCourseExists", e);
        }
        catch (SQLException e) {
            log.error("SQL error occurred while performing isCanonicalCourseExists", e);
            throw new SQLException("SQL error occurred while performing isCanonicalCourseExists", e);
        }
        finally {
            ConnectionManager.close(rset, pstmt, connection);
        }
        return exists;
    }

    /**
     * Returns a boolean indicating whether this course offering exists for another lecturer.
     */
    private boolean isCourseOfferingExists(ModuleOffering module, int year) throws Exception {
        boolean exists = false;
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        try {
            connection = ConnectionManager.getCourseManagementConnection();
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT COUNT(*) ");
            sql.append("FROM CM_MODULES m, CM_LECTURER l, CM_YEAR_CAMPUS c ");
            sql.append("WHERE m.status <> ? ");
            sql.append("AND m.course_code = ? ");
            sql.append("AND m.course_level = ? ");
            sql.append("AND m.course_module = ? ");
            sql.append("AND  m.lecturer_f_id = l.lecturer_id ");
            sql.append("AND l.year_campus_f_id = c.year_campus_id ");
            sql.append("AND c.year = ? ");
            sql.append("AND c.campus_code = ?");
            sql.append("AND method_of_del = ? ");
            sql.append("AND present_cat = ?");
            pstmt = connection.prepareStatement(sql.toString());
            pstmt.setString(1, ModuleLinkStatus.DELETED.toString());
            pstmt.setString(2, module.getModuleSubjectCode());
            pstmt.setString(3, module.getCourseLevel());
            pstmt.setString(4, module.getCourseModule());
            pstmt.setInt(5, year);
            pstmt.setString(6, module.getModuleSite());
            pstmt.setString(7, module.getMethodOfDeliveryCodeParam());
            pstmt.setString(8, module.getModeOfDeliveryCodeParam());     
            rset = pstmt.executeQuery();
            if (rset.next()) {
                exists = rset.getInt(1) > 0;
            }
        }
        catch (ConnectionNotEstablishedException e) {
            log.error(
                "A SQL Connection could not be established while performing isCourseOfferingExists", e);
            throw new ConnectionNotEstablishedException("A SQL Connection could not be established while performing isCourseOfferingExists", e);
        }
        catch (SQLException e) {
            log.error("SQL error occurred while performing isCourseOfferingExists", e);
            throw new SQLException("SQL error occurred while performing isCourseOfferingExists", e);
        }
        finally {
            ConnectionManager.close(rset, pstmt, connection);
        }
        return exists;
    }

    /**
     * Returns a boolean indicating whether only modules with 'deleted' status exists for the specified year.
     */
    private boolean isOnlyDeletedModulesExist(int year) throws Exception {
        boolean onlyDeletedModulesExist = false;
        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        try {
            connection = ConnectionManager.getCourseManagementConnection();
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT COUNT(*) ");
            sql.append("FROM CM_MODULES m, CM_LECTURER l, CM_YEAR_CAMPUS c ");
            sql.append("WHERE m.status <> ? ");
            sql.append("and m.lecturer_f_id = l.lecturer_id ");
            sql.append("and l.year_campus_f_id = c.year_campus_id ");
            sql.append("and c.year = ? ");
            pstmt = connection.prepareStatement(sql.toString());
            pstmt.setString(1, ModuleLinkStatus.DELETED.toString());
            pstmt.setInt(2, year);
            rset = pstmt.executeQuery();
            if (rset.next()) {
                onlyDeletedModulesExist = rset.getInt(1) == 0;
            }
        }
        catch (ConnectionNotEstablishedException e) {
            log.error(
                "A SQL Connection could not be established while performing isOnlyDeletedModulesExist", e);
            throw new ConnectionNotEstablishedException("A SQL Connection could not be established while performing isOnlyDeletedModulesExist", e);
        }
        catch (SQLException e) {
            log.error("SQL error occurred while performing isOnlyDeletedModulesExist", e);
            throw new SQLException("SQL error occurred while performing isOnlyDeletedModulesExist", e);
        }
        finally {
            ConnectionManager.close(rset, pstmt, connection);
        }
        return onlyDeletedModulesExist;
    }

    /**
     * Delete the Sakai Course Management Data.
     * @param sessionId The active Sakai session id
     * @param year The year for which to delete the course management data.
     * @param courseCode The course code of the module (Example: AFNL)
     * @param canonicalCourseRef The canonical course reference (Example: AFNL 111)
     * @param courseOfferingRef The course offering reference (Example: AFNL 111 P 2011)
     * @param enrollmentSetRef The enrollment set reference (Example: AFNL 111 P 2011 ES)
     * @param lecturerUserName The lecturer name linked to this module.
     * @param studentUserNames A list of student user names linked to this module.
     * @param lecturerLinkedToCourseSet A boolean indicating whether the lecturer is linked to a Course Set (any year)
     * @param courseSetExists A boolean indicating whether this Course Set exists (any year)
     * @param canonicalCourseExists A boolean indicating whether this Canonical Course exists (any year)
     * @param courseOfferingExists A boolean indicating whether this Course Offering exists for other lecturers. 
     * @param onlyDeletedModulesExist A boolean indicating whether only modules with a deleted status exist for the year.
     * @return The result string. Only when the string equals "success" has the call been successful.
     * @throws Exception
     */
    private String callDeleteCMDataWS(String sessionId, int year, String courseCode,
            String canonicalCourseRef, String courseOfferingRef, String enrollmentSetRef,
            String lecturerUserName, String studentUserNames, boolean courseSetExists,
            boolean canonicalCourseExists, boolean courseOfferingExists,
            boolean onlyDeletedModulesExist) throws Exception {
		WebClient client = WebClient.create(SettingsProperties.getProperty("sakai.rest.address") + "/nwu-cm/delete-cm-data");
		client = client.accept("application/json").type("application/json");
		client.query("sakai.session", sessionId);
		client.query("year", "" + year);
		client.query("courseCode", courseCode);
		client.query("canonicalCourseRef", canonicalCourseRef);
		client.query("courseOfferingRef", courseOfferingRef);
		client.query("enrollmentSetRef", enrollmentSetRef);
		client.query("lecturerUserName", lecturerUserName);
		client.query("studentUserNames", studentUserNames);
		client.query("courseSetExists", courseSetExists ? "true" : "false");
		client.query("canonicalCourseExists", canonicalCourseExists ? "true" : "false");
		client.query("courseOfferingExists", courseOfferingExists ? "true" : "false");
		client.query("onlyDeletedModulesExist", onlyDeletedModulesExist ? "true" : "false");
		Response response = client.post(null);
		int statusCode = response.getStatus();
		String results = null;
		if (statusCode == 200) {
			results = IOUtils.toString((InputStream) response.getEntity());
		}
        return results;
    }

    /**
     * Remove the module records with status equal to 'deleted'.
     */
    private void deleteDeletedDataStatus(int year) throws Exception {
        Connection connection = null;
        PreparedStatement pstmt = null;
        try {
            connection = ConnectionManager.getCourseManagementConnection();
            StringBuilder sql = new StringBuilder();
            sql.append("delete from CM_MODULES ");
            sql.append("where status = ? ");
            sql.append("and lecturer_f_id in ");
            sql.append("(select l.lecturer_id ");
            sql.append("from CM_LECTURER l, ");
            sql.append("CM_YEAR_CAMPUS c ");
            sql.append("where l.year_campus_f_id = c.year_campus_id ");
            sql.append("and c.year = ?)");
            pstmt = connection.prepareStatement(sql.toString());
            pstmt.setString(1, ModuleLinkStatus.DELETED.toString());
            pstmt.setInt(2, year);
            pstmt.executeUpdate();
        }
        catch (ConnectionNotEstablishedException e) {
            log.error("A SQL Connection could not be established while performing deleteDeletedDataStatus", e);
            throw new ConnectionNotEstablishedException("A SQL Connection could not be established while performing deleteDeletedDataStatus", e);
        }
        catch (SQLException e) {
            log.error("SQL error occurred while performing deleteDeletedDataStatus", e);
            throw new SQLException("SQL error occurred while performing deleteDeletedDataStatus", e);
        }
        finally {
            ConnectionManager.close(null, pstmt, connection);
        }
    }
}