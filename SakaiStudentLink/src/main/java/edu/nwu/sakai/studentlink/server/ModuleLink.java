package edu.nwu.sakai.studentlink.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import edu.nwu.sakai.studentlink.client.ModuleOffering;
import edu.nwu.sakai.studentlink.client.SearchCriteria;

public class ModuleLink {

    private static final Logger log = Logger.getLogger(ModuleLink.class);

    private static ModuleLink instance;

    private ModuleLink() {
    }

    public static ModuleLink getInstance() {
        if (instance == null) {
            instance = new ModuleLink();
        }
        return instance;
    }

    public List<ModuleOffering> getAllModulesLinkedToLecturer(Map<SearchCriteria, String> criteria) {
        List<ModuleOffering> linkedModules = new ArrayList<ModuleOffering>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        ModuleOffering moduleOffering = null;
        try {
            conn = ConnectionManager.getCourseManagementConnection();
            StringBuilder sql = new StringBuilder();
            sql.append("select m.course_code, m.course_level, m.course_module, m.method_of_del, m.present_cat ");
            sql.append("from CM_YEAR_CAMPUS c, CM_LECTURER l, CM_MODULES m ");
            sql.append("where c.year = ? ");
            sql.append("and c.campus_code = ? ");
            sql.append("and l.year_campus_f_id = c.year_campus_id ");
            sql.append("and l.username = ? ");
            sql.append("and m.lecturer_f_id = l.lecturer_id ");
            sql.append("and m.status <> ? ");

            String methodOfDel = criteria.get(SearchCriteria.METHOD_OF_DEL) == null ? null : criteria.get(SearchCriteria.METHOD_OF_DEL);
            if(methodOfDel != null){
                sql.append("and m.method_of_del = ? ");
            }
            String modeOfDel = criteria.get(SearchCriteria.PRESENT_CAT) == null ? null : criteria.get(SearchCriteria.PRESENT_CAT);
            if(modeOfDel != null){
                sql.append("and m.present_cat = ? ");
            }
            pstmt = conn.prepareStatement(sql.toString());
            int cnt = 1;
                     
            pstmt.setInt(cnt++, getYear(criteria.get(SearchCriteria.YEAR)));
            pstmt.setString(cnt++, criteria.get(SearchCriteria.CAMPUS));
            pstmt.setString(cnt++, criteria.get(SearchCriteria.USER_NAME));
            pstmt.setString(cnt++, ModuleLinkStatus.DELETED.toString());
            if(methodOfDel != null){
                pstmt.setString(cnt++, "vss.code.ENROLCAT." + methodOfDel);
            }   
            if(modeOfDel != null){
                pstmt.setString(cnt++, "vss.code.PRESENTCAT." + modeOfDel);
            }             
            rset = pstmt.executeQuery();
            while (rset.next()) {            	
            	moduleOffering = new ModuleOffering();
				moduleOffering.setModuleSubjectCode(rset.getString(1));
				moduleOffering.setModuleNumber(rset.getString(2) + rset.getString(3));
				moduleOffering.setModuleSite(criteria.get(SearchCriteria.CAMPUS));
				moduleOffering.setMethodOfDeliveryTypeKey(rset.getString(4));
				moduleOffering.setModeOfDeliveryTypeKey(rset.getString(5));
                linkedModules.add(moduleOffering);
            }
        }
        catch (ConnectionNotEstablishedException e) {
            log.error(
                "A SQL Connection could not be established while performing getAllModulesLinkedToLecturer",
                e);
        }
        catch (SQLException e) {
            log.error("A SQL Error occurred while performing getAllModulesLinkedToLecturer", e);
        }
        finally {
            ConnectionManager.close(rset, pstmt, conn);
        }
        return linkedModules;
    }

    public void linkInstructorToModules(List<ModuleOffering> modules,
            Map<SearchCriteria, String> criteria) {
        final int year = getYear(criteria.get(SearchCriteria.YEAR));
        final String campusNumber = criteria.get(SearchCriteria.CAMPUS);
        final String userName = criteria.get(SearchCriteria.USER_NAME);      
        int yearCampusId = getYearCampusId(year, campusNumber);
        if (yearCampusId == 0) {// year_campus doesn't exists
            insertYearCampus(year, campusNumber);
            yearCampusId = getYearCampusId(year, campusNumber);
        }
        int lecturerId = getLecturerId(userName, year, campusNumber);
        if (lecturerId == 0) {//lecturer doesn't exist
            insertLecturer(yearCampusId, userName);
            lecturerId = getLecturerId(userName, year, campusNumber);
        }
        for (ModuleOffering module : modules) {
            insertModule(lecturerId, module);
            if ("webservice".equals(SettingsProperties.getProperty("sakai.cm.insertion.type",
                "webservice"))) {
                CourseManagementWSHandler courseMgmtWsHandler = CourseManagementWSHandler
                        .getInstance();
                courseMgmtWsHandler.insertSakaiCMData(year, module, userName);
            }
        }
    }

    public void unlinkInstructorFromModules(List<ModuleOffering> modules,
            Map<SearchCriteria, String> criteria) {
        final int year = getYear(criteria.get(SearchCriteria.YEAR));
        final String userName = criteria.get(SearchCriteria.USER_NAME);
        int lecturerId = getLecturerId(userName, year, criteria.get(SearchCriteria.CAMPUS));
        for (ModuleOffering module : modules) {
            //If the module was set to 'Done' status - update it.
            int recordsUpdated = updateModuleLink(module, lecturerId);
            if (recordsUpdated == 0) {
                //If the module's status is 'Inserted', delete the record.
                deleteModuleLink(module, lecturerId);
            }
        }
        //the following must be done in a separate 'for loop' since its dependent on the status settings of all the modules
        if ("webservice".equals(SettingsProperties.getProperty("sakai.cm.insertion.type",
            "webservice"))) {
            CourseManagementWSHandler courseMgmtWSHandler = CourseManagementWSHandler.getInstance();
            courseMgmtWSHandler.deleteSakaiCMData(year, modules, userName);
        }
    }

    private int getYearCampusId(int year, String campusCode) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        int yearCampusId = 0;
        try {
            conn = ConnectionManager.getCourseManagementConnection();
            StringBuilder sql = new StringBuilder();
            sql.append("select year_campus_id ");
            sql.append("from CM_YEAR_CAMPUS ");
            sql.append("where year = ? ");
            sql.append("and campus_code = ?");
            pstmt = conn.prepareStatement(sql.toString());
            pstmt.setInt(1, year);
            pstmt.setString(2, campusCode);
            rset = pstmt.executeQuery();
            while (rset.next()) {
                yearCampusId = rset.getInt(1);
            }
        }
        catch (ConnectionNotEstablishedException e) {
            log.error("A SQL Connection could not be established while performing getYearCampusId",
                e);
        }
        catch (SQLException e) {
            log.error("A SQL Error occurred while performing getYearCampusId", e);
        }
        finally {
            ConnectionManager.close(rset, pstmt, conn);
        }
        return yearCampusId;
    }

    private void insertYearCampus(int year, String campusCode) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = ConnectionManager.getCourseManagementConnection();
            StringBuilder sql = new StringBuilder();
            sql.append("insert into ");
            sql.append("CM_YEAR_CAMPUS (year, campus_code) ");
            sql.append("values (?, ?)");
            pstmt = conn.prepareStatement(sql.toString());
            pstmt.setInt(1, year);
            pstmt.setString(2, campusCode);
            pstmt.executeUpdate();
        }
        catch (ConnectionNotEstablishedException e) {
            log.error(
                "A SQL Connection could not be established while performing insertYearCampus", e);
        }
        catch (SQLException e) {
            log.error("A SQL Error occurred while performing insertYearCampus", e);
        }
        finally {
            ConnectionManager.close(null, pstmt, conn);
        }
    }

    private int getLecturerId(String userName, int year, String campusCode) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        int lecturerId = 0;
        try {
            conn = ConnectionManager.getCourseManagementConnection();
            StringBuilder sql = new StringBuilder();
            sql.append("select l.lecturer_id ");
            sql.append("from CM_LECTURER l, ");
            sql.append("CM_YEAR_CAMPUS c ");
            sql.append("where l.year_campus_f_id = c.year_campus_id ");
            sql.append("and l.username = ? ");
            sql.append("and c.year = ? ");
            sql.append("and c.campus_code = ? ");
            pstmt = conn.prepareStatement(sql.toString());
            pstmt.setString(1, userName);
            pstmt.setInt(2, year);
            pstmt.setString(3, campusCode);
            rset = pstmt.executeQuery();
            while (rset.next()) {
                lecturerId = rset.getInt(1);
            }
        }
        catch (ConnectionNotEstablishedException e) {
            log.error("A SQL Connection could not be established while performing getLecturerId", e);
        }
        catch (SQLException e) {
            log.error("A SQL Error occurred while performing getLecturerId", e);
        }
        finally {
            ConnectionManager.close(rset, pstmt, conn);
        }
        return lecturerId;
    }

    private void insertLecturer(int campusId, String userName) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = ConnectionManager.getCourseManagementConnection();
            StringBuilder sql = new StringBuilder();
            sql.append("insert into ");
            sql.append("CM_LECTURER (year_campus_f_id, username) ");
            sql.append("values (?, ?)");
            pstmt = conn.prepareStatement(sql.toString());
            pstmt.setInt(1, campusId);
            pstmt.setString(2, userName);
            pstmt.executeUpdate();
        }
        catch (ConnectionNotEstablishedException e) {
            log.error("A SQL Connection could not be established while performing insertLecturer",
                e);
        }
        catch (SQLException e) {
            log.error("A SQL Error occurred while performing insertLecturer", e);
        }
        finally {
            ConnectionManager.close(null, pstmt, conn);
        }
    }

    private void insertModule(int lecturerId, ModuleOffering module) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = ConnectionManager.getCourseManagementConnection();
            StringBuilder sql = new StringBuilder();
            sql.append("insert into ");
            sql.append("CM_MODULES (lecturer_f_id, course_code, course_level, course_module, status, method_of_del, present_cat)");
            sql.append("values (?, ?, ?, ?, ?, ?, ?)");
            pstmt = conn.prepareStatement(sql.toString());
            pstmt.setInt(1, lecturerId);
            pstmt.setString(2, module.getModuleSubjectCode());
            pstmt.setString(3, module.getCourseLevel());
            pstmt.setString(4, module.getCourseModule());
            pstmt.setString(5, ModuleLinkStatus.INSERTED.toString());
            pstmt.setString(6, module.getMethodOfDeliveryCodeParam());
            pstmt.setString(7, module.getModeOfDeliveryCodeParam());
            pstmt.executeUpdate();
        }
        catch (ConnectionNotEstablishedException e) {
            log.error("A SQL Connection could not be established while performing insertModule", e);
        }
        catch (SQLException e) {
            log.error("A SQL Error occurred while performing insertModule", e);
        }
        finally {
            ConnectionManager.close(null, pstmt, conn);
        }
    }

    private int updateModuleLink(ModuleOffering module, int lecturerId) {
        int recordsUpdated = 0;
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = ConnectionManager.getCourseManagementConnection();
            StringBuilder sql = new StringBuilder();
            sql.append("update CM_MODULES ");
            sql.append("set status = ? ");
            sql.append("where course_code = ? ");
            sql.append("and course_level = ? ");
            sql.append("and course_module = ? ");
            sql.append("and lecturer_f_id = ? ");
            sql.append("and status = ?");
            sql.append("and method_of_del = ? ");
            sql.append("and present_cat = ?");
            pstmt = conn.prepareStatement(sql.toString());
            pstmt.setString(1, ModuleLinkStatus.DELETED.toString());
            pstmt.setString(2, module.getModuleSubjectCode());
            pstmt.setString(3, module.getCourseLevel());
            pstmt.setString(4, module.getCourseModule());
            pstmt.setInt(5, lecturerId);
            pstmt.setString(6, ModuleLinkStatus.DONE.toString());
            pstmt.setString(7, module.getMethodOfDeliveryCodeParam());
            pstmt.setString(8, module.getModeOfDeliveryCodeParam());
            recordsUpdated = pstmt.executeUpdate();
        }
        catch (ConnectionNotEstablishedException e) {
            log.error(
                "A SQL Connection could not be established while performing updateModuleLink", e);
        }
        catch (SQLException e) {
            log.error("A SQL Error occurred while performing updateModuleLink", e);
        }
        finally {
            ConnectionManager.close(null, pstmt, conn);
        }
        return recordsUpdated;
    }

    private void deleteModuleLink(ModuleOffering module, int lecturerId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = ConnectionManager.getCourseManagementConnection();
            StringBuilder sql = new StringBuilder();
            sql.append("delete from CM_MODULES ");
            sql.append("where course_code = ? ");
            sql.append("and course_level = ? ");
            sql.append("and course_module = ? ");
            sql.append("and lecturer_f_id = ? ");
            sql.append("and status = ? ");
            sql.append("and method_of_del = ? ");
            sql.append("and present_cat = ?");
            pstmt = conn.prepareStatement(sql.toString());
            pstmt.setString(1, module.getModuleSubjectCode());
            pstmt.setString(2, module.getCourseLevel());
            pstmt.setString(3, module.getCourseModule());
            pstmt.setInt(4, lecturerId);
            pstmt.setString(5, ModuleLinkStatus.INSERTED.toString());
            pstmt.setString(6, module.getMethodOfDeliveryCodeParam());
            pstmt.setString(7, module.getModeOfDeliveryCodeParam());
            pstmt.executeUpdate();
        }
        catch (ConnectionNotEstablishedException e) {
            log.error(
                "A SQL Connection could not be established while performing deleteModuleLink", e);
        }
        catch (SQLException e) {
            log.error("A SQL Error occurred while performing deleteModuleLink", e);
        }
        finally {
            ConnectionManager.close(null, pstmt, conn);
        }
    }

    private int getYear(String year) {
        return Integer.parseInt(year);
    }
}