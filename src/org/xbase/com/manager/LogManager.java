/**
 * 
 */
package org.xbase.com.manager;

import org.xbase.com.constants.MessageConstants;
import org.xbase.com.util.IOUtil;

public class LogManager {

	private LogManager() {}

	public static void logMigration(String path, String fileName, String content) {
		IOUtil.writeToFile(path, fileName, content);
		System.out.println(MessageConstants.DEBUG + "Log file generated. " + path + fileName);
	}
	
}