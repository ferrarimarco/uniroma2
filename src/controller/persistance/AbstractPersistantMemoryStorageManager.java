package controller.persistance;

import java.util.List;


public abstract class AbstractPersistantMemoryStorageManager implements PersistanceManager {

	@Override
	public abstract void create(StorageLocation location, List<FieldName> fieldNames, String... values);

	@Override
	public abstract String read(StorageLocation location, FieldName fieldName, String keyValue);

	@Override
	public abstract List<String> getSet(StorageLocation location, FieldName fieldName, String keyValue);

	@Override
	public abstract void update(StorageLocation location, String keyValue, List<FieldName> fieldNames, String... values);

	@Override
	public abstract void addToSet(StorageLocation location, String keyValue, FieldName fieldName, String... values);

	@Override
	public abstract void delete(StorageLocation location, String keyValue);

	@Override
	public abstract boolean isPresent(StorageLocation location, FieldName fieldName, String keyValue);

	@Override
	public abstract List<String> scanForMessageDimensions(String clientId, String userName);

	@Override
	public abstract List<String> getMessageUIDs(StorageLocation location, String clientId, String userName, boolean isToDelete);

}
