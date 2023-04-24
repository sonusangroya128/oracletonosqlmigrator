/**
 * 
 */
package org.xbase.com.migrator;

import java.util.Map;

import org.json.JSONObject;
import org.json.XML;
import org.xbase.com.constants.ConfigConstants;
import org.xbase.com.constants.MessageConstants;
import org.xbase.com.util.IOUtil;
import org.xbase.com.util.PrintUtil;

public class XMLMigrator {

	
	public static void migrate(final Map<String, String> configMap) {
		String fileContent = null;
		 try {
			fileContent = IOUtil.readFileAsString(configMap.get(ConfigConstants.DATABASEFILEPATH));
		} catch (Exception e) {
			PrintUtil.log(MessageConstants.ERROR + MessageConstants.EXCEPTIONWHILE + " reading XML database");
			PrintUtil.log(e.getMessage());
			e.printStackTrace();
		}
		 
		 JSONObject jsonDatabase = new JSONObject();
		 jsonDatabase = XML.toJSONObject(fileContent);
		 JSONMigrator.migrate(configMap, jsonDatabase);
		 
	}
	
}
