package controller.pop3;

public enum POP3SessionStatus {
	GREETINGS, AUTHORIZATION, TRANSACTION, UPDATE, UNKNOWN;
	
	@Override
	public String toString(){
		return super.toString().toLowerCase();
	}
}
