package controller.persistance;

import java.util.List;

public class StorageManager implements PersistanceManager {
	
	@Override
	public void create(StorageLocation location, List<FieldName> fieldNames, String ...values) {

		PersistanceManager awsDynamoDBStorageManager = getAWSDynamoDBStorageManager();

		awsDynamoDBStorageManager.create(location, fieldNames, values);
	}

	@Override
	public String read(StorageLocation location, FieldName fieldName, String keyValue) {
		PersistanceManager awsDynamoDBStorageManager = getAWSDynamoDBStorageManager();

		return awsDynamoDBStorageManager.read(location, fieldName, keyValue);
	}

	@Override
	public void update(StorageLocation location, FieldName fieldName, String keyValue, String newValue) {

		PersistanceManager awsDynamoDBStorageManager = getAWSDynamoDBStorageManager();

		awsDynamoDBStorageManager.update(location, fieldName, keyValue, newValue);
	}

	@Override
	public void delete(StorageLocation location, String keyValue) {
		PersistanceManager awsDynamoDBStorageManager = getAWSDynamoDBStorageManager();

		awsDynamoDBStorageManager.delete(location, keyValue);
	}

	private AWSDynamoDBStorageManager getAWSDynamoDBStorageManager() {
		return StorageManagerFactory.createAWSDynamoDBStorageManager();
	}

	@Override
	public boolean isPresent(StorageLocation location, FieldName fieldName, String keyValue) {

		PersistanceManager awsDynamoDBStorageManager = getAWSDynamoDBStorageManager();
		
		return awsDynamoDBStorageManager.isPresent(location, fieldName, keyValue);
	}

	@Override
	public void scanAndDeletePop3Messages(String keyUserName) {
		PersistanceManager awsDynamoDBStorageManager = getAWSDynamoDBStorageManager();
		
		awsDynamoDBStorageManager.scanAndDeletePop3Messages(keyUserName);
		
	}

}
