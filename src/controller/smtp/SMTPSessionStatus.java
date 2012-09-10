package controller.smtp;

public enum SMTPSessionStatus {
	GREETINGS, TRANSACTION, 
	
	UNKNOWN, EMPTY;
	
	@Override
	public String toString(){
		return super.toString().toLowerCase();
	}
	
	public static SMTPSessionStatus parseStatus(String value){
		
		value = value.toLowerCase();
		
		if(value.equals(GREETINGS.toString())){
			return GREETINGS;
		}else if(value.equals(TRANSACTION.toString())){
			return TRANSACTION;
		}else{
			return UNKNOWN;
		}
	}
}
