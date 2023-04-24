/**
 * 
 */
package org.xbase.com.converter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xbase.com.actions.MigratorActions;
import org.xbase.com.constants.MessageConstants;
import org.xbase.com.constants.PatternConstants;
import org.xbase.com.constants.QueryConstants;
import org.xbase.com.executor.OracleQueryExecutor;
import org.xbase.com.manager.InventoryManager;
import org.xbase.com.util.PrintUtil;
import org.xbase.com.util.QueryUtil;

public class EmbedHelper {

	public static JSONArray embed(JSONArray jsonArray, String currentParentTableName, Map<String, String> childTableDetails) {
		
		int rowCount = jsonArray.length();
		for(int currentRowNum=0; currentRowNum<rowCount ; currentRowNum++) {
			JSONObject currentObject = new JSONObject();
			currentObject = jsonArray.getJSONObject(currentRowNum);
			for(String currentTableName : childTableDetails.keySet()) {
				JSONArray currentChildTableEntry = new JSONArray();
				String joinColumnName = childTableDetails.get(currentTableName);
				String currentRowValue = currentObject.getString(joinColumnName); 
				currentChildTableEntry = getTableChildEntry(currentParentTableName, currentTableName, joinColumnName, currentRowValue);
				currentObject.put(currentTableName, currentChildTableEntry);
				jsonArray.put(currentRowNum, currentObject);
			}	
		}
		for(String currentChildTableName : childTableDetails.keySet()) {
			InventoryManager.updateInventory(MigratorActions.COLLECTIONEMBEDDED, currentParentTableName + PatternConstants.DOTSEPERATOR + currentChildTableName);
		}
		return jsonArray;
	}
	
	/**
	 * @param currentParentTableName
	 * @param currentTableName
	 * @param joinColumnName
	 * @param currentRowValue
	 * @return
	 */
	private static JSONArray getTableChildEntry(String currentParentTableName, String currentTableName,
			String joinColumnName, String currentRowJoinValue) {
		JSONArray childEntriesJSONArray = new JSONArray();
		List<String> columnList = new ArrayList<String>();
		columnList = getColumnListofTable(currentTableName);
		columnList.remove(joinColumnName);
		String childEntriesQuery = QueryUtil.getQueryToFindChildEntriesColumns(currentParentTableName, currentTableName, columnList, joinColumnName, currentRowJoinValue);
		ResultSet childEntriesResultSet = OracleQueryExecutor.execute(childEntriesQuery);
		childEntriesJSONArray = TableToJSONConverter.getJSON(childEntriesResultSet);
		return childEntriesJSONArray;
	}

	/**
	 * @param currentTableName
	 * @param columnList
	 */
	private static final List<String> getColumnListofTable(String currentTableName) {
		List<String> columnList = new ArrayList<String>();
		String query = QueryUtil.getQueryToFindAllColumnsOfTable(currentTableName);
		ResultSet colListresultset = OracleQueryExecutor.execute(query);		
		try {
			while(colListresultset.next()) {
				columnList.add(colListresultset.getString(QueryConstants.COLUMN_NAME));
			}
		} catch (SQLException e) {
			PrintUtil.log(MessageConstants.ERROR + MessageConstants.EXCEPTIONWHILE + "fetching column names of table during embedding.");
			e.printStackTrace();
		}
		return columnList;
	}
	
}
