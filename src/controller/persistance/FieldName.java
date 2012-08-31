package controller.persistance;

import java.util.ArrayList;
import java.util.List;

public enum FieldName {
	POP3_USER_NAME, POP3_FULL_NAME, POP3_USER_PASSWORD, POP3_SESSION_ID, POP3_SESSION_STATUS, POP3_LAST_COMMAND, POP3_LAST_COMMAND_RESULT, POP3_LAST_COMMAND_FIRST_ARGUMENT, POP3_LAST_COMMAND_SECOND_ARGUMENT;
	
	@Override
	public String toString(){
		return super.toString().toLowerCase();

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
