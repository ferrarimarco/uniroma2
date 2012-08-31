package controller.persistance;

import java.util.List;

public class StorageManager implements PersistanceManager {
	
	@Override
	public void create(StorageLocation location, List<FieldName> fieldNames, String ...values) {
		// TODO: handle creation if the record is already there

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

		// TODO: handle creation if the record does not exists

		PersistanceManager awsDynamoDBStorageManager = getAWSDynamoDBStorageManager();

		awsDynamoDBStorageManager.update(location, fieldName, keyValue, newValue);
	}

	@Override
	public void delete(StorageLocation location, FieldName fieldName, String keyValue) {
		PersistanceManager awsDynamoDBStorageManager = getAWSDynamoDBStorageManager();

		awsDynamoDBStorageManager.delete(location, fieldName, keyValue);
	}

	private AWSDynamoDBStorageManager getAWSDynamoDBStorageManager() {
		return StorageManagerFactory.createAWSDynamoDBStorageManager();
	}

	@Override
	public boolean isPresent(StorageLocation location, FieldName fieldName, String keyValue) {

		PersistanceManager awsDynamoDBStorageManager = getAWSDynamoDBStorageManager();
		
		return awsDynamoDBStorageManager.isPresent(location, fieldName, keyValue);
	}

}
