package controller.smtp;

public enum SMTPCode {
	GREETINGS, OK, ERR;

	@Override
	public String toString(){
		
		String value = "";
		
		if(this.equals(GREETINGS)){
			value = "220";
		}else if(this.equals(OK)){
			value = "250";
		}else if(this.equals(ERR)){
			value = "SET_ERROR_VALUE";
		}
		
		return value;
	}
}