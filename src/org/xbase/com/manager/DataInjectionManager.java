/**
 * 
 */
package org.xbase.com.manager;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xbase.com.constants.ConfigConstants;
import org.xbase.com.constants.ObjectConstants;
import org.xbase.com.constants.PatternConstants;
import org.xbase.com.executor.MongoQueryExecutor;
import org.xbase.com.util.PrintUtil;

public class DataInjectionManager {
	/**
	 * @param dataInjectionRanges
	 */
	public static void injectData(String dataInjectionRanges) {
		InventoryManager.startDataInjection();
		PrintUtil.log(PatternConstants.LINEPATTERNASTERIK);
		PrintUtil.log("Initiating " + ConfigConstants.DATAINJECTIONMODE);
		PrintUtil.log(PatternConstants.LINEPATTERNASTERIK);

		String[] injectionRange = dataInjectionRanges.split(",");
		int tableRange = Integer.parseInt(injectionRange[0]);
		int rowRange = Integer.parseInt(injectionRange[1]);
		int columnRange = Integer.parseInt(injectionRange[2]);
		
		MongoQueryExecutor mongoQE = MongoQueryExecutor.getInstance();
		mongoQE.createDatabase(ObjectConstants.INJDATADB);
		
		for (int coll = 1 ; coll <= tableRange ; coll++) {
			
			JSONArray currentCollection = new JSONArray();
			for(int doc=1 ; doc <= rowRange ; doc++) {
			
				JSONObject currentDocument = new JSONObject();
				for(int kvp=1 ; kvp <= columnRange ; kvp++) {
					StringBuilder currentKey = new StringBuilder();
					StringBuilder currentValue = new StringBuilder();
					
					currentKey.append(ObjectConstants.COLLECTION).append(coll); 
					currentKey.append(ObjectConstants.DOCUMENT).append(doc);
					currentKey.append(ObjectConstants.KEY).append(kvp);
					
					currentValue.append(ObjectConstants.COLLECTION).append(coll); 
					currentValue.append(ObjectConstants.DOCUMENT).append(doc);
					currentValue.append(ObjectConstants.VALUE).append(kvp);
					
					currentDocument.put(currentKey.toString(), currentValue.toString());
				}
				
				currentCollection.put(currentDocument);
			}
			mongoQE.createCollectionAndDocumentsInTargetDB(ObjectConstants.INJDATADB, ObjectConstants.COLLECTION+coll, currentCollection);
		}
		InventoryManager.endDataInjection();
	}
	
/*
	public static String getRandomCollectionName() {
		StringBuilder tableName = new StringBuilder();
		tableName.append(ObjectConstants.COLLECTION);
		tableName.append(getTimeStamp());
		return tableName.toString();
	}

	public static String getRandomColumnName() {
		StringBuilder columnName = new StringBuilder();
		columnName.append(ObjectConstants.COLUMN);
		columnName.append(getTimeStamp());
		return columnName.toString();
	}

	public static String getRandomKey() {
		StringBuilder rowData = new StringBuilder();
		rowData.append(ObjectConstants.KEY);
		rowData.append(getTimeStamp());
		return rowData.toString();
	}

	public static String getRandomValue() {
		StringBuilder rowData = new StringBuilder();
		rowData.append(ObjectConstants.KEY);
		rowData.append(getTimeStamp());
		return rowData.toString();
	}
	
	public static String getTimeStamp() {
		return new Timestamp(System.currentTimeMillis()).toString().replaceAll(" ", "_");
	}
*/	
}
