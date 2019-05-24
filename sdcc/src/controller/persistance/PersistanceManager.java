package controller.persistance;

import java.util.List;

public interface PersistanceManager {

	void create(StorageLocation location, List<FieldName> fieldNames, String... values);

	String read(StorageLocation location, FieldName fieldName, String keyValue);

	List<String> getSet(StorageLocation location, FieldName fieldName, String keyValue);

	void update(StorageLocation location, String keyValue, List<FieldName> fieldNames, String... values);

	void addToSet(StorageLocation location, String keyValue, FieldName fieldName, String... values);

	void delete(StorageLocation location, String keyValue);
	
	void delete(StorageLocation location, String keyValue, String rangeKey);

	boolean isPresent(StorageLocation location, FieldName fieldName, String keyValue);

	List<String> scanForMessageDimensions(StorageLocation location, String clientId, String userName, boolean isToDelete);

	List<String> getMessageUIDs(StorageLocation location, String clientId, String userName, boolean isToDelete);
}
