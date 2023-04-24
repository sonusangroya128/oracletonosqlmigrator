/**
 * 
 */
package org.xbase.com.manager;

import java.util.Map;

import org.xbase.com.migrator.XMLMigrator;

public class XMLManager {

	/**
	 * @param configMap
	 */
	public static void migrate(Map<String, String> configMap) {
		InventoryManager.startMigration();
		XMLMigrator.migrate(configMap);
		InventoryManager.endMigration();
	}

	

}
