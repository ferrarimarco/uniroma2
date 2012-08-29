package controller.persistance;

public enum StorageLocation {
	POP3_STATUS, CLIENT_ID;
	
	@Override
	public String toString(){
		return super.toString().toLowerCase();
	}
}
