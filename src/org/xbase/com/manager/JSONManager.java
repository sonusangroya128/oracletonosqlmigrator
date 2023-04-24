/**
 * 
 */
package org.xbase.com.manager;

import java.util.Map;

import org.json.JSONObject;
import org.xbase.com.constants.ConfigConstants;
import org.xbase.com.migrator.JSONMigrator;
import org.xbase.com.util.IOUtil;

public class JSONManager {

	/**
	 * @param configMap
	 */
	public static void migrate(Map<String, String> configMap) {
		InventoryManager.startMigration();
		String fileContent = IOUtil.readFileAsString(configMap.get(ConfigConstants.DATABASEFILEPATH));
		JSONObject jsonDatabase = new JSONObject(fileContent);
		JSONMigrator.migrate(configMap, jsonDatabase);
		InventoryManager.endMigration();
	}

}
