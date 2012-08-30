package controller.pop3;

import java.io.BufferedOutputStream;
import java.util.ArrayList;
import java.util.List;

import controller.persistance.PersistanceManager;
import controller.persistance.StorageLocation;

public class POP3CommandHandler {
	
	public void USERCommand(POP3CommunicationHandler pop3CommunicationHandler, BufferedOutputStream writer, String argument, PersistanceManager persistanceManager, String clientId){
		
		POP3SessionStatus status = getStatus(persistanceManager, clientId);
		
		// Check the status of the POP3 session: USER is available only in AUTH status
		if(!status.equals(POP3SessionStatus.AUTHORIZATION)){
			setLastCommand(persistanceManager, clientId, POP3Command.USER, POP3StatusIndicator.ERR);
			pop3CommunicationHandler.sendResponse(writer, POP3StatusIndicator.ERR, "This command is available only in AUTHORIZATION status.");
			return;
		}
		
		// Check the previous command: USER is available only after the POP3 server greeting or after an unsuccessful USER or PASS command
		POP3Command previousCommand = getPreviousCommand(persistanceManager, clientId);
		POP3StatusIndicator previousCommandResult = getPreviousCommandResult(persistanceManager, clientId);
		
		if(!previousCommand.equals(POP3Command.EMPTY) || (previousCommand.equals(POP3Command.USER) && previousCommandResult.equals(POP3StatusIndicator.OK)) || (previousCommand.equals(POP3Command.PASS) && previousCommandResult.equals(POP3StatusIndicator.OK))){
			
			setLastCommand(persistanceManager, clientId, POP3Command.USER, POP3StatusIndicator.ERR);
			
			pop3CommunicationHandler.sendResponse(writer, POP3StatusIndicator.ERR, "Invalid command sequence. PASS expected. You have to start over.");
			
			return;
		}

		// Check the argument
		if(argument.isEmpty() || argument == null){
			setLastCommand(persistanceManager, clientId, POP3Command.USER, POP3StatusIndicator.ERR);
			pop3CommunicationHandler.sendResponse(writer, POP3StatusIndicator.ERR, "Missing command argument.");
			return;
		}
		
		if(!persistanceManager.isPresent(StorageLocation.POP3_USER_NAME, argument)){
			setLastCommand(persistanceManager, clientId, POP3Command.USER, POP3StatusIndicator.ERR);
			pop3CommunicationHandler.sendResponse(writer, POP3StatusIndicator.ERR, "There is no such user.");
		}
		
		setLastCommandWithOneArgument(persistanceManager, clientId, POP3Command.USER, POP3StatusIndicator.OK, argument);
		pop3CommunicationHandler.sendResponse(writer, POP3StatusIndicator.OK, "User " + argument + " found.");	
	}
	
	public void PASSCommand(POP3CommunicationHandler pop3CommunicationHandler, BufferedOutputStream writer, String argument, PersistanceManager persistanceManager, String clientId){
		
		POP3SessionStatus status = getStatus(persistanceManager, clientId);
		
		// Check the status of the POP3 session: PASS is available only in AUTH status
		if(!status.equals(POP3SessionStatus.AUTHORIZATION)){
			
			setLastCommand(persistanceManager, clientId, POP3Command.PASS, POP3StatusIndicator.ERR);
			
			pop3CommunicationHandler.sendResponse(writer, POP3StatusIndicator.ERR, "This command is available only in AUTHORIZATION status.");
		}
		
		// Check the previous command: PASS is available only after a successful USER command
		POP3Command previousCommand = getPreviousCommand(persistanceManager, clientId);
		POP3StatusIndicator previousCommandResult = getPreviousCommandResult(persistanceManager, clientId);
		
		if(!previousCommand.equals(POP3Command.USER) && !previousCommandResult.equals(POP3StatusIndicator.OK)){
			
			setLastCommand(persistanceManager, clientId, POP3Command.PASS, POP3StatusIndicator.ERR);
			
			pop3CommunicationHandler.sendResponse(writer, POP3StatusIndicator.ERR, "Invalid sequence. USER not selected. You have to start over.");
		}
		
		// Check the argument
		if(argument.isEmpty() || argument == null){
			setLastCommand(persistanceManager, clientId, POP3Command.PASS, POP3StatusIndicator.ERR);
			pop3CommunicationHandler.sendResponse(writer, POP3StatusIndicator.ERR, "Missing command argument");
		}
		
		// TODO: hash the password
		// Get the password for the current username
		String userName = getPreviousCommandFirstArgument(persistanceManager, clientId);
		String password = persistanceManager.read(StorageLocation.POP3_USER_PASSWORD, userName);
		
		if(!password.equals(argument)){// Wrong password
			setLastCommand(persistanceManager, clientId, POP3Command.PASS, POP3StatusIndicator.ERR);
			pop3CommunicationHandler.sendResponse(writer, POP3StatusIndicator.ERR, "Wrong password for user " + userName);		
		}
		
		setLastCommandWithOneArgument(persistanceManager, clientId, POP3Command.PASS, POP3StatusIndicator.OK, argument);
		
		setStatus(persistanceManager, POP3SessionStatus.TRANSACTION, clientId);
		
		pop3CommunicationHandler.sendResponse(writer, POP3StatusIndicator.OK, "Authentication successful.");
	}
	
	public void QUITCommand(POP3CommunicationHandler pop3CommunicationHandler, BufferedOutputStream writer, PersistanceManager persistanceManager, String clientId){
		
		POP3SessionStatus status = getStatus(persistanceManager, clientId);
		
		if(status.equals(POP3SessionStatus.GREETINGS)){
			setLastCommand(persistanceManager, clientId, POP3Command.QUIT, POP3StatusIndicator.ERR);
			pop3CommunicationHandler.sendResponse(writer, POP3StatusIndicator.ERR, "POP3 server is in GREETINGS status");
		}else{
			
			setLastCommand(persistanceManager, clientId, POP3Command.QUIT, POP3StatusIndicator.OK);
			
			// TODO: check what to do if in AUTH status 
			if(status.equals(POP3SessionStatus.TRANSACTION)){
				setStatus(persistanceManager, POP3SessionStatus.UPDATE, clientId);
			}
			
			pop3CommunicationHandler.sendResponse(writer, POP3StatusIndicator.OK, "Closing session");
		}
		
		// TODO: unlock mailbox
		// TODO: delete marked messages
		// TODO: delete session data from db

	}

	public void CAPACommand(POP3CommunicationHandler pop3CommunicationHandler, BufferedOutputStream writer, PersistanceManager persistanceManager, String clientId){

		POP3SessionStatus status = getStatus(persistanceManager, clientId);
		
		if(status.equals(POP3SessionStatus.GREETINGS)){
			setLastCommand(persistanceManager, clientId, POP3Command.CAPA, POP3StatusIndicator.ERR);
			pop3CommunicationHandler.sendLine(writer, POP3StatusIndicator.ERR + " This command is not available in GREETINGS status", false, false);
		}else{
			
			setLastCommand(persistanceManager, clientId, POP3Command.CAPA, POP3StatusIndicator.OK);
			
			pop3CommunicationHandler.sendLine(writer, POP3StatusIndicator.OK + " Capabilities follow.", false, false);
			
			List<String> capabilities = new ArrayList<String>();
			capabilities.add("CAPA");
			capabilities.add("USER");
			capabilities.add("PASS");
			capabilities.add("QUIT");
			capabilities.add("STAT");
			capabilities.add("LIST");
			capabilities.add("RETR");
			capabilities.add("DELE");
			capabilities.add("TOP");
			capabilities.add("NOOP");
			capabilities.add("RSET");
			
			pop3CommunicationHandler.sendListAsMultiLineResponse(writer, capabilities);
		}
	}
	
	public void STATCommand(POP3CommunicationHandler pop3CommunicationHandler, BufferedOutputStream writer, PersistanceManager persistanceManager, String clientId){
		
		POP3SessionStatus status = getStatus(persistanceManager, clientId);
		
		// Check the status of the POP3 session
		if(!status.equals(POP3SessionStatus.TRANSACTION)){
			setLastCommand(persistanceManager, clientId, POP3Command.STAT, POP3StatusIndicator.ERR);
			pop3CommunicationHandler.sendResponse(writer, POP3StatusIndicator.ERR, "This command is available only in TRANSACTION status.");
		}
		
		int messages = 0;
		int dimension = 0;
		
		// Get the maildrop info about the user from DB
		// TODO: get # of messages and dimension for each message from DB
		
		setLastCommand(persistanceManager, clientId, POP3Command.STAT, POP3StatusIndicator.OK);
		
		pop3CommunicationHandler.sendResponse(writer, POP3StatusIndicator.OK, messages + " " + dimension);
	}
	
	public void LISTCommand(POP3CommunicationHandler pop3CommunicationHandler, BufferedOutputStream writer, String argument, PersistanceManager persistanceManager, String clientId){
		
		POP3SessionStatus status = getStatus(persistanceManager, clientId);
		
		// Check the status of the POP3 session
		if(!status.equals(POP3SessionStatus.TRANSACTION)){
			setLastCommand(persistanceManager, clientId, POP3Command.LIST, POP3StatusIndicator.ERR);
			pop3CommunicationHandler.sendResponse(writer, POP3StatusIndicator.ERR, "This command is available only in TRANSACTION status.");
			return;
		}
		
		// Check the argument
		if(argument.isEmpty() || argument == null){
			
			// Get information about all the messages in the maildrop
			List<String> messages = new ArrayList<String>();
			//TODO: read info from DB
			
			setLastCommand(persistanceManager, clientId, POP3Command.LIST, POP3StatusIndicator.OK);
			
			pop3CommunicationHandler.sendResponse(writer, POP3StatusIndicator.OK, messages.size() + " messages");
			
			// Send response
			if(messages.size() > 0){
				for(int i = 0; i < messages.size(); i++){
					if(i < messages.size() - 1){
						int dimension = 0;
						// TODO: compute the dimension of the i-nth message
						
						pop3CommunicationHandler.sendLine(writer, i + " " + dimension, true, false);
					}else{// Last line of the response
						int dimension = 0;
						// TODO: compute the dimension of the i-nth message
						
						pop3CommunicationHandler.sendLine(writer, i + " " + dimension, true, true);
					}
				}
			}else{// No messages in the maildrop
				pop3CommunicationHandler.sendResponse(writer, POP3StatusIndicator.OK, "");
				pop3CommunicationHandler.sendLine(writer, "", true, true);
			}

			
		}else{
			
			// Check if such message exists
			// TODO: check for such message in DB
			
			
			// Get the dimension of the message
			int dimension = 0;
			// TODO: compute the exact dimension of the message
			
			// Send the response
			pop3CommunicationHandler.sendResponse(writer, POP3StatusIndicator.OK, argument + " " + dimension);			
		}
	}
	
	public void RETRCommand(POP3CommunicationHandler pop3CommunicationHandler, BufferedOutputStream writer, String argument, PersistanceManager persistanceManager, String clientId){
		
		POP3SessionStatus status = getStatus(persistanceManager, clientId);
		
		// Check the status of the POP3 session
		if(!status.equals(POP3SessionStatus.TRANSACTION)){
			setLastCommand(persistanceManager, clientId, POP3Command.RETR, POP3StatusIndicator.ERR);
			pop3CommunicationHandler.sendResponse(writer, POP3StatusIndicator.ERR, "This command is available only in TRANSACTION status.");
			return;
		}
		
		// Check the argument
		if(argument.isEmpty() || argument == null){
			setLastCommand(persistanceManager, clientId, POP3Command.RETR, POP3StatusIndicator.ERR);
			pop3CommunicationHandler.sendResponse(writer, POP3StatusIndicator.ERR, "Missing command argument");
			return;
		}
		
		// Check if the specified message exists
		// TODO: check the message identified by the argument in the DB
		// TODO: send an error if the message does not exists and return
		
		// Send the message in the multiline response
		setLastCommand(persistanceManager, clientId, POP3Command.RETR, POP3StatusIndicator.OK);
		pop3CommunicationHandler.sendResponse(writer, POP3StatusIndicator.OK, "message follows");
		
		// TODO: send the message contents
	}
	
	public void DELECommand(POP3CommunicationHandler pop3CommunicationHandler, BufferedOutputStream writer, String argument, PersistanceManager persistanceManager, String clientId){
		
		POP3SessionStatus status = getStatus(persistanceManager, clientId);
		
		// Check the status of the POP3 session
		if(!status.equals(POP3SessionStatus.TRANSACTION)){
			setLastCommand(persistanceManager, clientId, POP3Command.DELE, POP3StatusIndicator.ERR);
			pop3CommunicationHandler.sendResponse(writer, POP3StatusIndicator.ERR, "This command is available only in TRANSACTION status.");
			return;
		}
		
		// Check the argument
		if(argument.isEmpty() || argument == null){
			setLastCommand(persistanceManager, clientId, POP3Command.DELE, POP3StatusIndicator.ERR);
			pop3CommunicationHandler.sendResponse(writer, POP3StatusIndicator.ERR, "Missing command argument.");
			return;
		}
		
		// Search for such a message
		// TODO: search for the message
		
		// Mark the message, if found for deletion. If there is no such message, send a negative response and return.
		// TODO: mark the message for deletion
		
		// Send positive response
		setLastCommand(persistanceManager, clientId, POP3Command.DELE, POP3StatusIndicator.OK);
		pop3CommunicationHandler.sendResponse(writer, POP3StatusIndicator.OK, "Message marked for deletion.");
	}
	
	public void TOPCommand(POP3CommunicationHandler pop3CommunicationHandler, BufferedOutputStream writer, String argument, String secondArgument, PersistanceManager persistanceManager, String clientId){
		
		POP3SessionStatus status = getStatus(persistanceManager, clientId);
		
		// Check the status of the POP3 session
		if(!status.equals(POP3SessionStatus.TRANSACTION)){
			setLastCommand(persistanceManager, clientId, POP3Command.TOP, POP3StatusIndicator.ERR);
			pop3CommunicationHandler.sendResponse(writer, POP3StatusIndicator.ERR, "This command is available only in TRANSACTION status.");
			return;
		}
		
		// Check the argument
		if(argument.isEmpty() || argument == null || secondArgument.isEmpty() || secondArgument == null){
			setLastCommand(persistanceManager, clientId, POP3Command.TOP, POP3StatusIndicator.ERR);
			pop3CommunicationHandler.sendResponse(writer, POP3StatusIndicator.ERR, "Missing command argument.");
			return;
		}
		
		// Check if the specified message exists
		// TODO: check the message identified by the argument in the DB
		// TODO: send an error if the message does not exists and return
		
		// Send the message in the multiline response
		setLastCommand(persistanceManager, clientId, POP3Command.TOP, POP3StatusIndicator.OK);
		pop3CommunicationHandler.sendResponse(writer, POP3StatusIndicator.OK, "top of message follows");
		
		// TODO: send the message contents
	}
	
	public void NOOPCommand(POP3CommunicationHandler pop3CommunicationHandler, BufferedOutputStream writer, PersistanceManager persistanceManager, String clientId){
		
		POP3SessionStatus status = getStatus(persistanceManager, clientId);
		
		// Check the status of the POP3 session
		if(!status.equals(POP3SessionStatus.TRANSACTION)){
			setLastCommand(persistanceManager, clientId, POP3Command.NOOP, POP3StatusIndicator.ERR);
			pop3CommunicationHandler.sendResponse(writer, POP3StatusIndicator.ERR, "This command is available only in TRANSACTION status.");
			return;
		}
		
		setLastCommand(persistanceManager, clientId, POP3Command.NOOP, POP3StatusIndicator.OK);
		pop3CommunicationHandler.sendResponse(writer, POP3StatusIndicator.OK, "NOOP command received.");
	}
	
	public void RSETCommand(POP3CommunicationHandler pop3CommunicationHandler, BufferedOutputStream writer, PersistanceManager persistanceManager, String clientId){
		
		POP3SessionStatus status = getStatus(persistanceManager, clientId);
		
		// Check the status of the POP3 session
		if(!status.equals(POP3SessionStatus.TRANSACTION)){
			setLastCommand(persistanceManager, clientId, POP3Command.RSET, POP3StatusIndicator.ERR);
			pop3CommunicationHandler.sendResponse(writer, POP3StatusIndicator.ERR, "This command is available only in TRANSACTION status.");
			return;
		}
		
		// Unmarks every messaged marked for deletion
		// TODO: search for such messages in the DB and unmark each one
		
		setLastCommand(persistanceManager, clientId, POP3Command.RSET, POP3StatusIndicator.OK);
		pop3CommunicationHandler.sendResponse(writer, POP3StatusIndicator.OK, "Maildrop reset completed.");
	}
	
	public void unsupportedCommand(POP3CommunicationHandler pop3CommunicationHandler, PersistanceManager persistanceManager, String clientId, BufferedOutputStream writer){
		setLastCommand(persistanceManager, clientId, POP3Command.UNSUPPORTED, POP3StatusIndicator.ERR);
		pop3CommunicationHandler.sendResponse(writer, POP3StatusIndicator.ERR, "Command is not supported");
	}
	
	public void sendGreetings(POP3CommunicationHandler pop3CommunicationHandler, BufferedOutputStream writer, PersistanceManager persistanceManager, String clientId){
		
		// Store client ID in DB
		if(!persistanceManager.isPresent(StorageLocation.POP3_CLIENT_ID, clientId)){
			persistanceManager.create(StorageLocation.POP3_CLIENT_ID, clientId, POP3SessionStatus.GREETINGS.toString());
			setStatus(persistanceManager, POP3SessionStatus.AUTHORIZATION, clientId);
		}
		
		setLastCommand(persistanceManager, clientId, POP3Command.EMPTY, POP3StatusIndicator.OK);
		pop3CommunicationHandler.sendResponse(writer, POP3StatusIndicator.OK, "POP3 server ready to roll!");
	}
	
	private POP3Command getPreviousCommand(PersistanceManager persistanceManager, String clientId){
		String result = persistanceManager.read(StorageLocation.POP3_PREVIOUS_COMMAND, clientId);

		if(result.isEmpty()){
			return POP3Command.EMPTY;
		}else{
			return POP3Command.parseCommand(result);
		}
	}
	
	private String getPreviousCommandFirstArgument(PersistanceManager persistanceManager, String clientId){
		return persistanceManager.read(StorageLocation.POP3_PREVIOUS_COMMAND_FIRST_ARGUMENT, clientId);
	}
	
	private POP3StatusIndicator getPreviousCommandResult(PersistanceManager persistanceManager, String clientId){
		String result = persistanceManager.read(StorageLocation.POP3_PREVIOUS_COMMAND_RESULT, clientId);

		if(result.isEmpty()){
			return POP3StatusIndicator.UNKNOWN;
		}else{
			return POP3StatusIndicator.parseStatusIndicator(result);
		}
	}
	
	private void setLastCommand(PersistanceManager persistanceManager, String clientId, POP3Command pop3Command, POP3StatusIndicator pop3StatusIndicator){
		persistanceManager.update(StorageLocation.POP3_PREVIOUS_COMMAND, clientId, pop3Command.toString());
		persistanceManager.update(StorageLocation.POP3_PREVIOUS_COMMAND_RESULT, clientId, pop3StatusIndicator.toString());
		
		// Clean the argument fields
		persistanceManager.update(StorageLocation.POP3_PREVIOUS_COMMAND_FIRST_ARGUMENT, clientId, "");
		persistanceManager.update(StorageLocation.POP3_PREVIOUS_COMMAND_SECOND_ARGUMENT, clientId, "");
	}
	
	private void setLastCommandWithOneArgument(PersistanceManager persistanceManager, String clientId, POP3Command pop3Command, POP3StatusIndicator pop3StatusIndicator, String argument){
		
		setLastCommand(persistanceManager, clientId, pop3Command, pop3StatusIndicator);
		persistanceManager.update(StorageLocation.POP3_PREVIOUS_COMMAND_FIRST_ARGUMENT, clientId, argument);

		// Clean the second argument field
		persistanceManager.update(StorageLocation.POP3_PREVIOUS_COMMAND_SECOND_ARGUMENT, clientId, "");
		
	}
	
	private POP3SessionStatus getStatus(PersistanceManager persistanceManager, String clientId){
		
		return POP3SessionStatus.parseStatus(persistanceManager.read(StorageLocation.POP3_STATUS, clientId));
	}

	private void setStatus(PersistanceManager persistanceManager, POP3SessionStatus status, String clientId){

		POP3SessionStatus currentStatus = getStatus(persistanceManager, clientId);
		
		if(!currentStatus.equals(status)){
			persistanceManager.update(StorageLocation.POP3_STATUS, clientId, status.toString());
		}
	}
}
