package za.ac.nwu.sql;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.component.api.ServerConfigurationService;

import za.ac.nwu.model.Campus;
import za.ac.nwu.model.Lecturer;
import za.ac.nwu.model.Module;
import za.ac.nwu.model.Status;
import za.ac.nwu.model.Student;
import za.ac.nwu.wsdl.studentacademicregistration.AcademicPeriodInfo;
import za.ac.nwu.wsdl.studentacademicregistration.ModuleOfferingSearchCriteriaInfo;
import za.ac.nwu.wsdl.studentacademicregistration.StudentAcademicRegistrationService;
import za.ac.nwu.wsdl.studentacademicregistration.StudentacademicregistrationserviceClientEp;

public class DataManager {

    private static final Log LOG = LogFactory.getLog(DataManager.class);

    private ConnectionManager connectionManager;

    public DataManager(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    public Map<Status, Integer> getNumOfLinkedModulesPerStatus(final int year) {
        LOG.info("Performing getNumOfLinkedModulesPerStatus (year=" + year + ")");
        Map<Status, Integer> statusMap = new HashMap<Status, Integer>();
        //Defaults
        statusMap.put(Status.DONE, 0);
        statusMap.put(Status.INSERTED, 0);
        statusMap.put(Status.DELETED, 0);
        Connection connection = connectionManager.getCourseManagementConnection();
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        StringBuilder sql = new StringBuilder();
        try {
            sql.append("select status, count(*) ");
            sql.append("from CM_MODULES ");
            sql.append("where lecturer_f_id in ");
            sql.append("(select l.lecturer_id  ");
            sql.append("from CM_LECTURER l, ");
            sql.append("CM_YEAR_CAMPUS c ");
            sql.append("where l.year_campus_f_id = c.year_campus_id ");
            sql.append("and c.year = ?) ");
            sql.append("group by status");
            pstmt = connection.prepareStatement(sql.toString());
            pstmt.setInt(1, year);
            rset = pstmt.executeQuery();
            while (rset.next()) {
                String status = rset.getString(1);
                int count = rset.getInt(2);
                LOG.info("Status - " + status + ": " + count + " record(s)");
                if(Status.DONE.toString().equals(status)){
                    statusMap.put(Status.DONE, count);
                } else if(Status.INSERTED.toString().equals(status)){
                    statusMap.put(Status.INSERTED, count);
                } else if(Status.DONE.toString().equals(status)){
                    statusMap.put(Status.DELETED, count);
                } 
            }
        }
        catch (SQLException e) {
            LOG.error(
                "SQL error occured when performing DataManager.getNumOfLinkedModulesPerStatus", e);
        }
        finally {
            try {
                connection.close();
            }
            catch (Exception e) {
                LOG.error("Error closing connection", e);
            }
        }
        return statusMap;
    }

    public Set<Module> getModules(final int year, final boolean withRelatedInfo,
            final Status... statuses) {
        StringBuilder logInfo = new StringBuilder();
        logInfo.append("Performing getModules (year=");
        logInfo.append(year);
        if (statuses != null && statuses.length > 0) {
            logInfo.append(", statuses=");
            for (int i = 0; i < statuses.length; i++) {
                logInfo.append(statuses[i].toString());
                if (i != statuses.length - 1) {
                    logInfo.append(", ");
                }
            }
        }
        logInfo.append(")");
        LOG.info(logInfo.toString());
        Set<Module> modules = new HashSet<Module>();
        Connection connection = connectionManager.getCourseManagementConnection();
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        StringBuilder sql = new StringBuilder();
        try {
            sql.append("select m.course_code, ");
            sql.append("m.course_level, ");
            sql.append("m.course_module, ");
            sql.append("c.campus_code, ");
            sql.append("l.lecturer_id, ");
            sql.append("m.method_of_del, ");
            sql.append("m.present_cat ");
            sql.append("from CM_MODULES m, ");
            sql.append("CM_LECTURER l, ");
            sql.append("CM_YEAR_CAMPUS c ");
            sql.append("where ");
            if (statuses != null && statuses.length > 0) {
                sql.append("m.status in (");
                for (int i = 0; i < statuses.length; i++) {
                    sql.append("?");
                    if (i != statuses.length - 1) {
                        sql.append(",");
                    }
                }
                sql.append(") and ");
            }
            sql.append("m.lecturer_f_id = l.lecturer_id ");
            sql.append("and l.year_campus_f_id = c.year_campus_id ");
            sql.append("and c.year = ? ");
            sql.append("group by m.course_code, m.course_level, m.course_module, c.campus_code, l.lecturer_id, m.method_of_del, m.present_cat");
            pstmt = connection.prepareStatement(sql.toString());
            int index = 1;
            if (statuses != null) {
                for (Status status : statuses) {
                    pstmt.setString(index++, status.toString());
                }
            }
            pstmt.setInt(index, year);
            rset = pstmt.executeQuery();
            boolean existingModuleFound = false;
            while (rset.next()) {
                Campus campus = null;
                if (Campus.POTCHEFSTROOM.getNumber().equals(rset.getString(4))) {
                    campus = Campus.POTCHEFSTROOM;
                }
                else if (Campus.VAALDRIEHOEK.getNumber().equals(rset.getString(4))) {
                    campus = Campus.VAALDRIEHOEK;
                }
                else if (Campus.MAFIKENG.getNumber().equals(rset.getString(4))) {
                    campus = Campus.MAFIKENG;
                }
                Module module = new Module(rset.getString(1), rset.getString(2), rset.getString(3),
                        campus, year, rset.getString(6), rset.getString(7));
                if (withRelatedInfo) {
                	Lecturer lecturer = getLecturer(rset.getInt(5));
                    for (Module existingModule : modules) {
                        if (existingModule.equals(module)) {
                            existingModule.addLinkedLecturer(lecturer);
                            existingModuleFound = true;
                            break;
                        }
                    }
                    if (!existingModuleFound) {
                        module.addLinkedLecturer(lecturer);
                        module.setLinkedStudents(getActiveStudents(module));
                    }
                }
                modules.add(module);
                existingModuleFound = false;
            }
        }
        catch (SQLException e) {
            LOG.error("SQL error occured when performing DataManager.getModules", e);
        }
        finally {
            try {
                connection.close();
            }
            catch (Exception e) {
                LOG.error("Error closing connection", e);
            }
        }
        return modules;
    }

    public Set<Module> getAllCMModules(final int year) {
        LOG.info("Performing getAllModules");
        Set<Module> modules = new HashSet<Module>();
        Connection connection = connectionManager.getCourseManagementConnection();
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        StringBuilder sql = new StringBuilder();
        try {
            sql.append("select distinct m.course_code, ");
            sql.append("m.course_level, ");
            sql.append("m.course_module, ");
            sql.append("c.campus_code, ");
            sql.append("m.method_of_del, ");
            sql.append("m.present_cat ");
            sql.append("from CM_MODULES m, ");
            sql.append("CM_LECTURER l, ");
            sql.append("CM_YEAR_CAMPUS c ");
            sql.append("where ");
            sql.append("m.lecturer_f_id = l.lecturer_id ");
            sql.append("and l.year_campus_f_id = c.year_campus_id ");
            sql.append("and c.year = ? ");
            sql.append("group by m.course_code, m.course_level, m.course_module, c.campus_code");
            pstmt = connection.prepareStatement(sql.toString());
            int index = 1;
            pstmt.setInt(index, year);
            rset = pstmt.executeQuery();
            while (rset.next()) {
                Campus campus = null;
                if (Campus.POTCHEFSTROOM.getNumber().equals(rset.getString(4))) {
                    campus = Campus.POTCHEFSTROOM;
                }
                else if (Campus.VAALDRIEHOEK.getNumber().equals(rset.getString(4))) {
                    campus = Campus.VAALDRIEHOEK;
                }
                else if (Campus.MAFIKENG.getNumber().equals(rset.getString(4))) {
                    campus = Campus.MAFIKENG;
                }
                Module module = new Module(rset.getString(1), rset.getString(2), rset.getString(3),
                        campus, year, rset.getString(5), rset.getString(6));
                module.setLinkedStudents(getActiveStudents(module));
                modules.add(module);
            }
        }
        catch (SQLException e) {
            LOG.error("SQL error occured when performing DataManager.getAllCMModules", e);
        }
        finally {
            try {
                connection.close();
            }
            catch (Exception e) {
                LOG.error("Error closing connection", e);
            }
        }
        return modules;
    }

    public Set<Module> getAllCMModulesForLecturerRemove(final int year) {
        Set<Module> modules = new HashSet<Module>();
        Connection connection = connectionManager.getCourseManagementConnection();
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        StringBuilder sql = new StringBuilder();
        try {
            sql.append("select distinct m.course_code, ");
            sql.append("m.course_level, ");
            sql.append("m.course_module, ");
            sql.append("c.campus_code, ");
            sql.append("l.lecturer_id, ");
            sql.append("m.method_of_del, ");
            sql.append("m.present_cat ");
            sql.append("from CM_MODULES m, ");
            sql.append("CM_LECTURER l, ");
            sql.append("CM_YEAR_CAMPUS c ");
            sql.append("where ");
            sql.append("m.lecturer_f_id = l.lecturer_id ");
            sql.append("and l.year_campus_f_id = c.year_campus_id ");
            sql.append("and c.year = ? ");
            sql.append("group by m.course_code, m.course_level, m.course_module, c.campus_code, l.lecturer_id");
            pstmt = connection.prepareStatement(sql.toString());
            int index = 1;
            pstmt.setInt(index, year);
            rset = pstmt.executeQuery();
            boolean existingModuleFound = false;
            while (rset.next()) {
                Campus campus = null;
                if (Campus.POTCHEFSTROOM.getNumber().equals(rset.getString(4))) {
                    campus = Campus.POTCHEFSTROOM;
                }
                else if (Campus.VAALDRIEHOEK.getNumber().equals(rset.getString(4))) {
                    campus = Campus.VAALDRIEHOEK;
                }
                else if (Campus.MAFIKENG.getNumber().equals(rset.getString(4))) {
                    campus = Campus.MAFIKENG;
                }
                Module module = new Module(rset.getString(1), rset.getString(2), rset.getString(3),
                        campus, year, rset.getString(6), rset.getString(7));
            	Lecturer lecturer = getLecturer(rset.getInt(5));
                for (Module existingModule : modules) {
                    if (existingModule.equals(module)) {
                        existingModule.addLinkedLecturer(lecturer);
                        existingModuleFound = true;
                        break;
                    }
                }
                if (!existingModuleFound) {
                    module.addLinkedLecturer(lecturer);
                }
                modules.add(module);
                existingModuleFound = false;
            }
        }
        catch (SQLException e) {
            LOG.error(
                "SQL error occured when performing DataManager.getAllCMModulesForLecturerRemove", e);
        }
        finally {
            try {
                connection.close();
            }
            catch (Exception e) {
                LOG.error("Error closing connection", e);
            }
        }
        return modules;
    }

    private Lecturer getLecturer(int lecturerId) {
        LOG.info("Performing getLecturer ");
        Lecturer lecturer = null;
        Connection connection = connectionManager.getCourseManagementConnection();
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        StringBuilder sql = new StringBuilder();
        try {
            sql.append("select l.username ");
            sql.append("from CM_LECTURER l ");
            sql.append("where l.lecturer_id = ? ");
            pstmt = connection.prepareStatement(sql.toString());
            pstmt.setInt(1, lecturerId);
            rset = pstmt.executeQuery();
            while (rset.next()) {
                lecturer = new Lecturer(rset.getString(1));
            }
        }
        catch (SQLException e) {
            LOG.error("SQL error occured when performing DataManager.getLecturer", e);
        }
        finally {
            try {
                connection.close();
            }
            catch (Exception e) {
                LOG.error("Error closing connection", e);
            }
        }
        return lecturer;
    }

    private Set<Student> getActiveStudents(final Module module) {
        LOG.info("Performing getStudents (module=" + module.getCourseOfferingReference() + ")");
        Set<Student> students = new HashSet<Student>();        
        Calendar calendar = Calendar.getInstance();
        
        ModuleOfferingSearchCriteriaInfo searchCriteria = new ModuleOfferingSearchCriteriaInfo();

        AcademicPeriodInfo academicPeriodInfo = new AcademicPeriodInfo();
        academicPeriodInfo.setAcadPeriodtTypeKey("vss.code.AcademicPeriod.Year");
        academicPeriodInfo.setAcadPeriodValue(Integer.toString(calendar.get(Calendar.YEAR)));
        
        searchCriteria.setAcademicPeriod(academicPeriodInfo);
        searchCriteria.setModuleSubjectCode(module.getCourseCode().toUpperCase());
        searchCriteria.setModuleNumber(module.getModuleNumber());       
        searchCriteria.setModuleSite(module.getCampus().getNumber());
        searchCriteria.setMethodOfDeliveryTypeKey(module.getMethodOfDeliveryCode());
        searchCriteria.setModeOfDeliveryTypeKey(module.getPresentationCategoryCode());
        ServerConfigurationService serverConfigurationService = connectionManager.getServerConfigurationService(); 
        String webserviceUrl = serverConfigurationService
                .getString("ws.student.url", "http://v-soatst-ap-lnx1.nwu.ac.za:8001/soa-infra/services/default/StudentAcademicRegistrationByModuleOffering/StudentAcademicRegistration?wsdl");
        StudentacademicregistrationserviceClientEp clientService;
		try {
			clientService = new StudentacademicregistrationserviceClientEp(new URL(webserviceUrl));

	        StudentAcademicRegistrationService service = clientService.getStudentAcademicRegistrationServicePt();
	        List<String> studentUserNames = service.getStudentAcademicRegistrationByModuleOffering(searchCriteria, null);
	        
	        for (int j = 0; j < studentUserNames.size(); j++) {
	        	String studentUserName = studentUserNames.get(j);
	        	students.add(new Student(studentUserName));
			}        
		} catch (Exception e) {
            LOG.error("Exception occured when trying to request the list of active students from StudentAcademicRegistrationByModuleOffering.", e);
		} 
        return students;
    }

    /**
     * Change 'Inserted' status to 'Done' for Module records.
     */
    public void updateInsertedDataStatus(final int year) {
        LOG.info("Performing updateInsertedDataStatus (year=" + year + ")");
        Connection connection = connectionManager.getCourseManagementConnection();
        PreparedStatement pstmt = null;
        StringBuilder sql = new StringBuilder();
        try {
            sql.append("update CM_MODULES ");
            sql.append("set status = ? ");
            sql.append("where status = ? ");
            sql.append("and lecturer_f_id in ");
            sql.append("(select l.lecturer_id ");
            sql.append("from CM_LECTURER l, ");
            sql.append("CM_YEAR_CAMPUS c ");
            sql.append("where l.year_campus_f_id = c.year_campus_id ");
            sql.append("and c.year = ?)");
            pstmt = connection.prepareStatement(sql.toString());
            pstmt.setString(1, Status.DONE.toString());
            pstmt.setString(2, Status.INSERTED.toString());
            pstmt.setInt(3, year);
            pstmt.executeUpdate();
        }
        catch (SQLException e) {
            LOG.error("SQL error occured when performing DataManager.updateInsertedDataStatus", e);
        }
        finally {
            try {
                connection.close();
            }
            catch (Exception e) {
                LOG.error("Error closing connection", e);
            }
        }
    }
    
    /**
      * Returns a boolean to indicate whether a non deleted CanonicalCourse exists. (in any year)
      */
    public boolean isNonDeletedCanonicalCourseExists(final Module module) {
        LOG.info("Performing isNonDeletedCanonicalCourseExists (module="
                + module.getCourseOfferingReference()
                + ")");
        boolean exists = false;
        Connection connection = connectionManager.getCourseManagementConnection();
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        StringBuilder sql = new StringBuilder();
        try {
            sql.append("select count(*) ");
            sql.append("from CM_MODULES m ");
            sql.append("where m.course_code = ? ");
            sql.append("and m.course_level = ? ");
            sql.append("and m.course_module = ? ");
            sql.append("and m.status <> ? ");
            sql.append("and m.method_of_del = ? ");
            sql.append("and m.present_cat = ? ");
            pstmt = connection.prepareStatement(sql.toString());
            pstmt.setString(1, module.getCourseCode());
            pstmt.setString(2, module.getCourseLevel());
            pstmt.setString(3, module.getCourseModule());
            pstmt.setString(4, Status.DELETED.toString());
            pstmt.setString(5, module.getMethodOfDeliveryCode());
            pstmt.setString(6, module.getPresentationCategoryCode());
            rset = pstmt.executeQuery();
            while (rset.next()) {
                exists = rset.getInt(1) > 0;
            }
        }
        catch (SQLException e) {
            LOG.error(
                "SQL error occured when performing DataManager.isNonDeletedCanonicalCourseExists",
                e);
        }
        finally {
            try {
                connection.close();
            }
            catch (Exception e) {
                LOG.error("Error closing connection", e);
            }
        }
        return exists;
    }

    /**
     * Returns a boolean whether a non deleted Course Set exists. (in any year)
     */
    public boolean isNonDeletedCourseSetExists(final Module module) {
        LOG.info("Performing isNonDeletedCourseSetExists (module="
                + module.getCourseOfferingReference()
                + ")");
        boolean exists = false;
        Connection connection = connectionManager.getCourseManagementConnection();
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        StringBuilder sql = new StringBuilder();
        try {
            sql.append("select count(*) ");
            sql.append("from CM_MODULES m ");
            sql.append("where m.course_code = ? ");
            sql.append("and m.status <> ? ");
            pstmt = connection.prepareStatement(sql.toString());
            pstmt.setString(1, module.getCourseCode());
            pstmt.setString(2, Status.DELETED.toString());
            rset = pstmt.executeQuery();
            while (rset.next()) {
                exists = rset.getInt(1) > 0;
            }
        }
        catch (SQLException e) {
            LOG.error("SQL error occured when performing DataManager.isNonDeletedCourseSetExists",
                e);
        }
        finally {
            try {
                connection.close();
            }
            catch (Exception e) {
                LOG.error("Error closing connection", e);
            }
        }
        return exists;
    }

    public void deleteDeletedDataStatus(final int year) {
        LOG.info("Performing deleteDeletedDataStatus (year=" + year + ")");
        Connection connection = connectionManager.getCourseManagementConnection();
        PreparedStatement pstmt = null;
        StringBuilder sql = new StringBuilder();
        try {
            sql.append("delete from CM_MODULES ");
            sql.append("where status = ? ");
            sql.append("and lecturer_f_id in ");
            sql.append("(select l.lecturer_id ");
            sql.append("from CM_LECTURER l, ");
            sql.append("CM_YEAR_CAMPUS c ");
            sql.append("where l.year_campus_f_id = c.year_campus_id ");
            sql.append("and c.year = ?)");
            pstmt = connection.prepareStatement(sql.toString());
            pstmt.setString(1, Status.DELETED.toString());
            pstmt.setInt(2, year);
            pstmt.executeUpdate();
        }
        catch (SQLException e) {
            LOG.error("SQL error occured when performing DataManager.deleteDeletedDataStatus", e);
        }
        finally {
            try {
                connection.close();
            }
            catch (Exception e) {
                LOG.error("Error closing connection", e);
            }
        }
    }

	public List<String> getUsers(String subject, String campus, String year)
			throws Exception {

		LOG.info("Performing getUsers (subject=" + subject + ")");
		List<String> students = new ArrayList<String>();
		ModuleOfferingSearchCriteriaInfo searchCriteria = new ModuleOfferingSearchCriteriaInfo();

		AcademicPeriodInfo academicPeriodInfo = new AcademicPeriodInfo();
		academicPeriodInfo.setAcadPeriodtTypeKey("vss.code.AcademicPeriod.Year");
		academicPeriodInfo.setAcadPeriodValue(year);

		searchCriteria.setAcademicPeriod(academicPeriodInfo);
		searchCriteria.setModuleSubjectCode(subject.substring(0, 4));
		searchCriteria.setModuleNumber(subject.substring(5, 8));
		searchCriteria.setModuleSite(campus);		
		String newValue = subject.substring(9);
		int indexOf = newValue.indexOf("-");
		searchCriteria.setMethodOfDeliveryTypeKey("vss.code.ENROLCAT." + newValue.substring(0, indexOf));		
		searchCriteria.setModeOfDeliveryTypeKey("vss.code.PRESENTCAT." + newValue.substring(indexOf + 1));
		ServerConfigurationService serverConfigurationService = connectionManager.getServerConfigurationService();
		String webserviceUrl = serverConfigurationService.getString("ws.student.url",
				"http://v-soatst-ap-lnx1.nwu.ac.za:8001/soa-infra/services/default/StudentAcademicRegistrationByModuleOffering/StudentAcademicRegistration?wsdl");
		StudentacademicregistrationserviceClientEp clientService;
		try {
			clientService = new StudentacademicregistrationserviceClientEp(new URL(webserviceUrl));

			StudentAcademicRegistrationService service = clientService.getStudentAcademicRegistrationServicePt();
			return service.getStudentAcademicRegistrationByModuleOffering(searchCriteria, null);

		} catch (Exception e) {
			LOG.error(
					"Exception occured when trying to request the list of active students from StudentAcademicRegistrationByModuleOffering.",
					e);
		}
		return students;
	}
}