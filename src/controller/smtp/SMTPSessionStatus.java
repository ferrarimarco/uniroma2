package controller.smtp;

public enum SMTPSessionStatus {
	GREETINGS, TRANSACTION, UNKNOWN;
	
	@Override
	public String toString(){
		return super.toString().toLowerCase();
	}
}
