package org.xbase.com.executor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.xbase.com.constants.MessageConstants;
import org.xbase.com.constants.PatternConstants;
import org.xbase.com.environment.EnvironmentSettings;
import org.xbase.com.manager.OracleConnectionManager;
import org.xbase.com.util.PrintUtil;

public class OracleQueryExecutor {

	public static ResultSet execute(Connection conn, final String query) {
		PreparedStatement pstmt = null;
		ResultSet resultSet = null;
		try {
			pstmt = conn.prepareStatement(query);
			if(EnvironmentSettings.DEBUGMODE) {
				PrintUtil.log(MessageConstants.DEBUG + OracleQueryExecutor.class.getName() + PatternConstants.DATASEPERATOR + query);
			}
			resultSet = pstmt.executeQuery();
		} catch (SQLException sqle) {
			PrintUtil.log(sqle.getMessage());
			sqle.printStackTrace();
		}
		return resultSet;
	}
	
	public static ResultSet execute(String query) {
		Connection conn = OracleConnectionManager.getOracleDBConnection();
		return execute(conn, query);
	}	
	
	public static ResultSetMetaData getResultSetMetaData(ResultSet resultSet) {
		ResultSetMetaData resultSetMetadata = null;
		try {
			resultSetMetadata = resultSet.getMetaData();
		}
		catch(SQLException sqle) {
			PrintUtil.log(MessageConstants.ERROR + MessageConstants.EXCEPTIONWHILE + " getting ResultSetMetaData: " + sqle.getMessage());
			sqle.printStackTrace();
		}
		return resultSetMetadata;
	}
	
}
