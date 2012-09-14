package controller.persistance;

public class PersistanceManagerFactory {

	public static PersistanceManager createAWSDynamoDBStorageManager() {
		return new AWSDynamoDBStorageManager();
	}

}
