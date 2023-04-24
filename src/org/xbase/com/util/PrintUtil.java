/**
 * 
 */
package org.xbase.com.util;

import static java.lang.System.out;

import org.xbase.com.constants.PatternConstants;

public class PrintUtil {
	
	private PrintUtil(){}
	
	private static StringBuilder printLog = new StringBuilder();
	
	public static void log(String message) {
		printLog.append(message + PatternConstants.LINESEPERATOR);
		out.println(message);
	}
	
	public static String getLog() {
		return printLog.toString();
	}
	
}
