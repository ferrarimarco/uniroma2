package controller;

import java.io.BufferedOutputStream;

public class POP3CommandHandler {
	
	private POP3CommunicationHandler pop3CommunicationHandler;
	
	public POP3CommandHandler(){
		pop3CommunicationHandler = new POP3CommunicationHandler();
	}
	
	public POP3Status USERCommand(BufferedOutputStream writer, POP3Status status, String argument){
		
		// Check the status of the POP3 session
		if(!status.equals(POP3Status.AUTHORIZATION)){
			pop3CommunicationHandler.sendResponse(writer, POP3StatusIndicator.ERR, "This command is available only in AUTHORIZATION status.");
			return status;
		}
		
		// Check the previous command: USER is available only in AUTH status, after the POP3 server greeting or after an unsuccessful USER or PASS command
		// TODO: get the previous command from DB. Valid ones are: empty, USER (failed), PASS (failed). Other possibilities must send an error and return the current session status
		
		// Check the argument
		if(argument.isEmpty() || argument == null){
			pop3CommunicationHandler.sendResponse(writer, POP3StatusIndicator.ERR, "Missing name");
			return status;
		}
		
		// TODO: search for the username supplied as "argument" in DB
		// TODO: if not found, send an error and return the current status
		// TODO: if found send OK and wait for the PASS command only. Then record the transaction in DB (last command: USER, output: OK, arg: argument)		
		
		// TODO: check this return statement
		return status;
	}
	
	public POP3Status PASSCommand(BufferedOutputStream writer, POP3Status status, String argument){
		
		// Check the status of the POP3 session
		if(!status.equals(POP3Status.AUTHORIZATION)){
			pop3CommunicationHandler.sendResponse(writer, POP3StatusIndicator.ERR, "This command is available only in AUTHORIZATION status.");
			return status;
		}
		
		// Check the previous command: PASS is available only in AUTH status, after a successful USER command
		// TODO: get the previous command from DB. Valid ones are: USER (passed). Other possibilities must send an error and return the current session status
		
		// Check the argument
		if(argument.isEmpty() || argument == null){
			pop3CommunicationHandler.sendResponse(writer, POP3StatusIndicator.ERR, "Missing name");
			return status;
		}
		
		// TODO: try to match the current password with the username sent with the previous USER command (DB)
		// TODO: if they don't match, send an error and return the current status
		// TODO: if they match send OK and change status to TRANSACTION. Then record the transaction in DB (last command: PASS, output: OK, arg: argument)
		
		// TODO: check this return statement
		return status;
	}
	
	public POP3Status QUITCommand(BufferedOutputStream writer, POP3Status status){
		
		if(status.equals(POP3Status.GREETINGS)){
			pop3CommunicationHandler.sendResponse(writer, POP3StatusIndicator.ERR, "POP3 server is in GREETINGS status");
		}else{
			pop3CommunicationHandler.sendResponse(writer, POP3StatusIndicator.OK, "Closing session");

			if(status.equals(POP3Status.TRANSACTION)){
				return POP3Status.UPDATE;
			}
		}
		
		// TODO: unlock mailbox
		
		return status;
	}

	public POP3Status CAPACommand(BufferedOutputStream writer, POP3Status status){

		if(status.equals(POP3Status.GREETINGS)){
			pop3CommunicationHandler.sendLine(writer, POP3StatusIndicator.ERR + " POP3 server is in GREETINGS status", false, false);
		}else{
			pop3CommunicationHandler.sendLine(writer, POP3StatusIndicator.OK + " Capabilities follow.", false, false);
		}
		
		// TODO: Send capabilities
		//sendLine(msg, true, false);
		
		return status;
	}
	
	public POP3Status unsupportedCommand(BufferedOutputStream writer, POP3Status status){
		
		pop3CommunicationHandler.sendResponse(writer, POP3StatusIndicator.ERR, "Command is not supported");
		
		return status;
	}
	
	public POP3Status sendGreetings(BufferedOutputStream writer, POP3Status status){
		
		pop3CommunicationHandler.sendResponse(writer, POP3StatusIndicator.OK, "POP3 server ready to roll!");
		
		return POP3Status.AUTHORIZATION;		
	}
}
