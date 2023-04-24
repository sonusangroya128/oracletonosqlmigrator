/**
 * 
 */
package org.xbase.com.migrator;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.xbase.com.constants.MessageConstants;
import org.xbase.com.constants.QueryConstants;
import org.xbase.com.executor.MongoQueryExecutor;
import org.xbase.com.executor.OracleQueryExecutor;
import org.xbase.com.object.Index;
import org.xbase.com.util.PrintUtil;
import org.xbase.com.util.QueryUtil;


public class IndexMigrator {
	/**
	 * @param mongoQE 
	 * @param currentTableName
	 */
	static void migrateIndexes(String targetDatabaseName, String currentTableName) {
		MongoQueryExecutor mongoQE = MongoQueryExecutor.getInstance();
		String queryToFindIndxList = QueryUtil.getQueryToFindListofIndexes(currentTableName);
		ResultSet resultSet = OracleQueryExecutor.execute(queryToFindIndxList);
		try {
			while(resultSet.next()) {
				Index currentIndex = new Index();
				currentIndex.setIndexName(resultSet.getString(QueryConstants.INDEX_NAME));
				String indexType = resultSet.getString(QueryConstants.INDEX_TYPE);
				if(QueryConstants.NORMAL.equals(indexType)) {
					currentIndex.setReverse(false);
				}
				else if(QueryConstants.NORMAL_REV.equals(indexType)) {
					currentIndex.setReverse(true);
				}
				else {
					throw new RuntimeException("Unknown Index Type");
				}
				String uniqueness = resultSet.getString(QueryConstants.UNIQUENESS);
				
				if(QueryConstants.NONUNIQUE.equals(uniqueness)) {
					currentIndex.setUnique(false);
				}
				else {
					currentIndex.setUnique(true);
				}
				String queryToFindColList = QueryUtil.getQueryToFindColumnsInIndex(currentTableName, currentIndex.getIndexName());
				ResultSet colListResultSet = OracleQueryExecutor.execute(queryToFindColList);
				while(colListResultSet.next()) {
					String currentColumnName = colListResultSet.getString(QueryConstants.COLUMN_NAME);
					currentIndex.addColumn(currentColumnName);
				}
				mongoQE.createIndex(targetDatabaseName, currentTableName, currentIndex);
			}
		} catch (SQLException e) {
			PrintUtil.log(MessageConstants.ERROR + MessageConstants.EXCEPTIONWHILE + "fetching indexes for "+ currentTableName);
			PrintUtil.log(e.getMessage());
			e.printStackTrace();
		}
	}
}
