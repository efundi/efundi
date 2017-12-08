package edu.nwu.sakai.studentlink.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

/**
 * This class handles all the connections to the different databases.<br/>
 * If no properties are specified in sakai.properties, the default jndi settings are used.
 */
public class ConnectionManager {

    private static final Logger log = Logger.getLogger(ConnectionManager.class);

    public static Connection getConsolidationConnection() throws ConnectionNotEstablishedException {
        Connection connection = null;
        if ("jdbc".equalsIgnoreCase(SettingsProperties.getProperty("kons.db", "jdbc"))) {
            connection = getConsolidationJDBCConnection();
        }
        else {
            connection = getConsolidationJNDIConnection();
        }
        return connection;
    }

    private static Connection getConsolidationJDBCConnection()
            throws ConnectionNotEstablishedException {
        Connection conn = null;
        try {
            Class.forName(SettingsProperties.getProperty("kons.jdbc.driver",
                "com.microsoft.sqlserver.jdbc.SQLServerDriver"));
            String url = SettingsProperties.getProperty("kons.jdbc.url",
                "jdbc:sqlserver://db-win1.puk.ac.za:1433;databaseName=KONSOLIDASIE");
            conn = DriverManager.getConnection(url,
                SettingsProperties.getProperty("kons.jdbc.user", "itbcjjvv"),
                SettingsProperties.getProperty("kons.jdbc.password", "kasteel123"));
        }
        catch (Exception e) {
            //log.error("Could not establish Consolidation JDBC connection.", e);
            throw new ConnectionNotEstablishedException(
                    "Could not establish Consolidation JDBC connection.", e);
        }
        return conn;
    }

    private static Connection getConsolidationJNDIConnection()
            throws ConnectionNotEstablishedException {
        Connection conn = null;
        try {
            Context ctx = new InitialContext();
            DataSource ds = (DataSource) ctx.lookup(SettingsProperties.getProperty(
                "kons.jndi.name", "java:comp/env/jdbc/kons"));
            conn = ds.getConnection();
        }
        catch (Exception e) {
            //log.error("Could not establish Consolidation JNDI connection.", e);
            throw new ConnectionNotEstablishedException(
                    "Could not establish Consolidation JNDI connection.", e);
        }
        return conn;
    }

    public static Connection getCourseManagementConnection()
            throws ConnectionNotEstablishedException {
        Connection connection = null;
        if ("jdbc".equalsIgnoreCase(SettingsProperties.getProperty("cm.db", "jdbc"))) {
            connection = getCourseManagementJDBCConnection();
        }
        else {
            connection = getCourseManagementJNDIConnection();
        }
        return connection;
    }

    private static Connection getCourseManagementJDBCConnection()
            throws ConnectionNotEstablishedException {
        Connection conn = null;
        try {
            Class.forName(SettingsProperties.getProperty("cm.jdbc.driver", "com.mysql.jdbc.Driver"));
            String url = SettingsProperties.getProperty("cm.jdbc.url",
                "jdbc:mysql://127.0.0.1:3306/sakai");
            conn = DriverManager.getConnection(url,
                SettingsProperties.getProperty("cm.jdbc.user", "sakai"),
                SettingsProperties.getProperty("cm.jdbc.password", "sakai"));
        }
        catch (Exception e) {
            //log.error("Could not establish Course Management JDBC connection.", e);
            throw new ConnectionNotEstablishedException(
                    "Could not establish Course Management JDBC connection.", e);
        }
        return conn;
    }

    private static Connection getCourseManagementJNDIConnection()
            throws ConnectionNotEstablishedException {
        Connection conn = null;
        try {
            Context ctx = new InitialContext();
            DataSource ds = (DataSource) ctx.lookup(SettingsProperties.getProperty("cm.jndi.name"));
            conn = ds.getConnection();
        }
        catch (Exception e) {
            //log.error("Could not establish Course Management JNDI connection.", e);
            throw new ConnectionNotEstablishedException(
                    "Could not establish Course Management JNDI connection.", e);
        }
        return conn;
    }

    public static void close(ResultSet rset, PreparedStatement pstmt, Connection connection) {
        try {
            if (rset != null) {
                rset.close();
            }
        }
        catch (Exception e) {
            log.error("Error closing sql resultset", e);
        }
        try {
            if (pstmt != null) {
                pstmt.close();
            }
        }
        catch (Exception e) {
            log.error("Error closing sql statement", e);
        }
        try {
            if (connection != null) {
                connection.close();
            }
        }
        catch (Exception e) {
            log.error("Error closing sql connection", e);
        }
    }
}