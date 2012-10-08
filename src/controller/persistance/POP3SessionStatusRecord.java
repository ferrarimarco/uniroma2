package controller.persistance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import controller.AbstractRequestHandler;
import controller.pop3.POP3MessageDeletion;

public class POP3SessionStatusRecord {

	private String sessionStatus;
	private String lastCommand;
	private String lastCommandResult;
	private String sessionUserName;
	private String lastCommandFirstArg;
	private String lastCommandSecondArg;
	private String howManyDeles;
	private Map<String, String> uids;
		
	public POP3SessionStatusRecord(String sessionStatus, String lastCommand, String lastCommandResult, String sessionUserName, String lastCommandFirstArg, String lastCommandSecondArg, String howManyDeles) {

		this.sessionStatus = sessionStatus;
		this.lastCommand = lastCommand;
		this.lastCommandResult = lastCommandResult;
		this.sessionUserName = sessionUserName;
		this.lastCommandFirstArg = lastCommandFirstArg;
		this.lastCommandSecondArg = lastCommandSecondArg;
		this.howManyDeles = howManyDeles;
		uids = new HashMap<String, String>();
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
	
	public void addUID(String uid) {
		uids.put(uid, POP3MessageDeletion.NO.toString());
	}
	
	public void setUIDDeletion(String uid, String deletion) {
		uids.put(uid, POP3MessageDeletion.parseValue(deletion).toString());
	}

	public List<String> getUIDsList(boolean isToDelete) {
		
		List<String> uidsList = new ArrayList<String>(uids.keySet());
		
		AbstractRequestHandler.log.info("uids size " + uids.size());
		AbstractRequestHandler.log.info("uidsList size " + uidsList.size());
		
		List<String> uidsNoDeletion = new ArrayList<String>(uids.size());
		
		for(int i = 0; i < uids.size(); i++) {
			if(!isToDelete) {
				
				AbstractRequestHandler.log.info("Get UIDs of message NOT to delete");
				
				if(uids.get(uidsList.get(i)).equals(POP3MessageDeletion.NO.toString())) {
					uidsNoDeletion.add(uidsList.get(i));
					AbstractRequestHandler.log.info("Add " + uidsList.get(i));
				}				
			}else {
				AbstractRequestHandler.log.info("Get UIDs of message TO delete");
				
				if(uids.get(uidsList.get(i)).equals(POP3MessageDeletion.YES.toString())) {
					uidsNoDeletion.add(uidsList.get(i));
					AbstractRequestHandler.log.info("Add " + uidsList.get(i));
				}
			}
		}

		return uidsNoDeletion;
	}
	
	
}
