package za.ac.nwu.sql;

import java.sql.Connection;
import java.sql.DriverManager;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sakaiproject.component.api.ServerConfigurationService;

/**
 * This class handles all the connections to the different databases.<br/>
 * If no properties are specified in sakai.properties, the default jndi settings are used.
 */
public class ConnectionManager {

    private static final Log LOG = LogFactory.getLog(ConnectionManager.class);

    private ServerConfigurationService serverConfigurationService;

	public ConnectionManager(ServerConfigurationService serverConfigurationService) {
        this.serverConfigurationService = serverConfigurationService;
    }

    public Connection getCourseManagementConnection() {
        Connection connection = null;
        if ("jdbc".equals(serverConfigurationService.getString("nwu.cm.link.db", "jdbc"))) {
            connection = getCourseManagementJDBCConnection();
        }
        else {
            connection = getCourseManagementJNDIConnection();
        }
        if (connection == null) {
            LOG.error("ConnectionManager: No Content Management JNDI/JDBC connection could be found");
        }
        return connection;
    }

    private Connection getCourseManagementJDBCConnection() {
        Connection conn = null;
        try {
            Class.forName(serverConfigurationService.getString("nwu.cm.link.jdbc.driver",
                "com.mysql.jdbc.Driver"));
            String url = serverConfigurationService.getString("nwu.cm.link.jdbc.url",
                "jdbc:mysql://127.0.0.1:3306/sakai");
            conn = DriverManager.getConnection(url,
                serverConfigurationService.getString("nwu.cm.link.jdbc.user", "sakai"),
                serverConfigurationService.getString("nwu.cm.link.jdbc.password", "sakai"));
        }
        catch (Exception e) {
            LOG.error("JDBC Content Management Connection could not be established.", e);
        }
        return conn;
    }

    private Connection getCourseManagementJNDIConnection() {
        Connection conn = null;
        try {
            Context ctx = new InitialContext();
            DataSource ds = (DataSource) ctx.lookup(serverConfigurationService
                    .getString("nwu.cm.link.jndi.name"));
            conn = ds.getConnection();
        }
        catch (Exception e) {
            LOG.error("JNDI Content Management Connection could not be established.", e);
        }
        return conn;
    }
    

    public ServerConfigurationService getServerConfigurationService() {
		return serverConfigurationService;
	}

}
