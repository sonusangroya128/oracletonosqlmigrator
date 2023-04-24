/**
 * 
 */
package org.xbase.com.migrator;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.xbase.com.constants.ConfigConstants;
import org.xbase.com.constants.MessageConstants;
import org.xbase.com.constants.XBASEConstants;
import org.xbase.com.constants.ObjectConstants;
import org.xbase.com.constants.OraTables;
import org.xbase.com.constants.PatternConstants;
import org.xbase.com.constants.QueryConstants;
import org.xbase.com.converter.EmbedHelper;
import org.xbase.com.converter.TableToJSONConverter;
import org.xbase.com.environment.EnvironmentSettings;
import org.xbase.com.executor.MongoQueryExecutor;
import org.xbase.com.executor.OracleQueryExecutor;
import org.xbase.com.object.Table;
import org.xbase.com.util.PrintUtil;
import org.xbase.com.util.QueryUtil;

public class TableMigrator {

	private TableMigrator() {}

	private static List<String> childTables = new ArrayList<String>();

	public static void transformAndMigrate(Connection conn, Map<String, String> configMap) {

		boolean embeddingEnabled = Boolean.valueOf(configMap.get(ConfigConstants.EMBEDDING));
		List<String> schemaList = new ArrayList<String>();
		MongoQueryExecutor mongoQE = MongoQueryExecutor.getInstance();
		String schemaToMigrate = configMap.get(ConfigConstants.SCHEMATOMIGRATE);
		String targetDatabaseName = configMap.get(ConfigConstants.TARGETDATABASENAME);

		System.out.print(MessageConstants.INFO + ConfigConstants.SCHEMATOMIGRATE + PatternConstants.DATASEPERATOR
				+ PatternConstants.SINGLEQUOTE);
		if (schemaToMigrate.equals("*")) {
			PrintUtil.log("All");
			schemaList = populateListFromQuery(conn, ObjectConstants.SCHEMA, schemaToMigrate);
		} else {
			schemaList.add(schemaToMigrate);
			PrintUtil.log(schemaToMigrate + PatternConstants.SINGLEQUOTE);
		}

		mongoQE.createDatabase(targetDatabaseName);

		for (String currentSchema : schemaList) {
			List<String> tableList = new ArrayList<String>();
			tableList = populateListFromQuery(conn, ObjectConstants.TABLE, schemaToMigrate);
			PrintUtil.log(MessageConstants.INFO + XBASEConstants.TABLESTOMIGRATE + PatternConstants.DATASEPERATOR
					+ tableList);
			for (String currentTableName : tableList) {
				Table currentTable = new Table(currentTableName);
				String currentAbsTableName = currentSchema + PatternConstants.DOTSEPERATOR + currentTableName;
				PrintUtil.log(MessageConstants.INFO + "Current Table: " + currentAbsTableName);
				currentTable.setRowCount(QueryUtil.findNumberofParentTables(currentTable.getName()));
				if (currentTable.getRowCount() > 0) {
					PrintUtil.log(MessageConstants.DEBUG + currentAbsTableName + " has a parent.");
					childTables.add(currentAbsTableName);
				}
				if (childTables.contains(currentAbsTableName) && embeddingEnabled) {
					PrintUtil.log(PatternConstants.LINESEPERATOR);
					continue;
				}
				String query = QueryConstants.SIMPLEFTS + currentAbsTableName;
				ResultSet resultSet = OracleQueryExecutor.execute(conn, query);
				JSONArray jsonArray = TableToJSONConverter.getJSON(resultSet);

				Map<String, String> childTableDetails = QueryUtil.childTableDetails(currentTableName);

				boolean hasChildTable = childTableDetails.size() > 0 ? true : false;

				// If 'hasChildTable' is set to true, then we need to embed child entries
				if (hasChildTable) {
					if (EnvironmentSettings.DEBUGMODE) {
						PrintUtil.log(MessageConstants.DEBUG + currentTableName + " has a child table");
						PrintUtil.log(MessageConstants.DEBUG + "Child Table Details: " + childTableDetails);
					}
					if (embeddingEnabled) {
						jsonArray = EmbedHelper.embed(jsonArray, currentTableName, childTableDetails);
					}
				}

				int currentRowCount = jsonArray.length();
				if (EnvironmentSettings.DEBUGMODEV) {
					PrintUtil.log(MessageConstants.DEBUGV + "Row Count: " + currentRowCount);
					if (currentRowCount > 0)
						PrintUtil.log(PatternConstants.LINESEPERATOR + jsonArray.toString(8));
				}
				mongoQE.createCollectionAndDocumentsInTargetDB(targetDatabaseName, currentTableName, jsonArray);
				IndexMigrator.migrateIndexes(targetDatabaseName, currentTableName);
				PrintUtil.log(PatternConstants.LINESEPERATOR);
			}
		}
		PrintUtil.log(PatternConstants.LINESEPERATOR);
		PrintUtil.log(
				MessageConstants.DEBUG + MessageConstants.CHILDTABLES + PatternConstants.DATASEPERATOR + childTables);
	}


	/**
	 * @param conn
	 * @return
	 */
	private static List<String> populateListFromQuery(Connection conn, String objectType, String schemaToMigrate) {
		List<String> schemaList = new ArrayList<String>();
		String query = null;
		if (ObjectConstants.SCHEMA.equals(objectType)) {
			query = "SELECT USERNAME FROM " + OraTables.DBA_USERS;
		} else if (ObjectConstants.TABLE.equals(objectType)) {
			query = "SELECT TABLE_NAME FROM " + OraTables.ALL_TABLES + " WHERE OWNER='" + schemaToMigrate + "'";
		} else {
			throw new RuntimeException("Unknown Object Type. Cannot populate Schema/Table List");
		}
		ResultSet resultSet = OracleQueryExecutor.execute(conn, query);
		try {
			while (resultSet.next())
				schemaList.add(resultSet.getString(1).toUpperCase());
		} catch (SQLException e) {
			PrintUtil.log(MessageConstants.ERROR + MessageConstants.EXCEPTIONWHILE + "finding schema list: "
					+ e.getMessage());
			e.printStackTrace();
		}
		return schemaList;
	}
}
