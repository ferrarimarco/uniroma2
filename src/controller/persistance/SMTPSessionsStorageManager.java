package controller.persistance;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;


public class SMTPSessionsStorageManager extends AbstractVolatileMemoryStorageManager {

	private Map<String, SMTPSessionStatusRecord> SMTPSessionStore;
	
	public SMTPSessionsStorageManager() {
		SMTPSessionStore = new HashMap<String, SMTPSessionStatusRecord>();
	}
	
	private Map<String, SMTPSessionStatusRecord> getMemoryStorageLocation(StorageLocation location){
		return SMTPSessionStore;
	}
	
	@Override
	public void create(StorageLocation location, List<FieldName> fieldNames, String... values) {

		String clientId = values[0];
		String initStatus = values[1];
		
		SMTPSessionStatusRecord statusRecord = new SMTPSessionStatusRecord(initStatus);
		
		getMemoryStorageLocation(location).put(clientId, statusRecord);
	}

	@Override
	public String read(StorageLocation location, FieldName fieldName, String keyValue) {
		
		if(fieldName.equals(FieldName.SMTP_SESSION_STATUS)) {
			return getMemoryStorageLocation(location).get(keyValue).getSessionStatus();
		}else {
			return "";
		}
	}

	@Override
	public List<String> getSet(StorageLocation location, FieldName fieldName, String keyValue) {
		throw new NotImplementedException();
	}

	@Override
	public void update(StorageLocation location, String keyValue, List<FieldName> fieldNames, String... values) {
		if(fieldNames.get(0).equals(FieldName.SMTP_SESSION_STATUS)) {
			getMemoryStorageLocation(location).get(keyValue).setSessionStatus(values[0]);
		}
	}

	@Override
	public void addToSet(StorageLocation location, String keyValue, FieldName fieldName, String... values) {
		throw new NotImplementedException();
	}

	@Override
	public void delete(StorageLocation location, String keyValue) {

		getMemoryStorageLocation(location).remove(keyValue);

	}

	@Override
	public boolean isPresent(StorageLocation location, FieldName fieldName, String keyValue) {
		return getMemoryStorageLocation(location).containsKey(keyValue);
	}

	@Override
	public List<String> scanForMessageDimensions(StorageLocation location, String clientId, String userName, boolean isToDelete) {
		throw new NotImplementedException();
	}

	@Override
	public List<String> getMessageUIDs(StorageLocation location, String clientId, String userName, boolean isToDelete) {
		throw new NotImplementedException();
	}

}
