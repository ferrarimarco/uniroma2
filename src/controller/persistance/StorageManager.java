package controller.persistance;

import java.util.List;

public class StorageManager implements PersistanceManager {

	private PersistanceManager awsDynamoDBStorageManager;
	private PersistanceManager smtpTempMessageStorageManager;
	
	public StorageManager(){
		awsDynamoDBStorageManager = PersistanceManagerFactory.createAWSDynamoDBStorageManager();
		smtpTempMessageStorageManager = PersistanceManagerFactory.createSMTPTempMessageStorageManager();
	}
	
	private PersistanceManager getPersistanceManager(StorageLocation location) {
		if(location.equals(StorageLocation.SMTP_TEMP_MESSAGE_STORE)){
			return smtpTempMessageStorageManager;
		}else{
			return awsDynamoDBStorageManager;
		}
	}
	
	@Override
	public void create(StorageLocation location, List<FieldName> fieldNames, String... values) {
		PersistanceManager persistanceManager = getPersistanceManager(location);
		persistanceManager.create(location, fieldNames, values);
	}

	@Override
	public String read(StorageLocation location, FieldName fieldName, String keyValue) {
		PersistanceManager persistanceManager = getPersistanceManager(location);
		return persistanceManager.read(location, fieldName, keyValue);
	}

	@Override
	public void update(StorageLocation location, String keyValue, List<FieldName> fieldNames, String... values) {
		PersistanceManager persistanceManager = getPersistanceManager(location);
		persistanceManager.update(location, keyValue, fieldNames, values);
	}

	@Override
	public void delete(StorageLocation location, String keyValue) {
		PersistanceManager persistanceManager = getPersistanceManager(location);
		persistanceManager.delete(location, keyValue);
	}

	@Override
	public boolean isPresent(StorageLocation location, FieldName fieldName, String keyValue) {
		PersistanceManager persistanceManager = getPersistanceManager(location);
		return persistanceManager.isPresent(location, fieldName, keyValue);
	}

	@Override
	public void scanAndDeletePop3Messages(String clientId) {
		PersistanceManager persistanceManager = getPersistanceManager(StorageLocation.POP3_MAILDROPS);
		persistanceManager.scanAndDeletePop3Messages(clientId);

	}

	@Override
	public List<String> scanForMessageDimensions(String clientId) {
		PersistanceManager persistanceManager = getPersistanceManager(StorageLocation.POP3_MAILDROPS);
		return persistanceManager.scanForMessageDimensions(clientId);
	}

	@Override
	public List<String> getMessageUIDs(String clientId) {
		PersistanceManager persistanceManager = getPersistanceManager(StorageLocation.POP3_MAILDROPS);
		return persistanceManager.getMessageUIDs(clientId);
	}

	@Override
	public void addToSet(StorageLocation location, String keyValue, FieldName fieldName, String... values) {
		PersistanceManager persistanceManager = getPersistanceManager(location);
		persistanceManager.addToSet(location, keyValue, fieldName, values);
	}

	@Override
	public List<String> getSet(StorageLocation location, FieldName fieldName, String keyValue) {
		PersistanceManager persistanceManager = getPersistanceManager(location);
		return persistanceManager.getSet(location, fieldName, keyValue);
	}

}
