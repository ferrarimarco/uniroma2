package controller.persistance;

import java.util.ArrayList;
import java.util.List;

public enum FieldName {
	POP3_USER_NAME, POP3_FULL_NAME, POP3_USER_PASSWORD,
	POP3_SESSION_ID, POP3_SESSION_STATUS, POP3_LAST_COMMAND, POP3_LAST_COMMAND_RESULT, POP3_SESSION_USER_NAME, 
	POP3_LAST_COMMAND_FIRST_ARGUMENT, POP3_LAST_COMMAND_SECOND_ARGUMENT,
	POP3_MESSAGE_UID, POP3_MESSAGE_TO, POP3_MESSAGE_TO_DELETE, POP3_MESSAGE_DIMENSION, POP3_MESSAGE_HEADER, POP3_MESSAGE_DATA;
	
	@Override
	public String toString(){
		return super.toString().toLowerCase();

		// TODO: change the tostring to match field names in dynamo
	}
	
	public static List<FieldName> getPOP3StatusTableFieldNames(){
		
		List<FieldName> pop3StatusFieldNames = new ArrayList<FieldName>();
		
		pop3StatusFieldNames.add(POP3_SESSION_ID);
		pop3StatusFieldNames.add(POP3_SESSION_STATUS);
		pop3StatusFieldNames.add(POP3_LAST_COMMAND);
		pop3StatusFieldNames.add(POP3_LAST_COMMAND_RESULT);
		pop3StatusFieldNames.add(POP3_LAST_COMMAND_FIRST_ARGUMENT);
		pop3StatusFieldNames.add(POP3_LAST_COMMAND_SECOND_ARGUMENT);
		
		return pop3StatusFieldNames;
	}
}
