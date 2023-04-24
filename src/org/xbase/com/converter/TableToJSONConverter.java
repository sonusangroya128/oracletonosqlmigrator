package org.xbase.com.converter;

import static java.lang.System.out;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xbase.com.constants.MessageConstants;
import org.xbase.com.constants.XBASEConstants;
import org.xbase.com.constants.PatternConstants;
import org.xbase.com.environment.EnvironmentSettings;
import org.xbase.com.executor.OracleQueryExecutor;
import org.xbase.com.util.PrintUtil;

public class TableToJSONConverter {

	/**
	 * This utility method will convert ResultSet into its equivalent JSONArray
	 * @param resultSet
	 * @return JSONArray
	 * @author VAMSI KRISHNA MYALAPALLI (vamsikrishna.vasu@gmail.com)
	 */
	public static JSONArray getJSON(ResultSet resultSet) {
		JSONArray jsonArray = new JSONArray();
		ResultSetMetaData resultSetMetaData = OracleQueryExecutor.getResultSetMetaData(resultSet);

		try {
			int columnCount = resultSetMetaData.getColumnCount();
			
			if(EnvironmentSettings.DEBUGMODE) {
				PrintUtil.log(MessageConstants.DEBUG + XBASEConstants.COLUMNCOUNT + PatternConstants.DATASEPERATOR + columnCount + PatternConstants.LINESEPERATOR);
			}
			if(EnvironmentSettings.DEBUGMODEV) {
				PrintUtil.log(MessageConstants.DEBUGV);
			}
			for (int i = 1; i <= columnCount; i++) {
				String currentColumnName = resultSetMetaData.getColumnName(i);
				// This will print column name and following code will print info in columns
				if (EnvironmentSettings.DEBUGMODEV) {	
					out.print(PatternConstants.TABSPACING + currentColumnName + " | ");
				}
			}
			
			while (resultSet.next()) {
				JSONObject jsonObject = new JSONObject();
				for (int i = 1; i <= columnCount; i++) {
					String currentValue = resultSet.getString(i);
					if(null==currentValue) {
						currentValue = "";
					}
					jsonObject.put(resultSetMetaData.getColumnName(i), currentValue);
					// This will print info present in columns
					if(EnvironmentSettings.DEBUGMODEV)
						out.print(PatternConstants.TABSPACING + currentValue + " | ");
				}
				jsonArray.put(jsonObject);
				if(EnvironmentSettings.DEBUGMODEV)
					PrintUtil.log(PatternConstants.LINESEPERATOR);
			}
			
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
		
		return jsonArray;
	}

}
