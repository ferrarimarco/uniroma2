package controller.pop3;

import java.io.BufferedOutputStream;
import java.util.ArrayList;
import java.util.List;

import controller.persistance.FieldName;
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
		if(argument.isEmpty()){
			setLastCommand(persistanceManager, clientId, POP3Command.USER, POP3StatusIndicator.ERR);
			pop3CommunicationHandler.sendResponse(writer, POP3StatusIndicator.ERR, "Missing command argument.");
			return;
		}
		
		if(!persistanceManager.isPresent(StorageLocation.POP3_USERS, FieldName.POP3_USER_NAME, argument)){
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
		if(argument.isEmpty()){
			setLastCommand(persistanceManager, clientId, POP3Command.PASS, POP3StatusIndicator.ERR);
			pop3CommunicationHandler.sendResponse(writer, POP3StatusIndicator.ERR, "Missing command argument");
		}
		
		// TODO: hash the password?
		// Get the password for the current user name
		String userName = getPreviousCommandFirstArgument(persistanceManager, clientId);
		String password = persistanceManager.read(StorageLocation.POP3_PASSWORDS, FieldName.POP3_USER_PASSWORD, userName);
		
		if(!password.equals(argument)){// Wrong password
			setLastCommand(persistanceManager, clientId, POP3Command.PASS, POP3StatusIndicator.ERR);
			pop3CommunicationHandler.sendResponse(writer, POP3StatusIndicator.ERR, "Wrong password for user " + userName);		
		}
		
		// Store the authenticated user name
		persistanceManager.update(StorageLocation.POP3_SESSIONS, FieldName.POP3_SESSION_USER_NAME, clientId, userName);
		
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
			
			setStatus(persistanceManager, POP3SessionStatus.UPDATE, clientId);
			
			pop3CommunicationHandler.sendResponse(writer, POP3StatusIndicator.OK, "Closing session");
		}
		
		// TODO: unlock mailbox
		
		persistanceManager.scanAndDeletePop3Messages(clientId);
		
		persistanceManager.delete(StorageLocation.POP3_SESSIONS, clientId);
	}

	public void CAPACommand(POP3CommunicationHandler pop3CommunicationHandler, BufferedOutputStream writer, PersistanceManager persistanceManager, String clientId){

		POP3SessionStatus status = getStatus(persistanceManager, clientId);
		
		if(status.equals(POP3SessionStatus.GREETINGS)){
			setLastCommand(persistanceManager, clientId, POP3Command.CAPA, POP3StatusIndicator.ERR);
			pop3CommunicationHandler.sendResponse(writer, POP3StatusIndicator.ERR, "This command is not available in GREETINGS status");
		}else{
			
			setLastCommand(persistanceManager, clientId, POP3Command.CAPA, POP3StatusIndicator.OK);
			
			pop3CommunicationHandler.sendResponse(writer, POP3StatusIndicator.OK, "Capabilities follow.");
			
			List<String> capabilities = POP3Command.getPOP3CapaCommands();
			
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
		int totalDimension = 0;
		
		// Get maildrop info about the user from DB
		List<String> messageDimension = persistanceManager.scanForMessageDimensions(clientId);
		
		messages = messageDimension.size();
		
		// Compute total size
		for(int i = 0; i < messageDimension.size(); i++){
			totalDimension += Integer.parseInt(messageDimension.get(i));
		}
		
		setLastCommand(persistanceManager, clientId, POP3Command.STAT, POP3StatusIndicator.OK);
		
		pop3CommunicationHandler.sendResponse(writer, POP3StatusIndicator.OK, messages + " " + totalDimension);
	}
	
	public void LISTCommand(POP3CommunicationHandler pop3CommunicationHandler, BufferedOutputStream writer, String argument, PersistanceManager persistanceManager, String clientId){
		
		// TODO: check if the order of the elements is consistent
		
		POP3SessionStatus status = getStatus(persistanceManager, clientId);
		
		// Check the status of the POP3 session
		if(!status.equals(POP3SessionStatus.TRANSACTION)){
			setLastCommand(persistanceManager, clientId, POP3Command.LIST, POP3StatusIndicator.ERR);
			pop3CommunicationHandler.sendResponse(writer, POP3StatusIndicator.ERR, "This command is available only in TRANSACTION status.");
			return;
		}
		
		// Check the argument
		if(argument.isEmpty()){
			
			setLastCommand(persistanceManager, clientId, POP3Command.LIST, POP3StatusIndicator.OK);
			
			// Get information about all the messages in the maildrop
			List<String> messageDimensions = persistanceManager.scanForMessageDimensions(clientId);
			
			pop3CommunicationHandler.sendResponse(writer, POP3StatusIndicator.OK, messageDimensions.size() + " messages");
			
			// Send response			
			if(messageDimensions.size() > 0){
				
				pop3CommunicationHandler.sendListAsMultiLineResponse(writer, messageDimensions);

			}else{// No messages in the maildrop
				pop3CommunicationHandler.sendResponse(writer, POP3StatusIndicator.OK, "");
				pop3CommunicationHandler.sendBlankLineMultilineEnd(writer);
			}
			
		}else{
			
			setLastCommandWithOneArgument(persistanceManager, clientId, POP3Command.LIST, POP3StatusIndicator.OK, argument);

			List<String> messageDimensions = persistanceManager.scanForMessageDimensions(clientId);
			
			// Check if such message exists
			if(Integer.parseInt(argument) >= messageDimensions.size() || Integer.parseInt(argument) < 0){
				pop3CommunicationHandler.sendResponse(writer, POP3StatusIndicator.ERR, "no such message");
			}	
			
			// Get the dimension of the message
			int dimension = Integer.parseInt(messageDimensions.get(Integer.parseInt(argument)));

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
		if(argument.isEmpty() || Integer.parseInt(argument) < 0){
			setLastCommand(persistanceManager, clientId, POP3Command.RETR, POP3StatusIndicator.ERR);
			pop3CommunicationHandler.sendResponse(writer, POP3StatusIndicator.ERR, "Missing command argument");
			return;
		}
		
		// Get UIDs for all the messages
		List<String> uids = persistanceManager.getMessageUIDs(clientId);
		
		int messageNumber = Integer.parseInt(argument);
		
		// Check if the specified message exists
		if(messageNumber >= uids.size()){
			setLastCommand(persistanceManager, clientId, POP3Command.RETR, POP3StatusIndicator.ERR);
			pop3CommunicationHandler.sendResponse(writer, POP3StatusIndicator.ERR, "no such message");
			return;	
		}
		
		// Get message data
		String messageHeader = persistanceManager.read(StorageLocation.POP3_MAILDROPS, FieldName.POP3_MESSAGE_HEADER, uids.get(messageNumber));
		String message = persistanceManager.read(StorageLocation.POP3_MAILDROPS, FieldName.POP3_MESSAGE_DATA, uids.get(messageNumber));
		
		// Send the message in the multiline response
		setLastCommandWithOneArgument(persistanceManager, clientId, POP3Command.RETR, POP3StatusIndicator.OK, argument);
		pop3CommunicationHandler.sendResponse(writer, POP3StatusIndicator.OK, "message follows");
		
		// TODO: check if this is correct. Perhaps there is something that terminates the header (a blank line?)
		String toSend = messageHeader + message;
		
		pop3CommunicationHandler.sendString(writer, toSend);
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
		if(argument.isEmpty() || Integer.parseInt(argument) < 0){
			setLastCommand(persistanceManager, clientId, POP3Command.DELE, POP3StatusIndicator.ERR);
			pop3CommunicationHandler.sendResponse(writer, POP3StatusIndicator.ERR, "Missing command argument.");
			return;
		}
		
		// Get UIDs for all the messages
		List<String> uids = persistanceManager.getMessageUIDs(clientId);
		
		int messageNumber = Integer.parseInt(argument);
		
		// Check if the specified message exists
		if(messageNumber >= uids.size()){
			setLastCommand(persistanceManager, clientId, POP3Command.RETR, POP3StatusIndicator.ERR);
			pop3CommunicationHandler.sendResponse(writer, POP3StatusIndicator.ERR, "no such message");
			return;	
		}
		
		// Mark the message for deletion
		persistanceManager.update(StorageLocation.POP3_MAILDROPS, FieldName.POP3_MESSAGE_TO_DELETE, uids.get(messageNumber), POP3MessageDeletion.YES.toString());

		// Send positive response
		setLastCommandWithOneArgument(persistanceManager, clientId, POP3Command.DELE, POP3StatusIndicator.OK, argument);
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
		if(argument.isEmpty() || secondArgument.isEmpty() || Integer.parseInt(secondArgument) < 0){
			setLastCommand(persistanceManager, clientId, POP3Command.TOP, POP3StatusIndicator.ERR);
			pop3CommunicationHandler.sendResponse(writer, POP3StatusIndicator.ERR, "Missing command argument.");
			return;
		}
		
		// Get UIDs for all the messages
		List<String> uids = persistanceManager.getMessageUIDs(clientId);
		
		int messageNumber = Integer.parseInt(argument);
		
		// Check if the specified message exists
		if(messageNumber >= uids.size()){
			setLastCommand(persistanceManager, clientId, POP3Command.RETR, POP3StatusIndicator.ERR);
			pop3CommunicationHandler.sendResponse(writer, POP3StatusIndicator.ERR, "no such message");
			return;	
		}
		
		// Get message data
		String messageHeader = persistanceManager.read(StorageLocation.POP3_MAILDROPS, FieldName.POP3_MESSAGE_HEADER, uids.get(messageNumber));
		String message = persistanceManager.read(StorageLocation.POP3_MAILDROPS, FieldName.POP3_MESSAGE_DATA, uids.get(messageNumber));
		
		int lines = Integer.parseInt(secondArgument);
		
		// Send the message in the multiline response
		setLastCommand(persistanceManager, clientId, POP3Command.TOP, POP3StatusIndicator.OK);
		pop3CommunicationHandler.sendResponse(writer, POP3StatusIndicator.OK, "top of message follows");
		
		pop3CommunicationHandler.sendTOPResponse(messageHeader, message, lines);
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
		
		// Get UIDs for all the messages
		List<String> uids = persistanceManager.getMessageUIDs(clientId);
		
		// Unmarks every messaged marked for deletion
		for(int i = 0; i < uids.size(); i++){
			persistanceManager.update(StorageLocation.POP3_MAILDROPS, FieldName.POP3_MESSAGE_TO_DELETE, uids.get(i), POP3MessageDeletion.NO.toString());
		}

		setLastCommand(persistanceManager, clientId, POP3Command.RSET, POP3StatusIndicator.OK);
		pop3CommunicationHandler.sendResponse(writer, POP3StatusIndicator.OK, "Maildrop reset completed.");
	}
	
	public void UIDLCommand(POP3CommunicationHandler pop3CommunicationHandler, BufferedOutputStream writer, String argument, PersistanceManager persistanceManager, String clientId) {
		POP3SessionStatus status = getStatus(persistanceManager, clientId);
		
		// Check the status of the POP3 session
		if(!status.equals(POP3SessionStatus.TRANSACTION)){
			setLastCommand(persistanceManager, clientId, POP3Command.UIDL, POP3StatusIndicator.ERR);
			pop3CommunicationHandler.sendResponse(writer, POP3StatusIndicator.ERR, "This command is available only in TRANSACTION status.");
			return;
		}
		
		// Check the argument
		if(argument.isEmpty()){
			
			setLastCommand(persistanceManager, clientId, POP3Command.LIST, POP3StatusIndicator.OK);
			
			// Get information about all the messages in the maildrop
			List<String> uids = persistanceManager.getMessageUIDs(clientId);
			
			pop3CommunicationHandler.sendResponse(writer, POP3StatusIndicator.OK, "");
			
			// Send response			
			if(uids.size() > 0){
				
				// Add message number
				for(int i = 0; i < uids.size(); i++){
					uids.set(i, i + " " + uids.get(i));
				}
				
				pop3CommunicationHandler.sendListAsMultiLineResponse(writer, uids);

			}else{// No messages in the maildrop
				pop3CommunicationHandler.sendResponse(writer, POP3StatusIndicator.OK, "");
				pop3CommunicationHandler.sendBlankLineMultilineEnd(writer);
			}
			
		}else{
			
			setLastCommandWithOneArgument(persistanceManager, clientId, POP3Command.UIDL, POP3StatusIndicator.OK, argument);

			List<String> uids = persistanceManager.getMessageUIDs(clientId);
			
			// Check if such message exists
			if(Integer.parseInt(argument) >= uids.size() || Integer.parseInt(argument) < 0){
				pop3CommunicationHandler.sendResponse(writer, POP3StatusIndicator.ERR, "no such message");
			}	
			
			// Get the UID of the message
			String messageUid = uids.get(Integer.parseInt(argument));

			String response = argument.toString() + " " + messageUid;
			
			// Send the response
			pop3CommunicationHandler.sendResponse(writer, POP3StatusIndicator.OK, argument + " " + response);		
		}
	}
	
	public void unsupportedCommand(POP3CommunicationHandler pop3CommunicationHandler, PersistanceManager persistanceManager, String clientId, BufferedOutputStream writer){
		setLastCommand(persistanceManager, clientId, POP3Command.UNSUPPORTED, POP3StatusIndicator.ERR);
		pop3CommunicationHandler.sendResponse(writer, POP3StatusIndicator.ERR, "Command is not supported");
	}
	
	public void sendGreetings(POP3CommunicationHandler pop3CommunicationHandler, BufferedOutputStream writer, PersistanceManager persistanceManager, String clientId){
		
		// Store client ID in DB
		if(!persistanceManager.isPresent(StorageLocation.POP3_SESSIONS, FieldName.POP3_SESSION_ID, clientId)){
			
			persistanceManager.create(StorageLocation.POP3_SESSIONS, FieldName.getPOP3StatusTableFieldNames(), POP3SessionStatus.GREETINGS.toString());
			setStatus(persistanceManager, POP3SessionStatus.AUTHORIZATION, clientId);
		}
		
		setLastCommand(persistanceManager, clientId, POP3Command.EMPTY, POP3StatusIndicator.OK);
		pop3CommunicationHandler.sendResponse(writer, POP3StatusIndicator.OK, "POP3 server ready to roll!");
	}
	
	private POP3Command getPreviousCommand(PersistanceManager persistanceManager, String clientId){
		String result = persistanceManager.read(StorageLocation.POP3_SESSIONS, FieldName.POP3_LAST_COMMAND, clientId);

		if(result.isEmpty()){
			return POP3Command.EMPTY;
		}else{
			return POP3Command.parseCommand(result);
		}
	}
	
	private String getPreviousCommandFirstArgument(PersistanceManager persistanceManager, String clientId){
		return persistanceManager.read(StorageLocation.POP3_SESSIONS, FieldName.POP3_LAST_COMMAND_FIRST_ARGUMENT, clientId);
	}
	
	private POP3StatusIndicator getPreviousCommandResult(PersistanceManager persistanceManager, String clientId){
		String result = persistanceManager.read(StorageLocation.POP3_SESSIONS, FieldName.POP3_LAST_COMMAND_RESULT, clientId);

		if(result.isEmpty()){
			return POP3StatusIndicator.UNKNOWN;
		}else{
			return POP3StatusIndicator.parseStatusIndicator(result);
		}
	}
	
	private void setLastCommand(PersistanceManager persistanceManager, String clientId, POP3Command pop3Command, POP3StatusIndicator pop3StatusIndicator){
		persistanceManager.update(StorageLocation.POP3_SESSIONS, FieldName.POP3_LAST_COMMAND, clientId, pop3Command.toString());
		persistanceManager.update(StorageLocation.POP3_SESSIONS, FieldName.POP3_LAST_COMMAND_RESULT, clientId, pop3StatusIndicator.toString());
		
		// Clean the argument fields
		persistanceManager.update(StorageLocation.POP3_SESSIONS, FieldName.POP3_LAST_COMMAND_FIRST_ARGUMENT, clientId, "");
		persistanceManager.update(StorageLocation.POP3_SESSIONS, FieldName.POP3_LAST_COMMAND_SECOND_ARGUMENT, clientId, "");
	}
	
	private void setLastCommandWithOneArgument(PersistanceManager persistanceManager, String clientId, POP3Command pop3Command, POP3StatusIndicator pop3StatusIndicator, String argument){
		
		setLastCommand(persistanceManager, clientId, pop3Command, pop3StatusIndicator);
		persistanceManager.update(StorageLocation.POP3_SESSIONS, FieldName.POP3_LAST_COMMAND_FIRST_ARGUMENT, clientId, argument);

		// Clean the second argument field
		persistanceManager.update(StorageLocation.POP3_SESSIONS, FieldName.POP3_LAST_COMMAND_SECOND_ARGUMENT, clientId, "");
		
	}
	
	private POP3SessionStatus getStatus(PersistanceManager persistanceManager, String clientId){
		
		return POP3SessionStatus.parseStatus(persistanceManager.read(StorageLocation.POP3_SESSIONS, FieldName.POP3_SESSION_STATUS, clientId));
	}

	private void setStatus(PersistanceManager persistanceManager, POP3SessionStatus status, String clientId){

		POP3SessionStatus currentStatus = getStatus(persistanceManager, clientId);
		
		if(!currentStatus.equals(status)){
			persistanceManager.update(StorageLocation.POP3_SESSIONS, FieldName.POP3_SESSION_STATUS, clientId, status.toString());
		}
	}
}
