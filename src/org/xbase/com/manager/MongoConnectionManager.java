package org.xbase.com.manager;

import org.xbase.com.constants.MessageConstants;
import org.xbase.com.constants.XBASEConstants;
import org.xbase.com.util.PrintUtil;

import com.mongodb.MongoClient;

public class MongoConnectionManager {
	
	private static MongoConnectionManager mongoConnectionManager = new MongoConnectionManager();

	private MongoConnectionManager() {}
	
	public static MongoConnectionManager getInstance() {
		return mongoConnectionManager;
	}
	
	public MongoClient getMongoClientHandle() {
		MongoClient mongo = null;
		try {
			mongo = new MongoClient(XBASEConstants.LOCALHOST, Integer.parseInt(XBASEConstants.MONGODEFAULTPORT));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		PrintUtil.log(MessageConstants.INFO + "Connected to the Mongo database...\n");
		return mongo;
	}
}
