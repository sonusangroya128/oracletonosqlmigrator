/**
 * 
 */
package org.xbase.com.migrator;

import java.util.Iterator;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xbase.com.constants.ConfigConstants;
import org.xbase.com.executor.MongoQueryExecutor;


public class JSONMigrator {
	
	public static void migrate(final Map<String, String> configMap, JSONObject jsonDatabase) {
		
		 Iterator<?> tables = jsonDatabase.keys();
		 MongoQueryExecutor mongoQE = MongoQueryExecutor.getInstance();
		 String targetDatabaseName = configMap.get(ConfigConstants.TARGETDATABASENAME);
		 mongoQE.createDatabase(targetDatabaseName);
		 while(tables.hasNext()) {
			 String currentTableName = (String) tables.next();
			 JSONArray records = new JSONArray();
			 JSONObject recordsObject = (JSONObject) jsonDatabase.get(currentTableName);
			 String recordsObjectName = null;
			 if(recordsObject.keys().hasNext()) {
				 recordsObjectName = (String)recordsObject.keys().next();
			 }
			 records = recordsObject.getJSONArray(recordsObjectName);
			 
			 mongoQE.createCollectionAndDocumentsInTargetDB(targetDatabaseName, currentTableName, records);
		 }		
	}

}
