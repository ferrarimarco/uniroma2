package controller.persistance;

public class StorageManager implements PersistanceManager {
	
	@Override
	public void create(StorageLocation location, String id, String value) {
		// TODO: handle creation if the record is already there

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

		// TODO: handle creation if the record does not exists

		PersistanceManager awsDynamoDBStorageManager = getAWSDynamoDBStorageManager();

		awsDynamoDBStorageManager.update(location, id, value);
	}

	@Override
	public void delete(StorageLocation location, String id) {
		PersistanceManager awsDynamoDBStorageManager = getAWSDynamoDBStorageManager();

		awsDynamoDBStorageManager.delete(location, id);
	}

	private AWSDynamoDBStorageManager getAWSDynamoDBStorageManager() {
		return StorageManagerFactory.createAWSDynamoDBStorageManager();
	}

	@Override
	public boolean isPresent(StorageLocation location, String id) {

		String result = read(location, id);

		if (result.isEmpty()) {
			return false;
		} else {
			return true;
		}
	}

}
