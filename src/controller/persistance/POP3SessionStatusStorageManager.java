package controller.persistance;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;


public class POP3SessionStatusStorageManager extends AbstractVolatileMemoryStorageManager {

	private Map<String, POP3SessionStatusRecord> POP3SessionStore;
	
	public POP3SessionStatusStorageManager() {
		POP3SessionStore = new HashMap<String, POP3SessionStatusRecord>();
	}
	
	private Map<String, POP3SessionStatusRecord> getMemoryStorageLocation(StorageLocation location){
		return POP3SessionStore;
	}
	
	@Override
	public void create(StorageLocation location, List<FieldName> fieldNames, String... values) {

		String clientId = values[0];
		POP3SessionStatusRecord statusRecord = new POP3SessionStatusRecord(values[1], values[2], values[3], values[4], values[5], values[6], values[7]);
		getMemoryStorageLocation(location).put(clientId, statusRecord);
	}

	@Override
	public String read(StorageLocation location, FieldName fieldName, String keyValue) {

		if(fieldName.equals(FieldName.POP3_SESSION_USER_NAME)){
			return getMemoryStorageLocation(location).get(keyValue).getSessionUserName();
		}else if(fieldName.equals(FieldName.POP3_LAST_COMMAND)) {
			return getMemoryStorageLocation(location).get(keyValue).getLastCommand();
		}else if(fieldName.equals(FieldName.POP3_LAST_COMMAND_FIRST_ARGUMENT)) {
			return getMemoryStorageLocation(location).get(keyValue).getLastCommandFirstArg();
		}else if(fieldName.equals(FieldName.POP3_LAST_COMMAND_RESULT)) {
			return getMemoryStorageLocation(location).get(keyValue).getLastCommandResult();
		}else if(fieldName.equals(FieldName.POP3_SESSION_STATUS)) {
			return getMemoryStorageLocation(location).get(keyValue).getSessionStatus();
		}else if(fieldName.equals(FieldName.POP3_HOW_MANY_DELES)) {
			return getMemoryStorageLocation(location).get(keyValue).getHowManyDeles();
		}else {
			return "";
		}
	}

	@Override
	public List<String> getSet(StorageLocation location, FieldName fieldName, String keyValue) {
		// TODO Auto-generated method stub
		throw new NotImplementedException();
	}

	@Override
	public void update(StorageLocation location, String keyValue, List<FieldName> fieldNames, String... values) {

		if(fieldNames.get(0).equals(FieldName.POP3_SESSION_USER_NAME)){
			getMemoryStorageLocation(location).get(keyValue).setSessionUserName(values[0]);
		}else if(fieldNames.get(0).equals(FieldName.POP3_HOW_MANY_DELES)) {
			getMemoryStorageLocation(location).get(keyValue).setHowManyDeles(values[0]);
		}else if(fieldNames.get(0).equals(FieldName.POP3_LAST_COMMAND)) {
			getMemoryStorageLocation(location).get(keyValue).setLastCommand(values[0]);
			getMemoryStorageLocation(location).get(keyValue).setLastCommandResult(values[1]);
			getMemoryStorageLocation(location).get(keyValue).setLastCommandFirstArg(values[2]);
			getMemoryStorageLocation(location).get(keyValue).setLastCommandSecondArg(values[3]);
		}else if(fieldNames.get(0).equals(FieldName.POP3_SESSION_STATUS)) {
			getMemoryStorageLocation(location).get(keyValue).setSessionStatus(values[0]);
		}
	}

	@Override
	public void addToSet(StorageLocation location, String keyValue, FieldName fieldName, String... values) {
		// TODO Auto-generated method stub
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
	public void scanAndDeletePop3Messages(String clientId, String userName) {
		// TODO Auto-generated method stub
		throw new NotImplementedException();
	}

	@Override
	public List<String> scanForMessageDimensions(String ClientId, String userName) {
		// TODO Auto-generated method stub
		throw new NotImplementedException();
	}

	@Override
	public List<String> getMessageUIDs(String ClientId, String userName) {
		// TODO Auto-generated method stub
		throw new NotImplementedException();
	}

}
