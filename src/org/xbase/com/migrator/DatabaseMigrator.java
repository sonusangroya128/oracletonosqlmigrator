/**
 * 
 */
package org.xbase.com.migrator;

import java.sql.Connection;
import java.util.Map;

import org.xbase.com.manager.InventoryManager;

public class DatabaseMigrator{
	
	public static void migrate(Connection conn, Map<String, String> configMap) {
		InventoryManager.startMigration();
		TableMigrator.transformAndMigrate(conn, configMap);
		InventoryManager.endMigration();
	}
	
}
