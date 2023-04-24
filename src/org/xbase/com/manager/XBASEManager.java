package org.xbase.com.manager;

import java.io.IOException;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import org.xbase.com.actions.MessageType;
import org.xbase.com.constants.ConfigConstants;
import org.xbase.com.constants.MessageConstants;
import org.xbase.com.constants.PatternConstants;
import org.xbase.com.constants.XBASEConstants;
import org.xbase.com.migrator.DatabaseMigrator;
import org.xbase.com.util.PrintUtil;

public class XBASEManager {

	private XBASEManager() {}
	private static Map<String, String> configMap = new HashMap<String, String>();
	private static Connection conn = null;
	private static String dbType = null;
	public static void main(String[] args) {
		try {
		InventoryManager.initiateXBASEInventory();
		initiateConfigMap(args);
		dbType = determineDBType(configMap.get(ConfigConstants.SOURCEDATABASE));
		if(dbType.equals(XBASEConstants.ORACLE)) {
		 conn = OracleConnectionManager.getOracleDBConnection();
		 if(Boolean.valueOf(configMap.get(ConfigConstants.MIGRATIONMODE))) {
				DatabaseMigrator.migrate(conn, configMap);
			}
		}
		else if(dbType.equals(XBASEConstants.XML)) {
			XMLManager.migrate(configMap);
		}
		else if(dbType.equals(XBASEConstants.JSON)) {
			JSONManager.migrate(configMap);
		}
		if(Boolean.valueOf(configMap.get(ConfigConstants.DATAINJECTIONMODE))) {
			DataInjectionManager.injectData(configMap.get(ConfigConstants.DATAINJECTIONRANGE));
		}
		else {
			InventoryManager.log(ConfigConstants.DATAINJECTIONMODE + PatternConstants.SPACESEPERATOR + "disabled" + PatternConstants.LINESEPERATOR);
		}
		XBASEWindup();
		}
		catch(Exception e) {
			PrintUtil.log(MessageType.ERROR + e.getMessage());
			PrintUtil.log(e.toString());
			e.printStackTrace();
		}
	}
	
	/**
	 * @return
	 */
	private static String determineDBType(String currentDatabaseName) {
		currentDatabaseName = currentDatabaseName.toUpperCase();
		if(currentDatabaseName.contains(XBASEConstants.ORACLE)) {
			return XBASEConstants.ORACLE;
		}
		else if(currentDatabaseName.contains(XBASEConstants.XML)) {
			return XBASEConstants.XML;
		}
		else if(currentDatabaseName.contains(XBASEConstants.JSON)) {
			return XBASEConstants.JSON;
		}
		else if(currentDatabaseName.contains(XBASEConstants.MYSQL)) {
			return XBASEConstants.MYSQL;
		}
		else if(currentDatabaseName.contains(XBASEConstants.SQLSERVER)) {
			return XBASEConstants.SQLSERVER;
		}
		PrintUtil.log(MessageConstants.NOSUCHDATABASE + PatternConstants.SPACESEPERATOR + currentDatabaseName);
		PrintUtil.log(MessageConstants.EXITING);
		System.exit(1);
		throw new RuntimeException(MessageConstants.NOSUCHDATABASE);
	}

	/**
	 * This method initiates and prints config map.
	 */
	private static void initiateConfigMap(String[] configArgs) {
		if (configArgs.length < 1) {
            PrintUtil.log(PatternConstants.LINESEPERATOR + XBASEConstants.PROPERTYFILEMISSING + PatternConstants.LINESEPERATOR);
            System.exit(1);
        }
		try {
			configMap = ConfigManager.populateConfigDetails(configArgs[0]);
		} catch (IOException e) {
			PrintUtil.log(MessageType.ERROR + "IO " + MessageConstants.EXCEPTIONWHILE + "populating config details." + e.getMessage());
			PrintUtil.log(e.toString());
			e.printStackTrace();
		}
		ConfigManager.printConfigDetails(configMap);
	}

	/**
	 * This method creates inventory and log files. Closes all connections
	 */
	private static void XBASEWindup() {
		InventoryManager.createInventory(configMap.get(ConfigConstants.INVENTORYFILEPATH), configMap.get(ConfigConstants.INVENTORYFILENAME));
		LogManager.logMigration(configMap.get(ConfigConstants.LOGFILEPATH), configMap.get(ConfigConstants.LOGFILENAME), PrintUtil.getLog());
		if(dbType.equals(XBASEConstants.ORACLE)) {
			OracleConnectionManager.getInstance().closeConnection(conn);
		}
	}

	public static Map<String,String> getConfigMap(){
		if(null==configMap)
			throw new RuntimeException("ConfigMap " + MessageConstants.NOTINITIALIZED);
		else
			return configMap;
	}
	
}
