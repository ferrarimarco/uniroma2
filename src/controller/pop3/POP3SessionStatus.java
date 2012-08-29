package controller.pop3;

public enum POP3SessionStatus {
	GREETINGS, AUTHORIZATION, TRANSACTION, UPDATE, UNKNOWN;
	
	public static POP3SessionStatus parseStatus(String value){
		
		value = value.toLowerCase();
		
		if(value.equals(GREETINGS)){
			return GREETINGS;
		}else if(value.equals(AUTHORIZATION)){
			return AUTHORIZATION;
		}else if(value.equals(TRANSACTION)){
			return TRANSACTION;
		}else if(value.equals(UPDATE)){
			return UPDATE;
		}else{
			return UNKNOWN;
		}
	}
	
	@Override
	public String toString(){
		return super.toString().toLowerCase();
	}
}
