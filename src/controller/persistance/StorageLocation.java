package controller.persistance;

public enum StorageLocation {
	POP3_SESSIONS, POP3_USERS, POP3_PASSWORDS, POP3_MAILDROPS,
	
	SMTP_SESSIONS, SMTP_TEMP_MESSAGE_STORE;
	
	@Override
	public String toString(){
		if(this.equals(POP3_SESSIONS)){
			return "pop3sessions_claudiani_ferrari";
		}else if(this.equals(POP3_USERS) || this.equals(POP3_PASSWORDS)){
			return "pop3users_claudiani_ferrari";
		}else if(this.equals(POP3_MAILDROPS)){
			return "pop3maildrops_claudiani_ferrari";
		}else if(this.equals(SMTP_SESSIONS)){
			return "smtpSessions_claudiani_ferrari";
		}else if(this.equals(SMTP_TEMP_MESSAGE_STORE)){
			return "smtp_temp_message_store_claudiani_ferrari";
		}else{
			// TODO: check this return statement
			// This should never occur
			return "";			
		}
	}
}
