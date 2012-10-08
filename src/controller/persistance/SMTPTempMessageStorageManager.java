package controller.persistance;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.Message;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class SMTPTempMessageStorageManager extends AbstractVolatileMemoryStorageManager {

	private Map<String, Message> smtpTempMessageStore;
	
	public SMTPTempMessageStorageManager(){
		smtpTempMessageStore = new HashMap<String, Message>();
	}
	
	private Map<String, Message> getMemoryStorageLocation(StorageLocation location){
		return smtpTempMessageStore;
	}

	@Override
	public void create(StorageLocation location, List<FieldName> fieldNames, String... values) {
		
		String clientId = values[0];
		String from = values[1];
		
		Message message = new Message();
		message.setFromAddress(from);
		
		getMemoryStorageLocation(location).put(clientId, message);
	}

	@Override
	public String read(StorageLocation location, FieldName fieldName, String keyValue) {
		if(fieldName.equals(FieldName.SMTP_TEMP_BODY)){
			return getMemoryStorageLocation(location).get(keyValue).getBody();
		}else if(fieldName.equals(FieldName.SMTP_TEMP_HEADER)){
			return getMemoryStorageLocation(location).get(keyValue).getHeader();
		}else if(fieldName.equals(FieldName.SMTP_TEMP_ID)){
			return getMemoryStorageLocation(location).get(keyValue).getUID();
		}else if(fieldName.equals(FieldName.SMTP_TEMP_MESSAGE_SIZE)){
			return getMemoryStorageLocation(location).get(keyValue).getMessageSize();
		}else if(fieldName.equals(FieldName.SMTP_TEMP_RAW_DATA)){
			return getMemoryStorageLocation(location).get(keyValue).getRawData();
		}else if(fieldName.equals(FieldName.SMTP_TEMP_FROM)){
			return getMemoryStorageLocation(location).get(keyValue).getFromAddress();
		}else{
			return "";
		}
	}

	@Override
	public List<String> getSet(StorageLocation location, FieldName fieldName, String keyValue) {
		
		if(fieldName.equals(FieldName.SMTP_TEMP_TO_USERS)){
			return getMemoryStorageLocation(location).get(keyValue).getToUsers();
		}else if(fieldName.equals(FieldName.SMTP_TEMP_TO_ADDRESSES)){
			return getMemoryStorageLocation(location).get(keyValue).getToAddresses();
		}else{
			// TODO Because we use this method to retrieve the USERS and TO_ADDRESSES lists only!
			return null;		
		}
	}

	@Override
	public void update(StorageLocation location, String keyValue, List<FieldName> fieldNames, String... values) {
		if(fieldNames.get(0).equals(FieldName.SMTP_TEMP_RAW_DATA)){
			getMemoryStorageLocation(location).get(keyValue).setRawData(values[0]);
		}else if(fieldNames.get(0).equals(FieldName.SMTP_TEMP_FROM)){
			// TODO: delete this, is not used??????
			getMemoryStorageLocation(location).get(keyValue).setFromAddress(values[0]);
		}else{
			// TODO Because we use this method to update the SMTP_TEMP_RAW_DATA and SMTP_TEMP_FROM fields	
		}
	}

	@Override
	public void addToSet(StorageLocation location, String keyValue, FieldName fieldName, String... values) {
		
		if(fieldName.equals(FieldName.SMTP_TEMP_TO_ADDRESSES)){
			getMemoryStorageLocation(location).get(keyValue).addToAddress(values[0]);
		}else{
			// TODO Because we use this method to add to the USERS list only!
		}
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
	public List<String> scanForMessageDimensions(String clientId, String userName) {
		throw new NotImplementedException();
	}

	@Override
	public List<String> getMessageUIDs(StorageLocation location, String clientId, String userName, boolean isToDelete) {
		throw new NotImplementedException();
	}
	
}
