package controller.persistance;


public class SMTPSessionStatusRecord {

	private String sessionStatus;

	public SMTPSessionStatusRecord(String sessionStatus) {

		super();
		this.sessionStatus = sessionStatus;
	}

	
	public String getSessionStatus() {
	
		return sessionStatus;
	}

	
	public void setSessionStatus(String sessionStatus) {
	
		this.sessionStatus = sessionStatus;
	}
	
}
