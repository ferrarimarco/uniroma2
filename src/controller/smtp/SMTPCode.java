package controller.smtp;

public enum SMTPCode {
	GREETINGS, OK;

	@Override
	public String toString(){
		
		String value = "";
		
		if(this.equals(GREETINGS)){
			value = "220";
		}else if(this.equals(OK)){
			value = "250";
		}
		
		return value;
	}
}