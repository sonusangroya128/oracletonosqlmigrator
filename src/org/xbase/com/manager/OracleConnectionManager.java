package org.xbase.com.manager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.xbase.com.constants.ConfigConstants;
import org.xbase.com.constants.MessageConstants;
import org.xbase.com.constants.XBASEConstants;
import org.xbase.com.constants.PatternConstants;
import org.xbase.com.util.PrintUtil;

public class OracleConnectionManager {

	private static OracleConnectionManager oracleConnectionManager = new OracleConnectionManager(); 
	private static Connection connection = null;

	private OracleConnectionManager() {
	}

	public static OracleConnectionManager getInstance() {
		return oracleConnectionManager;
	}

	/**
	 * This method will establish connection to Oracle Database and returns the
	 * connection
	 */
	public static Connection getOracleDBConnection() {
		try {
			if (null == connection) {
				Properties configProperties = new Properties();
				configProperties = ConfigManager.getConfigProperties();
				configProperties.put(ConfigConstants.USER, configProperties.get(ConfigConstants.SOURCEDATABASEUSERNAME));
				configProperties.put(ConfigConstants.PASSWORD, configProperties.get(ConfigConstants.SOURCEDATABASEPASSWORD));
				// Sample URL: "jdbc:oracle:thin:@localhost:1521:orcl";
				String connectionURL = XBASEConstants.JDBCORACLETHIN + configProperties.get(ConfigConstants.HOSTNAME) + PatternConstants.COLON
						+ configProperties.get(ConfigConstants.SOURCEDATABASEPORT) + PatternConstants.COLON + XBASEConstants.ORCL;
				PrintUtil.log(MessageConstants.INFO + XBASEConstants.CONNECTIONTRYMESSAGE + PatternConstants.DATASEPERATOR + "[" + connectionURL + "]");
				// Load and register, establish db connection
				// connection = DriverManager.getConnection(MigratorConstants.ORACLEDRIVERORCL,properties);
				connection = DriverManager.getConnection(connectionURL, configProperties);
				if (connection != null) {
					PrintUtil.log(MessageConstants.INFO + XBASEConstants.CONNECTIONSUCCESSFUL + PatternConstants.LINESEPERATOR);
				} else {
					PrintUtil.log(MessageConstants.ERROR + XBASEConstants.CONNECTIONFAILURE + PatternConstants.LINESEPERATOR);
				}
			}
		} catch (SQLException e) {
			PrintUtil.log(MessageConstants.ERROR + "JDBC Driver not found. Please use respective jar. " + "Eg. ojdbc7.jar for Oracle");
			e.printStackTrace();
		}
		return connection;
	}

	/**
	 * This method will close an open connection
	 */
	public void closeConnection(Connection connection) {
		try {
			connection.close();
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
	}
}
