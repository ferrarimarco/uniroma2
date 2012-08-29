package controller.persistance;

public class StorageManagerFactory {

	public static AWSDynamoDBStorageManager createAWSDynamoDBStorageManager(){
		// TODO: fill with paramters
		
		return new AWSDynamoDBStorageManager();
	}
	
}
