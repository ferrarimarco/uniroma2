package controller.persistance;

public enum StorageLocation {
	POP3_STATUS, POP3_CLIENT_ID, POP3_PREVIOUS_COMMAND, POP3_PREVIOUS_COMMAND_RESULT, POP3_PREVIOUS_COMMAND_FIRST_ARGUMENT, POP3_PREVIOUS_COMMAND_SECOND_ARGUMENT, 
	POP3_USER_NAME, POP3_USER_PASSWORD;
	
	@Override
	public String toString(){
		return super.toString().toLowerCase();
	}
}
