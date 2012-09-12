package controller.persistance;

public class StorageManagerFactory {

	public static PersistanceManager createAWSDynamoDBStorageManager(){
		// TODO: fill with paramters
		
		return new AWSDynamoDBStorageManager();
	}
	
}
