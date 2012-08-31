package controller.persistance;

public enum StorageLocation {
	POP3_SESSIONS, POP3_USERS, POP3_PASSWORDS, POP3_MAILDROPS;
	
	@Override
	public String toString(){
		if(this.equals(POP3_SESSIONS)){
			return "pop3sessions_claudiani_ferrari";
		}else if(this.equals(POP3_USERS) || this.equals(POP3_PASSWORDS)){
			return "pop3users_claudiani_ferrari";
		}else if(this.equals(POP3_MAILDROPS)){
			return "pop3maildrops_claudiani_ferrari";
		}else{
			// TODO: check this return statement
			// This should never occur
			return "";			
		}
	}
}
