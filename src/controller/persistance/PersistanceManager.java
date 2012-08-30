package controller.persistance;

public interface PersistanceManager {

	void create(StorageLocation location, String id, String value);
	
	String read(StorageLocation location, String id);
	
	void update(StorageLocation location, String id, String value);
	
	void delete(StorageLocation location, String id);
	
	boolean isPresent(StorageLocation location, String id);
}
