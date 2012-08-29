package controller.persistance;

public enum StorageLocation {
	POP3_STATUS, CLIENT_ID, POP3_PREVIOUS_COMMAND, POP3_PREVIOUS_COMMAND_RESULT;
	
	@Override
	public String toString(){
		return super.toString().toLowerCase();
	}
}
