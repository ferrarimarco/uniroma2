package controller.persistance;

public class POP3SessionStatusRecord {

	private String sessionStatus;
	private String lastCommand;
	private String lastCommandResult;
	private String sessionUserName;
	private String lastCommandFirstArg;
	private String lastCommandSecondArg;
	private String howManyDeles;
	
	public POP3SessionStatusRecord(String sessionStatus, String lastCommand, String lastCommandResult, String sessionUserName, String lastCommandFirstArg, String lastCommandSecondArg, String howManyDeles) {

		this.sessionStatus = sessionStatus;
		this.lastCommand = lastCommand;
		this.lastCommandResult = lastCommandResult;
		this.sessionUserName = sessionUserName;
		this.lastCommandFirstArg = lastCommandFirstArg;
		this.lastCommandSecondArg = lastCommandSecondArg;
		this.howManyDeles = howManyDeles;
	}

	public String getSessionStatus() {
	
		return sessionStatus;
	}

	
	public void setSessionStatus(String sessionStatus) {
	
		this.sessionStatus = sessionStatus;
	}

	
	public String getLastCommand() {
	
		return lastCommand;
	}

	
	public void setLastCommand(String lastCommand) {
	
		this.lastCommand = lastCommand;
	}

	
	public String getLastCommandResult() {
	
		return lastCommandResult;
	}

	
	public void setLastCommandResult(String lastCommandResult) {
	
		this.lastCommandResult = lastCommandResult;
	}

	
	public String getSessionUserName() {
	
		return sessionUserName;
	}

	
	public void setSessionUserName(String sessionUserName) {
	
		this.sessionUserName = sessionUserName;
	}

	
	public String getLastCommandFirstArg() {
	
		return lastCommandFirstArg;
	}

	
	public void setLastCommandFirstArg(String lastCommandFirstArg) {
	
		this.lastCommandFirstArg = lastCommandFirstArg;
	}

	
	public String getLastCommandSecondArg() {
	
		return lastCommandSecondArg;
	}

	
	public void setLastCommandSecondArg(String lastCommandSecondArg) {
	
		this.lastCommandSecondArg = lastCommandSecondArg;
	}

	
	public String getHowManyDeles() {
	
		return howManyDeles;
	}

	
	public void setHowManyDeles(String howManyDeles) {
	
		this.howManyDeles = howManyDeles;
	}
	
	
	
}
