package controller;

public enum POP3Status {
	GREETINGS, AUTHORIZATION, TRANSACTION, UPDATE, UNKNOWN;
	
	@Override
	public String toString(){
		return super.toString().toLowerCase();
	}
}
