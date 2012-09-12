package controller.persistance;

import java.util.List;

public class StorageManager implements PersistanceManager {

	@Override
	public void create(StorageLocation location, List<FieldName> fieldNames, String... values) {

		PersistanceManager awsDynamoDBStorageManager = getAWSDynamoDBStorageManager();

		awsDynamoDBStorageManager.create(location, fieldNames, values);
	}

	@Override
	public String read(StorageLocation location, FieldName fieldName, String keyValue) {
		PersistanceManager awsDynamoDBStorageManager = getAWSDynamoDBStorageManager();

		return awsDynamoDBStorageManager.read(location, fieldName, keyValue);
	}

	@Override
	public void update(StorageLocation location, String keyValue, List<FieldName> fieldNames, String... values) {

		PersistanceManager awsDynamoDBStorageManager = getAWSDynamoDBStorageManager();

		awsDynamoDBStorageManager.update(location, keyValue, fieldNames, values);
	}

	@Override
	public void delete(StorageLocation location, String keyValue) {
		PersistanceManager awsDynamoDBStorageManager = getAWSDynamoDBStorageManager();

		awsDynamoDBStorageManager.delete(location, keyValue);
	}

	private PersistanceManager getAWSDynamoDBStorageManager() {
		return StorageManagerFactory.createAWSDynamoDBStorageManager();
	}

	@Override
	public boolean isPresent(StorageLocation location, FieldName fieldName, String keyValue) {

		PersistanceManager awsDynamoDBStorageManager = getAWSDynamoDBStorageManager();

		return awsDynamoDBStorageManager.isPresent(location, fieldName, keyValue);
	}

	@Override
	public void scanAndDeletePop3Messages(String clientId) {
		PersistanceManager awsDynamoDBStorageManager = getAWSDynamoDBStorageManager();

		awsDynamoDBStorageManager.scanAndDeletePop3Messages(clientId);

	}

	@Override
	public List<String> scanForMessageDimensions(String clientId) {

		PersistanceManager awsDynamoDBStorageManager = getAWSDynamoDBStorageManager();

		return awsDynamoDBStorageManager.scanForMessageDimensions(clientId);
	}

	@Override
	public List<String> getMessageUIDs(String clientId) {
		PersistanceManager awsDynamoDBStorageManager = getAWSDynamoDBStorageManager();

		return awsDynamoDBStorageManager.getMessageUIDs(clientId);
	}

	@Override
	public void addToSet(StorageLocation location, String keyValue, FieldName fieldName, String... values) {
		PersistanceManager awsDynamoDBStorageManager = getAWSDynamoDBStorageManager();
		awsDynamoDBStorageManager.addToSet(location, keyValue, fieldName, values);
	}

	@Override
	public List<String> getSet(StorageLocation location, FieldName fieldName, String keyValue) {
		PersistanceManager awsDynamoDBStorageManager = getAWSDynamoDBStorageManager();
		return awsDynamoDBStorageManager.getSet(location, fieldName, keyValue);
	}

}
