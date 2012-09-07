package controller.smtp;

import java.io.BufferedOutputStream;

import controller.AbstractCommandHandler;
import controller.CommunicationHandler;
import controller.persistance.FieldName;
import controller.persistance.PersistanceManager;
import controller.persistance.StorageLocation;

public class SMTPCommandHandler extends AbstractCommandHandler {
	
	@Override
	public void handleCommand(CommunicationHandler communicationHandler, BufferedOutputStream writer, String command, String argument, String secondArgument, PersistanceManager persistanceManager, String clientId){

		// TODO: change to IF with SMTPCommand enum
		switch(command.toUpperCase()){
		case "EHLO":
			break;
		case "HELO":
			break;
		case "MAIL":
			break;
		case "RCPT":
			break;
		case "DATA":
			break;
		case "QUIT":
			break;
		default:
			unsupportedCommand(communicationHandler, writer);
			break;
		}
	}
	
	private void unsupportedCommand(CommunicationHandler communicationHandler, BufferedOutputStream writer){
		
		// TODO: save as last command unsupported
		
		communicationHandler.sendResponse(writer, SMTPCode.UNSUPPORTED_COMMAND.toString(), "Command is not supported");
	}
	
	@Override
	public void sendGreetings(CommunicationHandler communicationHandler, BufferedOutputStream writer, PersistanceManager persistanceManager, String clientId){
		
		// Store client ID in DB
		if(!persistanceManager.isPresent(StorageLocation.SMTP_SESSIONS, FieldName.SMTP_SESSION_ID, clientId)){
			/*
			// Set the status to AUTH directly to avoid another query
			persistanceManager.create(StorageLocation.SMTP_SESSIONS, FieldName.getSMTPStatusTableFieldNames(), 
					clientId,
					POP3SessionStatus.AUTHORIZATION.toString(),
					POP3Command.EMPTY.toString(),
					POP3StatusIndicator.UNKNOWN.toString(),
					POP3Command.EMPTY.toString(),
					POP3Command.EMPTY.toString(),
					POP3Command.EMPTY.toString());
*/
			communicationHandler.sendResponse(writer, SMTPCode.GREETINGS.toString(), "SMTP server ready to roll!");
		}
	}
	
}
