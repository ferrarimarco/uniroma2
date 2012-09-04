package controller.persistance;

import java.util.List;

public interface PersistanceManager {

	void create(StorageLocation location, List<FieldName> fieldNames, String ...values);
	
	String read(StorageLocation location, FieldName fieldName, String keyValue);
	
	void update(StorageLocation location, FieldName fieldName, String keyValue, String newValue);
	
	void delete(StorageLocation location, String keyValue);
	
	boolean isPresent(StorageLocation location, FieldName fieldName, String keyValue);
	
	void scanAndDeletePop3Messages(String clientId);
	
	List<String> scanForMessageDimensions(String ClientId);
	
	List<String> getMessageUIDs(String ClientId);
}
