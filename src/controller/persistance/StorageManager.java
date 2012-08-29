package controller.persistance;

public class StorageManager implements PersistanceManager{

	@Override
	public void create(StorageLocation location, String id, String value) {
		PersistanceManager awsDynamoDBStorageManager = getAWSDynamoDBStorageManager();
		
		awsDynamoDBStorageManager.create(location, id, value);
	}

	@Override
	public String read(StorageLocation location, String id) {
		PersistanceManager awsDynamoDBStorageManager = getAWSDynamoDBStorageManager();
		
		// TODO: if not found return an empty ("") string
		
		return awsDynamoDBStorageManager.read(location, id);
	}

	@Override
	public void update(StorageLocation location, String id, String value) {
		PersistanceManager awsDynamoDBStorageManager = getAWSDynamoDBStorageManager();
		
		awsDynamoDBStorageManager.update(location, id, value);
	}

	@Override
	public void delete(StorageLocation location, String id) {
		PersistanceManager awsDynamoDBStorageManager = getAWSDynamoDBStorageManager();
		
		awsDynamoDBStorageManager.delete(location, id);
	}
	
	private AWSDynamoDBStorageManager getAWSDynamoDBStorageManager(){
		return StorageManagerFactory.createAWSDynamoDBStorageManager();
	}
	
	
}
