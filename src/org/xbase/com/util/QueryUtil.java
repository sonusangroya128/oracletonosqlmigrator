/**
 * 
 */
package org.xbase.com.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xbase.com.constants.MessageConstants;
import org.xbase.com.constants.PatternConstants;
import org.xbase.com.constants.QueryConstants;
import org.xbase.com.executor.OracleQueryExecutor;

public final class QueryUtil {
	/*
	 * This method prepares query using PreparedStatement
	 */
	public static final String getQueryToFindDependentTables(final String parentTableName) {
		StringBuilder query = new StringBuilder();
		query.append("SELECT FK.OWNER, FK.TABLE_NAME, COL.COLUMN_NAME FROM ALL_CONSTRAINTS PK JOIN ALL_CONSTRAINTS FK");
		query.append(" ON PK.CONSTRAINT_NAME=FK.R_CONSTRAINT_NAME AND FK.CONSTRAINT_TYPE='R' JOIN ALL_CONS_COLUMNS COL ");
		query.append("ON FK.CONSTRAINT_NAME=COL.CONSTRAINT_NAME WHERE PK.TABLE_NAME='");
		query.append(parentTableName);
		query.append("' AND PK.CONSTRAINT_TYPE='P'");
		return query.toString();
	}
	
	public static final String getQueryToFindDependentIndexes(final String tableName) {
		StringBuilder query = new StringBuilder();
		return query.toString();
	}
	
	public static final String getQueryToFindListOfParentTables(final String childTableName) {
		StringBuilder query = new StringBuilder();
		query.append("SELECT C_PK.TABLE_NAME FROM USER_CONS_COLUMNS A JOIN USER_CONSTRAINTS C ");
		query.append("ON A.OWNER=C.OWNER AND A.CONSTRAINT_NAME=C.CONSTRAINT_NAME JOIN ALL_CONSTRAINTS C_PK ");
		query.append("ON C.R_CONSTRAINT_NAME=C_PK.CONSTRAINT_NAME WHERE C.CONSTRAINT_TYPE='R' AND A.TABLE_NAME='");
		query.append(childTableName);
		query.append("'");
		return query.toString();
	}

	/**
	 * @param currentTableName
	 * @return
	 */
	public static final boolean isChildTable(final String currentTableName) {
		String query = getQueryToFindListOfParentTables(currentTableName);
		ResultSet rs = OracleQueryExecutor.execute(query);
		List<String> parentTables = new ArrayList<String>();
		try {
			while(rs.next()) {
				parentTables.add(rs.getString(QueryConstants.TABLE_NAME));
			}
		} catch (SQLException e) {
			PrintUtil.log(MessageConstants.ERROR + MessageConstants.EXCEPTIONWHILE + "fetching parent table list.");
			e.printStackTrace();
		}
		if(parentTables.contains(currentTableName)) {
			parentTables.remove(currentTableName);
		}
		if(parentTables.size()>0) {
			return true;
		}
		return false;
	}
	
	/**
	 * @param currentTableName
	 * @return
	 */
	public static int findNumberofParentTables(String currentTableName) {
		String findParentTablequery = QueryUtil.getQueryToFindListOfParentTables(currentTableName);
		ResultSet parentTablesResultSet = OracleQueryExecutor.execute(findParentTablequery);
		List<String> parentTables = new ArrayList<String>();
		try {
			while(parentTablesResultSet.next())
				parentTables.add(parentTablesResultSet.getString(QueryConstants.TABLE_NAME));
		} catch (SQLException e1) {
			PrintUtil.log(MessageConstants.ERROR + MessageConstants.EXCEPTIONWHILE + " getting row count of parent tables." );
			e1.printStackTrace();
		}
		if(parentTables.contains(currentTableName)) {
			parentTables.remove(currentTableName);
		}
		return parentTables.size();
	}
	
	public static final String getQueryToFindChildTables(String parentTableName) {
		StringBuilder query = new StringBuilder();
		query.append("SELECT FK.TABLE_NAME, COL.COLUMN_NAME FROM USER_CONSTRAINTS PK JOIN USER_CONSTRAINTS FK ");
		query.append("ON PK.CONSTRAINT_NAME=FK.R_CONSTRAINT_NAME AND FK.CONSTRAINT_TYPE='R' JOIN ALL_CONS_COLUMNS COL ");
		query.append("ON FK.CONSTRAINT_NAME=COL.CONSTRAINT_NAME ");
		query.append("WHERE PK.TABLE_NAME='");
		query.append(parentTableName);
		query.append("' AND PK.CONSTRAINT_TYPE='P'");
		return query.toString();
	}

	/**
	 * @param currentTableName
	 * @return
	 */
	public static Map<String, String> childTableDetails(String parentTableName) {
		Map<String, String> childTableDetails = new HashMap<String, String>();
		String query = getQueryToFindChildTables(parentTableName);
		ResultSet resultset = OracleQueryExecutor.execute(query);
		try {
			while(resultset.next()) {
				String tableName = resultset.getString(QueryConstants.TABLE_NAME);
				String columnName = resultset.getString(QueryConstants.COLUMN_NAME);
				childTableDetails.put(tableName, columnName);
			}
		} catch (SQLException e) {
			PrintUtil.log(MessageConstants.ERROR + MessageConstants.EXCEPTIONWHILE + " fetching child table details");
			e.printStackTrace();
		}
		return childTableDetails;
	}
	
	public static final String getQueryToFindAllColumnsOfTable(String tableName) {
		StringBuilder query = new StringBuilder();
		query.append("SELECT COLUMN_NAME FROM USER_TAB_COLUMNS WHERE TABLE_NAME='");
		query.append(tableName);
		query.append("'");
		
		return query.toString();
	}

	/**
	 * @param currentTableName
	 * @param columnList
	 * @param joinColumnName 
	 * @param currentRowJoinValue 
	 * @return
	 */
	public static final String getQueryToFindChildEntriesColumns(String parentTableName, String currentTableName, List<String> columnList, String joinColumnName, String currentRowJoinValue) {
		StringBuilder query = new StringBuilder();
		query.append("SELECT ");
		int columnListSize = columnList.size(); 
		for(int i=0; i<columnListSize ; i++) {
			query.append(currentTableName);
			query.append(PatternConstants.DOTSEPERATOR);
			query.append(columnList.get(i));
			if(i<columnListSize-1) {
				query.append(", ");
			}
		}
		query.append(" FROM ").append(currentTableName);
		query.append(" JOIN ");
		query.append(parentTableName);
		query.append(" ON ");
		query.append(parentTableName);
		query.append(PatternConstants.DOTSEPERATOR);
		query.append(joinColumnName);
		query.append("=");
		query.append(currentTableName);
		query.append(PatternConstants.DOTSEPERATOR);
		query.append(joinColumnName);
		query.append(" WHERE ");
		query.append(parentTableName);
		query.append(PatternConstants.DOTSEPERATOR);
		query.append(joinColumnName);
		query.append("=");
		query.append(currentRowJoinValue);
		
		return query.toString();
	}
	
	public static final String getQueryToListAColumn(final String tableName, final String columnName) {
		StringBuilder query = new StringBuilder();
		query.append("SELECT ");		
		query.append(columnName);
		query.append(" FROM ");
		query.append(tableName);
		
		return query.toString();
	}

	/**
	 * @param currentTableName
	 * @return
	 */
	public static final String getQueryToFindListofIndexes(final String tableName) {
		StringBuilder query = new StringBuilder();
		 // Sample -> SELECT INDEX_NAME, INDEX_TYPE, TABLE_NAME, UNIQUENESS FROM USER_INDEXES WHERE TABLE_NAME='HUNTING';
		query.append("SELECT INDEX_NAME, INDEX_TYPE, TABLE_NAME, UNIQUENESS FROM USER_INDEXES WHERE TABLE_NAME='");
		query.append(tableName);
		query.append("'");
		return query.toString();
	}

	/**
	 * @param currentTableName
	 * @param currentIndexName
	 * @return
	 */
	public static final String getQueryToFindColumnsInIndex(String currentTableName, String currentIndexName) {
		StringBuilder query = new StringBuilder();
		query.append("SELECT COLUMN_NAME FROM USER_IND_COLUMNS WHERE TABLE_NAME='");
		query.append(currentTableName);
		query.append("' AND INDEX_NAME='");
		query.append(currentIndexName);
		query.append("'");
		return query.toString();
	}
	
}
