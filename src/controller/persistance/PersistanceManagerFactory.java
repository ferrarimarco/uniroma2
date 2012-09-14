package controller.persistance;

public class PersistanceManagerFactory {

	public static PersistanceManager createAWSDynamoDBStorageManager() {
		return new AWSDynamoDBStorageManager();
	}
	
	public static PersistanceManager createSMTPTempMessageStorageManager(){
		return new SMTPTempMessageStorageManager();
	}
}
