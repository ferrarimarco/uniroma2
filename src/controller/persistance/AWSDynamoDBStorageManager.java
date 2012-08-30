package controller.persistance;

public class AWSDynamoDBStorageManager implements PersistanceManager {

	public AWSDynamoDBStorageManager(){
		// TODO Auto-generated method stub
	}
	
	@Override
	public void create(StorageLocation location, String id, String value) {
		// TODO Auto-generated method stub

	}

	@Override
	public String read(StorageLocation location, String id) {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public void update(StorageLocation location, String id, String value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void delete(StorageLocation location, String id) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isPresent(StorageLocation location, String id) {
		// TODO Auto-generated method stub
		return false;
	}

}
