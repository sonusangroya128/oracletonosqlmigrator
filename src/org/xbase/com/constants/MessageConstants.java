/**
 * 
 */
package org.xbase.com.constants;

import org.xbase.com.actions.MessageType;

public interface MessageConstants {

		public static final String CHILDTABLES = "Child Tables";
		public static final String CURRENTQUERY = "Current Query: ";
		public static final String EXCEPTIONWHILE = "Exception while ";
		public static final String EXITING = "Exiting...!";
		public static final String NOSUCHDATABASE = "No Such Database";
		public static final String NOTINITIALIZED =  "Not Yet Initialized";
		
		public static final String DEBUG   = "   -- [" + MessageType.DEBUG + "]   ";
		public static final String DEBUGV  = "   -- [" + MessageType.DEBUGV + "]  ";
		public static final String ERROR   = "   -- [" + MessageType.ERROR + "]   "; 
		public static final String INFO    = "   -- [" + MessageType.INFO + "]    ";
		public static final String WARNING = "   -- [" + MessageType.WARNING + "] ";
		
}
