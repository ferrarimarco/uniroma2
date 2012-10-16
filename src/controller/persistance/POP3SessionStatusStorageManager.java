package controller.persistance;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import controller.AbstractRequestHandler;

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
		}else if(fieldName.equals(FieldName.USER_MESSAGES_NUMBER)) {
			return getMemoryStorageLocation(location).get(keyValue).getMessagesNumber();
		}else if(fieldName.equals(FieldName.MESSAGES_TOTAL_DIMENSION)) {
			return getMemoryStorageLocation(location).get(keyValue).getMessagesTotalSize();
		}else if(fieldName.equals(FieldName.POP3_DELETED_MESSAGES_NUMBER)) {
			return getMemoryStorageLocation(location).get(keyValue).getDeletedMessagesNumber();
		}else if(fieldName.equals(FieldName.POP3_DELETED_MESSAGES_SIZE)) {
			return getMemoryStorageLocation(location).get(keyValue).getDeletedMessagesTotalSize();
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
			
		}else if(fieldNames.get(0).equals(FieldName.POP3_SESSION_UIDS)) {
			
			AbstractRequestHandler.log.info("Adding "+ values.length +" UIDS " + " to " + location.toString());
			
			for(int i = 0; i < values.length; i++) {
				getMemoryStorageLocation(location).get(keyValue).addUID(values[i]);
			}
			
		}else if(fieldNames.get(0).equals(FieldName.POP3_MESSAGE_TO_DELETE)) {
			
			AbstractRequestHandler.log.info("Marking message "+ values[0] +" for deletion");
			
			getMemoryStorageLocation(location).get(keyValue).setUIDDeletion(values[0], values[1]);
			
		}else if(fieldNames.get(0).equals(FieldName.POP3_DIMENSIONS_LIST)) {
			
			AbstractRequestHandler.log.info("Adding dimensions ");
			
			getMemoryStorageLocation(location).get(keyValue);
			
			for(int i = 0; i < values.length; i++) {
				getMemoryStorageLocation(location).get(keyValue).addDimension(values[i]);
			}			
		}else if(fieldNames.get(0).equals(FieldName.USER_MESSAGES_NUMBER)) {
			
			AbstractRequestHandler.log.info("Updating messages number and size");
			
			getMemoryStorageLocation(location).get(keyValue).setMessagesNumber(values[0]);
			getMemoryStorageLocation(location).get(keyValue).setMessagesTotalSize(values[1]);
		}else if(fieldNames.get(0).equals(FieldName.POP3_DELETED_MESSAGES_NUMBER)) {
			
			AbstractRequestHandler.log.info("Updating deleted number and size");
			
			getMemoryStorageLocation(location).get(keyValue).setDeletedMessagesNumber(values[0]);
			getMemoryStorageLocation(location).get(keyValue).setDeletedMessagesTotalSize(values[1]);
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
	public List<String> scanForMessageDimensions(StorageLocation location, String clientId, String userName, boolean isToDelete) {
		return getMemoryStorageLocation(location).get(clientId).getDimensionsList(isToDelete);
	}

	@Override
	public List<String> getMessageUIDs(StorageLocation location, String clientId, String userName, boolean isToDelete) {
		AbstractRequestHandler.log.info("Get UIDS from " + location.toString());
		return getMemoryStorageLocation(location).get(clientId).getUIDsList(isToDelete);
	}
	
	@Override
	public void delete(StorageLocation location, String keyValue, String rangeKey) {
		throw new NotImplementedException();
	}
}
