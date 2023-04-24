package org.xbase.com.manager;

import java.io.IOException;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import org.xbase.com.constants.ConfigConstants;
import org.xbase.com.constants.MessageConstants;
import org.xbase.com.constants.MigratorConstants;
import org.xbase.com.migrator.TableMigrator;

public class MigrationManager {

	private MigrationManager() {}
	
	private static Map<String, String> configMap = new HashMap<String, String>();
	private static Connection conn = null;
	public static void main(String[] args) {
		InventoryManager.startMigration();
		try {
			configMap = ConfigManager.populateConfigDetails(args);
		} catch (IOException e) {
			System.out.println("IO " + MessageConstants.EXCEPTIONWHILE + " populating config details." + e.getMessage());
			e.printStackTrace();
		}
		
		if(configMap.get(ConfigConstants.SOURCEDATABASE).equalsIgnoreCase(MigratorConstants.ORACLE)) {
		 conn = OracleConnectionManager.getInstance().getOracleDBConnection();
		}
		
		TableMigrator.migrate(conn, configMap);
		
		InventoryManager.endMigration(configMap.get(ConfigConstants.INVENTORYFILEPATH), configMap.get(ConfigConstants.INVENTORYFILENAME));
	}
	
	public static Map<String, String> getConfigMap(){
		return configMap;
	}
	
	public static Connection getOracleConnection() {
		return OracleConnectionManager.getInstance().getOracleDBConnection();
	}
	
}
