package controller.persistance;

import java.util.List;

public class StorageManager implements PersistanceManager {

	private PersistanceManager awsDynamoDBStorageManager;
	private PersistanceManager smtpTempMessageStorageManager;
	
	private PersistanceManager pop3SessionStatusStorageManager;
	
	private PersistanceManager smtpSessionStorageManager;
	
	public StorageManager(){
		awsDynamoDBStorageManager = new AWSDynamoDBStorageManager();
		smtpTempMessageStorageManager = new SMTPTempMessageStorageManager();
		
		pop3SessionStatusStorageManager = new POP3SessionStatusStorageManager();
		
		smtpSessionStorageManager = new SMTPSessionsStorageManager();
	}
	
	private PersistanceManager getPersistanceManager(StorageLocation location) {
		if(location.equals(StorageLocation.SMTP_TEMP_MESSAGE_STORE)){
			return smtpTempMessageStorageManager;
		}else if(location.equals(StorageLocation.POP3_SESSIONS)) {
			return pop3SessionStatusStorageManager;
		}else if(location.equals(StorageLocation.SMTP_SESSIONS)) {
			return smtpSessionStorageManager;
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
	public List<String> scanForMessageDimensions(StorageLocation location, String clientId, String userName, boolean isToDelete) {
		PersistanceManager persistanceManager = getPersistanceManager(location);
		return persistanceManager.scanForMessageDimensions(location, clientId, userName, isToDelete);
	}

	@Override
	public List<String> getMessageUIDs(StorageLocation location, String clientId, String userName, boolean isToDelete) {
		PersistanceManager persistanceManager = getPersistanceManager(location);
		return persistanceManager.getMessageUIDs(location, clientId, userName, isToDelete);
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
