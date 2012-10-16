package controller.persistance;

import java.util.ArrayList;
import java.util.List;

public enum FieldName {

	// Users table (USER_MESSAGES_NUMBER and MESSAGES_TOTAL_DIMENSION are used also for each POP3 Session)
	POP3_USER_NAME, POP3_FULL_NAME, POP3_USER_PASSWORD, USER_MESSAGES_NUMBER, MESSAGES_TOTAL_DIMENSION,

	// POP3 Session table
	POP3_SESSION_ID, POP3_SESSION_STATUS, POP3_LAST_COMMAND, POP3_LAST_COMMAND_RESULT, POP3_SESSION_USER_NAME, POP3_LAST_COMMAND_FIRST_ARGUMENT, POP3_LAST_COMMAND_SECOND_ARGUMENT, POP3_HOW_MANY_DELES, POP3_SESSION_UIDS, POP3_DIMENSIONS_LIST, POP3_DELETED_MESSAGES_SIZE, POP3_DELETED_MESSAGES_NUMBER,

	// Maildrops table
	POP3_MESSAGE_UID, POP3_MESSAGE_TO, POP3_MESSAGE_TO_DELETE, POP3_MESSAGE_DIMENSION, POP3_MESSAGE_HEADER, POP3_MESSAGE_DATA,

	// SMTP session table
	SMTP_SESSION_ID, SMTP_SESSION_STATUS,

	// SMTP temp message store
	SMTP_TEMP_ID, SMTP_TEMP_FROM, SMTP_TEMP_RAW_DATA, SMTP_TEMP_TO_ADDRESSES, SMTP_TEMP_TO_USERS, SMTP_TEMP_HEADER, SMTP_TEMP_BODY, SMTP_TEMP_MESSAGE_SIZE,

	// Tables maintenance table
	UPDATE_TIME;
	
	@Override
	public String toString() {
		return super.toString().toLowerCase();
	}

	public static List<FieldName> getMaintenanceFields(){
		
		List<FieldName> maintenanceData = new ArrayList<FieldName>();

		maintenanceData.add(UPDATE_TIME);

		return maintenanceData;
	}
	
	public static List<FieldName> getDeletedMessagesStats(){
		List<FieldName> maildropData = new ArrayList<FieldName>();

		maildropData.add(POP3_DELETED_MESSAGES_NUMBER);
		maildropData.add(POP3_DELETED_MESSAGES_SIZE);

		return maildropData;
	}
	
	public static List<FieldName> getMaildropStatData(){
		List<FieldName> maildropData = new ArrayList<FieldName>();

		maildropData.add(USER_MESSAGES_NUMBER);
		maildropData.add(MESSAGES_TOTAL_DIMENSION);

		return maildropData;
	}
	
	public static List<FieldName> getPOP3StatusTableFieldNames() {

		List<FieldName> pop3StatusFieldNames = new ArrayList<FieldName>();

		pop3StatusFieldNames.add(POP3_SESSION_ID);
		pop3StatusFieldNames.add(POP3_SESSION_STATUS);
		pop3StatusFieldNames.add(POP3_LAST_COMMAND);
		pop3StatusFieldNames.add(POP3_LAST_COMMAND_RESULT);
		pop3StatusFieldNames.add(POP3_LAST_COMMAND_FIRST_ARGUMENT);
		pop3StatusFieldNames.add(POP3_LAST_COMMAND_SECOND_ARGUMENT);
		pop3StatusFieldNames.add(POP3_SESSION_USER_NAME);
		pop3StatusFieldNames.add(POP3_HOW_MANY_DELES);

		return pop3StatusFieldNames;
	}

	public static List<FieldName> getPOP3UsernameTableFieldNames() {

		List<FieldName> pop3StatusFieldNames = new ArrayList<FieldName>();

		pop3StatusFieldNames.add(POP3_SESSION_USER_NAME);

		return pop3StatusFieldNames;
	}

	public static List<FieldName> getPOP3StatusTableFieldOnly() {

		List<FieldName> pop3StatusFieldNames = new ArrayList<FieldName>();

		pop3StatusFieldNames.add(POP3_SESSION_STATUS);

		return pop3StatusFieldNames;
	}

	public static List<FieldName> getPOP3StatusCommandTableFieldNames() {

		List<FieldName> pop3StatusFieldNames = new ArrayList<FieldName>();

		pop3StatusFieldNames.add(POP3_LAST_COMMAND);
		pop3StatusFieldNames.add(POP3_LAST_COMMAND_RESULT);
		pop3StatusFieldNames.add(POP3_LAST_COMMAND_FIRST_ARGUMENT);
		pop3StatusFieldNames.add(POP3_LAST_COMMAND_SECOND_ARGUMENT);

		return pop3StatusFieldNames;
	}
	
	public static List<FieldName> getPOP3UIDS() {

		List<FieldName> pop3StatusFieldNames = new ArrayList<FieldName>();

		pop3StatusFieldNames.add(POP3_SESSION_UIDS);

		return pop3StatusFieldNames;
	}
	
	public static List<FieldName> getPOP3Dimensions() {

		List<FieldName> pop3StatusFieldNames = new ArrayList<FieldName>();

		pop3StatusFieldNames.add(POP3_DIMENSIONS_LIST);

		return pop3StatusFieldNames;
	}

	public static List<FieldName> getPOP3MessagesTableFieldNames() {

		List<FieldName> pop3StatusFieldNames = new ArrayList<FieldName>();

		pop3StatusFieldNames.add(POP3_MESSAGE_UID);
		pop3StatusFieldNames.add(POP3_MESSAGE_HEADER);
		pop3StatusFieldNames.add(POP3_MESSAGE_DATA);

		return pop3StatusFieldNames;
	}
	
	public static List<FieldName> getUidsTableFields() {

		List<FieldName> uidsTableFields = new ArrayList<FieldName>();

		uidsTableFields.add(POP3_MESSAGE_TO);
		uidsTableFields.add(POP3_MESSAGE_UID);
		uidsTableFields.add(POP3_MESSAGE_DIMENSION);

		return uidsTableFields;
	}

	public static List<FieldName> getMessageToDeleteTableFieldOnly() {

		List<FieldName> pop3StatusFieldNames = new ArrayList<FieldName>();

		pop3StatusFieldNames.add(POP3_MESSAGE_TO_DELETE);

		return pop3StatusFieldNames;
	}
	
	public static List<FieldName> getPOP3DelesFieldOnly() {

		List<FieldName> pop3StatusFieldNames = new ArrayList<FieldName>();

		pop3StatusFieldNames.add(POP3_HOW_MANY_DELES);

		return pop3StatusFieldNames;
	}
	
	public static List<FieldName> getSMTPStatusTableFieldNames() {

		List<FieldName> smtpStatusFieldNames = new ArrayList<FieldName>();

		smtpStatusFieldNames.add(SMTP_SESSION_ID);
		smtpStatusFieldNames.add(SMTP_SESSION_STATUS);

		return smtpStatusFieldNames;
	}
	
	public static List<FieldName> getSMTPStatusTableFieldOnly() {

		List<FieldName> smtpStatusFieldNames = new ArrayList<FieldName>();

		smtpStatusFieldNames.add(SMTP_SESSION_STATUS);

		return smtpStatusFieldNames;
	}


	public static List<FieldName> getSMTPTempTableFromFieldOnly() {

		List<FieldName> smtpStatusFieldNames = new ArrayList<FieldName>();

		smtpStatusFieldNames.add(SMTP_TEMP_FROM);

		return smtpStatusFieldNames;
	}

	public static List<FieldName> getSMTPTempTableDataFieldOnly() {

		List<FieldName> smtpStatusFieldNames = new ArrayList<FieldName>();

		smtpStatusFieldNames.add(SMTP_TEMP_RAW_DATA);

		return smtpStatusFieldNames;
	}
}
