package org.xbase.com.manager;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import org.xbase.com.actions.MessageType;
import org.xbase.com.constants.ConfigConstants;
import org.xbase.com.constants.MessageConstants;
import org.xbase.com.constants.OraTables;
import org.xbase.com.constants.PatternConstants;
import org.xbase.com.constants.XBASEConstants;
import org.xbase.com.environment.EnvironmentSettings;
import org.xbase.com.executor.OracleQueryExecutor;
import org.xbase.com.util.PrintUtil;

public class ConfigManager {
    
	private ConfigManager() {}
    private static boolean configMapInitialized = false, propertiesInitialized=false;
    private static Map<String,String> configMap = new TreeMap<String,String>();
    private static final Properties properties = new Properties();
    
	public static Map<String, String> populateConfigDetails(String configFilePath) throws IOException {
        InputStream inputStream = new FileInputStream(configFilePath);
        properties.load(inputStream);
        propertiesInitialized=true;
        
        try {
        	EnvironmentSettings.DEBUGMODE = Boolean.valueOf(properties.getProperty(ConfigConstants.DEBUGMODE, "false"));
        	EnvironmentSettings.DEBUGMODEV = Boolean.valueOf(properties.getProperty(ConfigConstants.DEBUGMODEV, "false"));
        }
        catch(Exception e) {
        	EnvironmentSettings.DEBUGMODE = EnvironmentSettings.DEBUGMODEV = false;
        }
        
        String migrationMode = properties.getProperty(ConfigConstants.MIGRATIONMODE);
        String hostName = properties.getProperty(ConfigConstants.HOSTNAME, ConfigConstants.LOCALHOST);
        String sourceDatabase = properties.getProperty(ConfigConstants.SOURCEDATABASE);
        String databaseFilePath = properties.getProperty(ConfigConstants.DATABASEFILEPATH);
        String sourceDatabasePort = properties.getProperty(ConfigConstants.SOURCEDATABASEPORT);
        String sourceDatabaseUsername = properties.getProperty(ConfigConstants.SOURCEDATABASEUSERNAME);
        String sourceDatabasePassword = properties.getProperty(ConfigConstants.SOURCEDATABASEPASSWORD);
        String sourceDatabasePath = properties.getProperty(ConfigConstants.SOURCEDATABASEPATH);
        String schemaToMigrate = properties.getProperty(ConfigConstants.SCHEMATOMIGRATE).toUpperCase();
        String migrateSystemSchema = properties.getProperty(ConfigConstants.MIGRATESYSTEMSCHEMA, ConfigConstants.FALSE);
        String targetDatabase = properties.getProperty(ConfigConstants.TARGETDATABASE); 
        String targetDatabasePort = properties.getProperty(ConfigConstants.TARGETDATABASEPORT, XBASEConstants.MONGODEFAULTPORT);
        String targetDatabaseName = properties.getProperty(ConfigConstants.TARGETDATABASENAME);
    	String targetDatabaseUserName = properties.getProperty(ConfigConstants.TARGETDATABASEUSERNAME);
    	String targetDatabasePassword = properties.getProperty(ConfigConstants.TARGETDATABASEPASSWORD); 
    	String embedding = properties.getProperty(ConfigConstants.EMBEDDING, ConfigConstants.FALSE);
    	String migrateIndexes = properties.getProperty(ConfigConstants.MIGRATEINDEXES, ConfigConstants.FALSE);
    	String exportJSONDump = properties.getProperty(ConfigConstants.EXPORTJSONDUMP, ConfigConstants.FALSE);
    	String jsonDumpFilePath = properties.getProperty(ConfigConstants.JSONDUMPFILEPATH);
    	String jsonDumpFileName = properties.getProperty(ConfigConstants.JSONDUMPFILENAME);
    	String inventoryFileName = properties.getProperty(ConfigConstants.INVENTORYFILENAME);
    	String inventoryFilePath = properties.getProperty(ConfigConstants.INVENTORYFILEPATH); 
    	String logFilePath = properties.getProperty(ConfigConstants.LOGFILEPATH);
    	String logFileName = properties.getProperty(ConfigConstants.LOGFILENAME);
        String dataInjectionMode = properties.getProperty(ConfigConstants.DATAINJECTIONMODE, ConfigConstants.FALSE);
    	String dataInjectionRange = properties.getProperty(ConfigConstants.DATAINJECTIONRANGE, "0");
    	 
    	
    	configMap.put(ConfigConstants.MIGRATIONMODE, migrationMode);
    	configMap.put(ConfigConstants.HOSTNAME, hostName);
    	configMap.put(ConfigConstants.SOURCEDATABASE, sourceDatabase);
    	configMap.put(ConfigConstants.DATABASEFILEPATH, databaseFilePath);
    	configMap.put(ConfigConstants.SOURCEDATABASEPORT, sourceDatabasePort);  
    	configMap.put(ConfigConstants.SOURCEDATABASEUSERNAME, sourceDatabaseUsername);
    	configMap.put(ConfigConstants.SOURCEDATABASEPASSWORD, sourceDatabasePassword);
    	configMap.put(ConfigConstants.SOURCEDATABASEPATH, sourceDatabasePath);
    	configMap.put(ConfigConstants.SCHEMATOMIGRATE, schemaToMigrate);
    	configMap.put(ConfigConstants.MIGRATESYSTEMSCHEMA, migrateSystemSchema);
    	configMap.put(ConfigConstants.TARGETDATABASE, targetDatabase);
    	configMap.put(ConfigConstants.TARGETDATABASEPORT, targetDatabasePort);
    	configMap.put(ConfigConstants.TARGETDATABASENAME, targetDatabaseName);
    	configMap.put(ConfigConstants.TARGETDATABASEUSERNAME, targetDatabaseUserName);
    	configMap.put(ConfigConstants.TARGETDATABASEPASSWORD, targetDatabasePassword);
    	configMap.put(ConfigConstants.EMBEDDING, embedding);
    	configMap.put(ConfigConstants.MIGRATEINDEXES, migrateIndexes);
    	configMap.put(ConfigConstants.EXPORTJSONDUMP, exportJSONDump);
    	configMap.put(ConfigConstants.JSONDUMPFILEPATH, jsonDumpFilePath);
    	configMap.put(ConfigConstants.JSONDUMPFILENAME, jsonDumpFileName);
    	configMap.put(ConfigConstants.INVENTORYFILEPATH, inventoryFilePath);
    	configMap.put(ConfigConstants.INVENTORYFILENAME, inventoryFileName);
    	configMap.put(ConfigConstants.LOGFILEPATH, logFilePath);
    	configMap.put(ConfigConstants.LOGFILENAME, logFileName);
    	configMap.put(ConfigConstants.DATAINJECTIONMODE, dataInjectionMode);
    	configMap.put(ConfigConstants.DATAINJECTIONRANGE, dataInjectionRange);
    	
    	configMapInitialized=true;
    	
    	if(EnvironmentSettings.DEBUGMODEV) {
    	  PrintUtil.log(MessageConstants.DEBUGV + XBASEConstants.CONFIGMAP);
    	  PrintUtil.log(PatternConstants.TABSPACING + configMap);
    	}
    	
		return configMap;
	}

	/**
	 * This method fetches source database name (instance name) Eg:orcl
	 * @return DatabaseName
	 */
	private static String fetchDatabaseName() {
		ResultSet rs = OracleQueryExecutor.execute("SELECT " + OraTables.ORA_DATABASE_NAME + " FROM " + OraTables.DUAL);
		String databaseName = null;
		try {
			while(rs.next()) {
				databaseName = rs.getString(1).toUpperCase();
			}
		} catch (SQLException e) {
			InventoryManager.log(MessageConstants.ERROR + MessageConstants.EXCEPTIONWHILE + " fetching Database Name: " + e.getMessage(), MessageType.ERROR);
			e.printStackTrace();
		}
		return databaseName;
	}

	/**
	 * This module will print Configuration Details to Console.
	 * @param configMap
	 */
	static void printConfigDetails(Map<String, String> configMap) {
    
		PrintUtil.log(PatternConstants.LINESEPERATOR + PatternConstants.TABSPACING+ XBASEConstants.VALIDATINGINPUTS + PatternConstants.LINESEPERATOR);
        PrintUtil.log("*** MigrationMode - - - - - - - - - [ " + configMap.get(ConfigConstants.MIGRATIONMODE) + " ]");
        PrintUtil.log("*** SourceDatabase - - - - - - - - -[ " + configMap.get(ConfigConstants.SOURCEDATABASE) + " ]");
        PrintUtil.log("*** SourceDatabasePort - - - - - - -[ " + configMap.get(ConfigConstants.SOURCEDATABASEPORT) + " ]");
        PrintUtil.log("*** SourceDatabaseUsername- - - - - [ " + configMap.get(ConfigConstants.SOURCEDATABASEUSERNAME) + " ]");
        PrintUtil.log("*** SourceDatabasePassword - - - - -[ " + configMap.get(ConfigConstants.SOURCEDATABASEPASSWORD) + " ]");
        PrintUtil.log("*** SourceDatabasePath - - - - - - -[ " + configMap.get(ConfigConstants.SOURCEDATABASEPATH) + " ]");
        PrintUtil.log("*** SchemaToMigrate - - - - - - - - [ " + configMap.get(ConfigConstants.SCHEMATOMIGRATE) + " ]");
        PrintUtil.log("*** MigrateSystemSchema - - - - - - [ " + configMap.get(ConfigConstants.MIGRATESYSTEMSCHEMA) + " ]");
        PrintUtil.log("*** TargetDatabase - - - - - - - - -[ " + configMap.get(ConfigConstants.TARGETDATABASE) + " ]");
        PrintUtil.log("*** TargetDatabasePort- - - - - - - [ " + configMap.get(ConfigConstants.TARGETDATABASEPORT) + " ]");
        PrintUtil.log("*** TargetDatabaseName - - - - - - -[ " + configMap.get(ConfigConstants.TARGETDATABASENAME) + " ]");
        PrintUtil.log("*** TargetDatabaseUserName - - - - -[ " + configMap.get(ConfigConstants.TARGETDATABASEUSERNAME) + " ]");
        PrintUtil.log("*** TargetDatabasePassword - - - - -[ " + configMap.get(ConfigConstants.TARGETDATABASEPASSWORD) + " ]");
        PrintUtil.log("*** Embedded JSON- - - - - - - - - -[ " + configMap.get(ConfigConstants.EMBEDDING) + " ]");
        PrintUtil.log("*** MigrateIndexes - - - - - - - - -[ " + configMap.get(ConfigConstants.MIGRATEINDEXES) + " ]");
        PrintUtil.log("*** ExportJSONDump - - - - - - - - -[ " + configMap.get(ConfigConstants.EXPORTJSONDUMP) + " ]");
        if(Boolean.valueOf(configMap.get(ConfigConstants.EXPORTJSONDUMP))) {
        	PrintUtil.log("*** JSONDumpFilePath - - - - - - - -[ " + configMap.get(ConfigConstants.JSONDUMPFILEPATH)+" ]");
        	PrintUtil.log("*** JSONDumpFileName - - - - - - - -[ " + configMap.get(ConfigConstants.JSONDUMPFILENAME)+" ]");
        }
        PrintUtil.log("*** InventoryFilePath - - - - - - - [ " + configMap.get(ConfigConstants.INVENTORYFILEPATH) + " ]");
        PrintUtil.log("*** InventoryFileName - - - - - - - [ " + configMap.get(ConfigConstants.INVENTORYFILENAME) + " ]");
        PrintUtil.log("*** Operating Sytem - - - - - - - - [ " + System.getProperty(ConfigConstants.OSNAME) + " ]");
        PrintUtil.log("*** DataInjectionMode - - - - - - - [ " + configMap.get(ConfigConstants.DATAINJECTIONMODE) + " ]");
        PrintUtil.log("*** DataInjectionRange - - - - - - -[ " + configMap.get(ConfigConstants.DATAINJECTIONRANGE) + " ]");
        
        PrintUtil.log(PatternConstants.LINESEPERATOR);
        
    	// populateDatabaseName();
    	
        if(EnvironmentSettings.DEBUGMODE) {
    		PrintUtil.log(MessageConstants.INFO + "Debug Mode Enabled" + PatternConstants.LINESEPERATORDOUBLE);
    	}
        
        if(configMap.get(ConfigConstants.SCHEMATOMIGRATE).equals(PatternConstants.ASTERIK)) {
        	if(!configMap.get(ConfigConstants.SOURCEDATABASEUSERNAME).startsWith("SYS")) {
        		System.out.print(MessageConstants.ERROR);
        		PrintUtil.log("To migrate all the schemas, USER running this tool has to be " + XBASEConstants.SYS + " or " + XBASEConstants.SYSTEM);
        		PrintUtil.log(MessageConstants.EXITING);
        		System.exit(1);
        	}
        }
	}

	/**
	 * @param configMap
	 */
   static void populateDatabaseName() {
		String sourceDatabaseName = fetchDatabaseName();
    	configMap.put(ConfigConstants.SOURCEDATABASENAME, sourceDatabaseName);
    	if(null==configMap.get(ConfigConstants.TARGETDATABASENAME)) {
    		configMap.put(ConfigConstants.TARGETDATABASENAME, sourceDatabaseName);
    	}
	}	
	
	static Map<String, String> getConfigMap() {
		if(configMapInitialized)
			return configMap;
		else
			throw new RuntimeException(XBASEConstants.CONFIGMAP + PatternConstants.SPACESEPERATOR + MessageConstants.NOTINITIALIZED);
	}
	
	static Properties getConfigProperties() {
		if(propertiesInitialized)
			return properties;
		else
			throw new RuntimeException(XBASEConstants.PROPERTIES + PatternConstants.SPACESEPERATOR + MessageConstants.NOTINITIALIZED);
	}
	
	static Map<String, String> getConfigProperties(String configFilePath) {
		if(configMap==null)
			try {
				configMap = populateConfigDetails(configFilePath);
			} catch (IOException e) {
				PrintUtil.log(MessageType.ERROR + "IO " + MessageConstants.EXCEPTIONWHILE + "populating config details." + e.getMessage());
				PrintUtil.log(e.toString());
				e.printStackTrace();
			}
		return configMap;	
	}
}
