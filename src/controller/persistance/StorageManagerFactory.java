package controller.persistance;

public class StorageManagerFactory {

	public static PersistanceManager createAWSDynamoDBStorageManager(){
		return new AWSDynamoDBStorageManager();
	}
	
}
