package org.xbase.com.executor;

import org.bson.Document;
import org.json.JSONArray;
import org.xbase.com.actions.MigratorActions;
import org.xbase.com.constants.MessageConstants;
import org.xbase.com.constants.MongoQueryConstants;
import org.xbase.com.constants.PatternConstants;
import org.xbase.com.constants.QueryConstants;
import org.xbase.com.environment.EnvironmentSettings;
import org.xbase.com.manager.InventoryManager;
import org.xbase.com.manager.MongoConnectionManager;
import org.xbase.com.object.Index;
import org.xbase.com.object.View;
import org.xbase.com.util.PrintUtil;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.MongoCommandException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;

public class MongoQueryExecutor {
		
	private static DB db;
	private static MongoDatabase mongoDatabase;
	private static MongoClient mongoClient = MongoConnectionManager.getInstance().getMongoClientHandle();
	private static MongoQueryExecutor mongoQueryExecutor = new MongoQueryExecutor();
	
	private MongoQueryExecutor() {}		
	
	public static MongoQueryExecutor getInstance() {
		return mongoQueryExecutor;
	}
	/*
	 * This method will create Database
	 */
	public void createDatabase(String databaseName) {
		mongoDatabase = mongoClient.getDatabase(databaseName);
		if(EnvironmentSettings.DEBUGMODE) {
			PrintUtil.log(MessageConstants.INFO + QueryConstants.DATABASECREATED + PatternConstants.DATASEPERATOR + databaseName);
		}
		InventoryManager.updateInventory(MigratorActions.DATABASECREATED, databaseName);
	}
	
	/*
	 * This method will create Collection/Table in Database
	 */
	public void createCollection(String databaseName, String collectionName) {	
		mongoDatabase = mongoClient.getDatabase(databaseName);
		mongoDatabase.createCollection(collectionName);
		if(EnvironmentSettings.DEBUGMODE) {
			//PrintUtil.log("Current Collection Name:\t" + db.getCollectionNames());
			PrintUtil.log(MessageConstants.INFO + MongoQueryConstants.COLLECTIONCREATED + PatternConstants.DATASEPERATOR + collectionName);
		}
		InventoryManager.updateInventory(MigratorActions.COLLECTIONCREATED, collectionName);
	}
	
	/*
	 * This method will create Documents/Records in target Collection/Table
	 */
	public void createDocuments(String databaseName, String collectionName, JSONArray documents) {
		MongoCollection<Document> collection = mongoDatabase.getCollection(collectionName);
		
		for(int i=0 ; i<documents.length() ; i++) {
		   collection.insertOne(Document.parse(documents.get(i).toString()));
		}
	}
	
	/**
	 * @param mongoQE
	 * @param targetDatabaseName
	 * @param currentTableName
	 * @param jsonArray
	 */
	public void createCollectionAndDocumentsInTargetDB(String targetDatabaseName, String currentTableName, JSONArray jsonArray) {
		MongoQueryExecutor mongoQE = MongoQueryExecutor.getInstance();
		try {
			mongoQE.createCollection(targetDatabaseName, currentTableName);
			mongoQE.createDocuments(targetDatabaseName, currentTableName, jsonArray);
			// mongoQE.printCollection(databaseName, collectionName);
		} catch (MongoCommandException mce) {
			PrintUtil.log(MessageConstants.ERROR + MessageConstants.EXCEPTIONWHILE + "creating object in Mongo Database");
			mce.printStackTrace();
		}
	}
	
	/*
	 * This method will create Documents/Records in target Collection/Table
	 */
	public void createIndex(String databaseName, String collectionName, Index currentIndex) {
		mongoDatabase = mongoClient.getDatabase(databaseName);
		MongoCollection<Document> collection = mongoDatabase.getCollection(collectionName);
		IndexOptions indexOptions = new IndexOptions();
		indexOptions.unique(currentIndex.isUniqueIndex());
		indexOptions.name(currentIndex.getIndexName());
		if(currentIndex.isReverseIndex()) {
			collection.createIndex(Indexes.descending(currentIndex.getColumns()), indexOptions);
		}
		else {
			collection.createIndex(Indexes.ascending(currentIndex.getColumns()), indexOptions);
		}	
		InventoryManager.updateInventory(MigratorActions.INDEXCREATED, currentIndex.getIndexName());
	}	
	
	public void listCollectionIndexes(String collectionName) {
		MongoCollection<Document> collection = mongoDatabase.getCollection(collectionName);
		for (Document index : collection.listIndexes()) {
		    System.out.println(index.toJson());
		}
	}
	
	/*
	 * This method will print Documents/Records in target Collection/Table
	 */
	public void printCollection(String databaseName, String collectionName) {
		// db = mongo.getDB(databaseName);
		mongoDatabase = mongoClient.getDatabase(databaseName);
		MongoCollection<Document> collection = mongoDatabase.getCollection(collectionName);
		PrintUtil.log(collection.toString());
	}

	public void dropCollection(String databaseName, String collectionName)
	{
		mongoDatabase = mongoClient.getDatabase(databaseName);	
		DBCollection collection = db.getCollection(collectionName);
		collection.drop();
		if(EnvironmentSettings.DEBUGMODE) {
			PrintUtil.log("Deleting Collection:\t" + collectionName);
		}
		InventoryManager.updateInventory(MigratorActions.COLLECTIONDELETED, collectionName);
	}
	
	/*
	 * This method will createView
	 */
	public void createView(String databaseName, String collectionName, View currentView) {
		mongoDatabase = mongoClient.getDatabase(databaseName);
		// MongoCollection<Document> collection = mongoDatabase.getCollection(collectionName);
		
		
		
		
		InventoryManager.updateInventory(MigratorActions.VIEWCREATED, currentView.getViewName());
	}
	
	/*
	 * This method will drop View
	 */
	public void dropView(String databaseName, String collectionName, View currentView) {
		mongoDatabase = mongoClient.getDatabase(databaseName);
		
		
		InventoryManager.updateInventory(MigratorActions.VIEWDELETED, currentView.getViewName());
	}
	
}
