package controller.pop3;

import java.io.BufferedOutputStream;
import java.util.List;

import controller.AbstractCommandHandler;
import controller.CommunicationHandler;
import controller.persistance.FieldName;
import controller.persistance.PersistanceManager;
import controller.persistance.StorageLocation;

// TODO: send last command update only for commands that need to check the previous command?

public class POP3CommandHandler extends AbstractCommandHandler {
	
	@Override
	public void handleCommand(CommunicationHandler communicationHandler, BufferedOutputStream writer, String command, String argument, String secondArgument, PersistanceManager persistanceManager, String clientId){
		
		POP3Command pop3Command = POP3Command.parseCommand(command);
		
		if(pop3Command.equals(POP3Command.CAPA)){
			CAPACommand(communicationHandler, writer, persistanceManager, clientId);
		}else if(pop3Command.equals(POP3Command.QUIT)){
			QUITCommand(communicationHandler, writer, persistanceManager, clientId);
		}else if(pop3Command.equals(POP3Command.USER)){
			USERCommand(communicationHandler, writer, argument, persistanceManager, clientId);
		}else if(pop3Command.equals(POP3Command.PASS)){
			PASSCommand(communicationHandler, writer, argument, persistanceManager, clientId);
		}else if(pop3Command.equals(POP3Command.STAT)){
			STATCommand(communicationHandler, writer, persistanceManager, clientId);
		}else if(pop3Command.equals(POP3Command.LIST)){
			LISTCommand(communicationHandler, writer, argument, persistanceManager, clientId);
		}else if(pop3Command.equals(POP3Command.RETR)){
			RETRCommand(communicationHandler, writer, argument, persistanceManager, clientId);
		}else if(pop3Command.equals(POP3Command.DELE)){
			DELECommand(communicationHandler, writer, argument, persistanceManager, clientId);
		}else if(pop3Command.equals(POP3Command.NOOP)){
			NOOPCommand(communicationHandler, writer, persistanceManager, clientId);
		}else if(pop3Command.equals(POP3Command.RSET)){
			RSETCommand(communicationHandler, writer, persistanceManager, clientId);
		}else if(pop3Command.equals(POP3Command.TOP)){
			TOPCommand(communicationHandler, writer, argument, secondArgument, persistanceManager, clientId);
		}else if(pop3Command.equals(POP3Command.UIDL)){
			UIDLCommand(communicationHandler, writer, argument, persistanceManager, clientId);
		}else if(pop3Command.equals(POP3Command.UNSUPPORTED)){
			unsupportedCommand(communicationHandler, persistanceManager, clientId, writer);
		}
	}
	
	private void USERCommand(CommunicationHandler communicationHandler, BufferedOutputStream writer, String argument, PersistanceManager persistanceManager, String clientId){
		
		POP3SessionStatus status = getStatus(persistanceManager, clientId);
		
		// Check the status of the POP3 session: USER is available only in AUTH status
		if(!status.equals(POP3SessionStatus.AUTHORIZATION)){
			setLastCommand(persistanceManager, clientId, POP3Command.USER, POP3StatusIndicator.ERR);
			communicationHandler.sendResponse(writer, POP3StatusIndicator.ERR.toString(), "This command is available only in AUTHORIZATION status.");
			return;
		}
		
		// Check the previous command: USER is available only after the POP3 server greeting or after an unsuccessful USER or PASS command
		POP3Command previousCommand = getPreviousCommand(persistanceManager, clientId);
		POP3StatusIndicator previousCommandResult = getPreviousCommandResult(persistanceManager, clientId);
		
		if(previousCommand.equals(POP3Command.USER) && previousCommandResult.equals(POP3StatusIndicator.OK)){
			setLastCommand(persistanceManager, clientId, POP3Command.USER, POP3StatusIndicator.ERR);
			communicationHandler.sendResponse(writer, POP3StatusIndicator.ERR.toString(), "Invalid command sequence. PASS expected. You have to start over.");
			
			return;
		}
		
		// Check the argument
		if(argument.isEmpty()){
			setLastCommand(persistanceManager, clientId, POP3Command.USER, POP3StatusIndicator.ERR);
			communicationHandler.sendResponse(writer, POP3StatusIndicator.ERR.toString(), "Missing command argument.");
			return;
		}
		
		if(!persistanceManager.isPresent(StorageLocation.POP3_USERS, FieldName.POP3_USER_NAME, argument)){
			setLastCommand(persistanceManager, clientId, POP3Command.USER, POP3StatusIndicator.ERR);
			communicationHandler.sendResponse(writer, POP3StatusIndicator.ERR.toString(), "There is no such user.");
			return;
		}
				
		setLastCommandWithOneArgument(persistanceManager, clientId, POP3Command.USER, POP3StatusIndicator.OK, argument);
		communicationHandler.sendResponse(writer, POP3StatusIndicator.OK.toString(), "User " + argument + " found.");	
	}
	
	private void PASSCommand(CommunicationHandler communicationHandler, BufferedOutputStream writer, String argument, PersistanceManager persistanceManager, String clientId){
		
		POP3SessionStatus status = getStatus(persistanceManager, clientId);
		
		// Check the status of the POP3 session: PASS is available only in AUTH status
		if(!status.equals(POP3SessionStatus.AUTHORIZATION)){
			setLastCommand(persistanceManager, clientId, POP3Command.PASS, POP3StatusIndicator.ERR);
			communicationHandler.sendResponse(writer, POP3StatusIndicator.ERR.toString(), "This command is available only in AUTHORIZATION status.");
			return;
		}
		
		// Check the previous command: PASS is available only after a successful USER command
		POP3Command previousCommand = getPreviousCommand(persistanceManager, clientId);
		POP3StatusIndicator previousCommandResult = getPreviousCommandResult(persistanceManager, clientId);
		
		if(!previousCommand.equals(POP3Command.USER) && !previousCommandResult.equals(POP3StatusIndicator.OK)){
			setLastCommand(persistanceManager, clientId, POP3Command.PASS, POP3StatusIndicator.ERR);
			communicationHandler.sendResponse(writer, POP3StatusIndicator.ERR.toString(), "Invalid sequence. USER not selected. You have to start over.");
			return;
		}
		
		// Check the argument
		if(argument.isEmpty()){
			setLastCommand(persistanceManager, clientId, POP3Command.PASS, POP3StatusIndicator.ERR);
			communicationHandler.sendResponse(writer, POP3StatusIndicator.ERR.toString(), "Missing command argument");
			return;
		}
		
		// TODO: hash the password?
		// Get the password for the current user name
		String userName = getPreviousCommandFirstArgument(persistanceManager, clientId);
		String password = persistanceManager.read(StorageLocation.POP3_PASSWORDS, FieldName.POP3_USER_PASSWORD, userName);
		
		if(!password.equals(argument)){// Wrong password
			setLastCommand(persistanceManager, clientId, POP3Command.PASS, POP3StatusIndicator.ERR);
			communicationHandler.sendResponse(writer, POP3StatusIndicator.ERR.toString(), "Wrong password for user " + userName);
			return;
		}
		
		List<FieldName> usernameField = FieldName.getPOP3UsernameTableFieldNames();
		
		persistanceManager.update(StorageLocation.POP3_SESSIONS, clientId, usernameField, userName);
		
		setLastCommandWithOneArgument(persistanceManager, clientId, POP3Command.PASS, POP3StatusIndicator.OK, argument);
		
		setStatus(persistanceManager, POP3SessionStatus.TRANSACTION, clientId);
		
		communicationHandler.sendResponse(writer, POP3StatusIndicator.OK.toString(), "Authentication successful.");
	}
	
	private void QUITCommand(CommunicationHandler communicationHandler, BufferedOutputStream writer, PersistanceManager persistanceManager, String clientId){
		
		POP3SessionStatus status = getStatus(persistanceManager, clientId);
		
		if(status.equals(POP3SessionStatus.GREETINGS)){
			setLastCommand(persistanceManager, clientId, POP3Command.QUIT, POP3StatusIndicator.ERR);
			communicationHandler.sendResponse(writer, POP3StatusIndicator.ERR.toString(), "POP3 server is in GREETINGS status");
			return;
		}else{
			setLastCommand(persistanceManager, clientId, POP3Command.QUIT, POP3StatusIndicator.OK);
			setStatus(persistanceManager, POP3SessionStatus.UPDATE, clientId);
			communicationHandler.sendResponse(writer, POP3StatusIndicator.OK.toString(), "Closing session");
		}
		
		// TODO: unlock mailbox
		
		persistanceManager.scanAndDeletePop3Messages(clientId);
		
		persistanceManager.delete(StorageLocation.POP3_SESSIONS, clientId);
	}

	private void CAPACommand(CommunicationHandler communicationHandler, BufferedOutputStream writer, PersistanceManager persistanceManager, String clientId){

		POP3SessionStatus status = getStatus(persistanceManager, clientId);
		
		if(status.equals(POP3SessionStatus.GREETINGS)){
			setLastCommand(persistanceManager, clientId, POP3Command.CAPA, POP3StatusIndicator.ERR);
			communicationHandler.sendResponse(writer, POP3StatusIndicator.ERR.toString(), "This command is not available in GREETINGS status");
			return;
		}else{
			setLastCommand(persistanceManager, clientId, POP3Command.CAPA, POP3StatusIndicator.OK);
			communicationHandler.sendResponse(writer, POP3StatusIndicator.OK.toString(), "Capabilities follow.");
			List<String> capabilities = POP3Command.getPOP3CapaCommands();
			communicationHandler.sendListAsMultiLineResponse(writer, capabilities);
		}
	}
	
	private void STATCommand(CommunicationHandler communicationHandler, BufferedOutputStream writer, PersistanceManager persistanceManager, String clientId){
		
		POP3SessionStatus status = getStatus(persistanceManager, clientId);
		
		// Check the status of the POP3 session
		if(!status.equals(POP3SessionStatus.TRANSACTION)){
			setLastCommand(persistanceManager, clientId, POP3Command.STAT, POP3StatusIndicator.ERR);
			communicationHandler.sendResponse(writer, POP3StatusIndicator.ERR.toString(), "This command is available only in TRANSACTION status.");
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
		
		communicationHandler.sendResponse(writer, POP3StatusIndicator.OK.toString(), messages + " " + totalDimension);
	}
	
	private void LISTCommand(CommunicationHandler communicationHandler, BufferedOutputStream writer, String argument, PersistanceManager persistanceManager, String clientId){
		
		// TODO: check if the order of the elements is consistent
		
		POP3SessionStatus status = getStatus(persistanceManager, clientId);
		
		// Check the status of the POP3 session
		if(!status.equals(POP3SessionStatus.TRANSACTION)){
			setLastCommand(persistanceManager, clientId, POP3Command.LIST, POP3StatusIndicator.ERR);
			communicationHandler.sendResponse(writer, POP3StatusIndicator.ERR.toString(), "This command is available only in TRANSACTION status.");
			return;
		}
		
		// Check the argument
		if(argument.isEmpty()){
			
			setLastCommand(persistanceManager, clientId, POP3Command.LIST, POP3StatusIndicator.OK);
			
			// Get information about all the messages in the maildrop
			List<String> messageDimensions = persistanceManager.scanForMessageDimensions(clientId);
			
			communicationHandler.sendResponse(writer, POP3StatusIndicator.OK.toString(), messageDimensions.size() + " messages");
			
			// Send response			
			if(messageDimensions.size() > 0){
				
				communicationHandler.sendListAsMultiLineResponse(writer, messageDimensions);

			}else{// No messages in the maildrop
				communicationHandler.sendResponse(writer, POP3StatusIndicator.OK.toString(), "");
				communicationHandler.sendBlankLineMultilineEnd(writer);
			}
			
		}else{
			
			setLastCommandWithOneArgument(persistanceManager, clientId, POP3Command.LIST, POP3StatusIndicator.OK, argument);

			List<String> messageDimensions = persistanceManager.scanForMessageDimensions(clientId);
			
			// Check if such message exists
			if(Integer.parseInt(argument) >= messageDimensions.size() || Integer.parseInt(argument) < 0){
				communicationHandler.sendResponse(writer, POP3StatusIndicator.ERR.toString(), "no such message");
				return;
			}	
			
			// Get the dimension of the message
			int dimension = Integer.parseInt(messageDimensions.get(Integer.parseInt(argument)));

			// Send the response
			communicationHandler.sendResponse(writer, POP3StatusIndicator.OK.toString(), argument + " " + dimension);		
		}
	}
	
	private void RETRCommand(CommunicationHandler communicationHandler, BufferedOutputStream writer, String argument, PersistanceManager persistanceManager, String clientId){
		
		POP3SessionStatus status = getStatus(persistanceManager, clientId);
		
		// Check the status of the POP3 session
		if(!status.equals(POP3SessionStatus.TRANSACTION)){
			setLastCommand(persistanceManager, clientId, POP3Command.RETR, POP3StatusIndicator.ERR);
			communicationHandler.sendResponse(writer, POP3StatusIndicator.ERR.toString(), "This command is available only in TRANSACTION status.");
			return;
		}
		
		// Check the argument
		if(argument.isEmpty() || Integer.parseInt(argument) < 0){
			setLastCommand(persistanceManager, clientId, POP3Command.RETR, POP3StatusIndicator.ERR);
			communicationHandler.sendResponse(writer, POP3StatusIndicator.ERR.toString(), "Missing command argument");
			return;
		}
		
		// Get UIDs for all the messages
		List<String> uids = persistanceManager.getMessageUIDs(clientId);
		
		int messageNumber = Integer.parseInt(argument);
		
		// Check if the specified message exists
		if(messageNumber >= uids.size()){
			setLastCommand(persistanceManager, clientId, POP3Command.RETR, POP3StatusIndicator.ERR);
			communicationHandler.sendResponse(writer, POP3StatusIndicator.ERR.toString(), "no such message");
			return;	
		}
		
		// Get message data
		String messageHeader = persistanceManager.read(StorageLocation.POP3_MAILDROPS, FieldName.POP3_MESSAGE_HEADER, uids.get(messageNumber));
		String message = persistanceManager.read(StorageLocation.POP3_MAILDROPS, FieldName.POP3_MESSAGE_DATA, uids.get(messageNumber));
		
		// Send the message in the multiline response
		setLastCommandWithOneArgument(persistanceManager, clientId, POP3Command.RETR, POP3StatusIndicator.OK, argument);
		communicationHandler.sendResponse(writer, POP3StatusIndicator.OK.toString(), "message follows");
		
		// TODO: check if this is correct. Perhaps there is something that terminates the header (a blank line?)
		String toSend = messageHeader + message;
		
		communicationHandler.sendString(writer, toSend);
	}
	
	private void DELECommand(CommunicationHandler communicationHandler, BufferedOutputStream writer, String argument, PersistanceManager persistanceManager, String clientId){
		
		POP3SessionStatus status = getStatus(persistanceManager, clientId);
		
		// Check the status of the POP3 session
		if(!status.equals(POP3SessionStatus.TRANSACTION)){
			setLastCommand(persistanceManager, clientId, POP3Command.DELE, POP3StatusIndicator.ERR);
			communicationHandler.sendResponse(writer, POP3StatusIndicator.ERR.toString(), "This command is available only in TRANSACTION status.");
			return;
		}
		
		// Check the argument
		if(argument.isEmpty() || Integer.parseInt(argument) < 0){
			setLastCommand(persistanceManager, clientId, POP3Command.DELE, POP3StatusIndicator.ERR);
			communicationHandler.sendResponse(writer, POP3StatusIndicator.ERR.toString(), "Missing command argument.");
			return;
		}
		
		// Get UIDs for all the messages
		List<String> uids = persistanceManager.getMessageUIDs(clientId);
		
		int messageNumber = Integer.parseInt(argument);
		
		// Check if the specified message exists
		if(messageNumber >= uids.size()){
			setLastCommand(persistanceManager, clientId, POP3Command.RETR, POP3StatusIndicator.ERR);
			communicationHandler.sendResponse(writer, POP3StatusIndicator.ERR.toString(), "no such message");
			return;	
		}
		
		List<FieldName> messageToDeleteField = FieldName.getMessageToDeleteTableFieldOnly();
		
		// Mark the message for deletion
		persistanceManager.update(StorageLocation.POP3_MAILDROPS, uids.get(messageNumber), messageToDeleteField, POP3MessageDeletion.YES.toString());

		// Send positive response
		setLastCommandWithOneArgument(persistanceManager, clientId, POP3Command.DELE, POP3StatusIndicator.OK, argument);
		communicationHandler.sendResponse(writer, POP3StatusIndicator.OK.toString(), "Message marked for deletion.");
	}
	
	private void TOPCommand(CommunicationHandler communicationHandler, BufferedOutputStream writer, String argument, String secondArgument, PersistanceManager persistanceManager, String clientId){
		
		POP3SessionStatus status = getStatus(persistanceManager, clientId);
		
		// Check the status of the POP3 session
		if(!status.equals(POP3SessionStatus.TRANSACTION)){
			setLastCommand(persistanceManager, clientId, POP3Command.TOP, POP3StatusIndicator.ERR);
			communicationHandler.sendResponse(writer, POP3StatusIndicator.ERR.toString(), "This command is available only in TRANSACTION status.");
			return;
		}
		
		// Check the argument
		if(argument.isEmpty() || secondArgument.isEmpty() || Integer.parseInt(secondArgument) < 0){
			setLastCommand(persistanceManager, clientId, POP3Command.TOP, POP3StatusIndicator.ERR);
			communicationHandler.sendResponse(writer, POP3StatusIndicator.ERR.toString(), "Missing command argument.");
			return;
		}
		
		// Get UIDs for all the messages
		List<String> uids = persistanceManager.getMessageUIDs(clientId);
		
		int messageNumber = Integer.parseInt(argument);
		
		// Check if the specified message exists
		if(messageNumber >= uids.size()){
			setLastCommand(persistanceManager, clientId, POP3Command.RETR, POP3StatusIndicator.ERR);
			communicationHandler.sendResponse(writer, POP3StatusIndicator.ERR.toString(), "no such message");
			return;	
		}
		
		// Get message data
		String messageHeader = persistanceManager.read(StorageLocation.POP3_MAILDROPS, FieldName.POP3_MESSAGE_HEADER, uids.get(messageNumber));
		String message = persistanceManager.read(StorageLocation.POP3_MAILDROPS, FieldName.POP3_MESSAGE_DATA, uids.get(messageNumber));
		
		// TODO: check if this is correct. Perhaps there is something that terminates the header (a blank line?)
		String toSend = messageHeader + message;
		
		int lines = Integer.parseInt(secondArgument);
		
		// Send the message in the multiline response
		setLastCommand(persistanceManager, clientId, POP3Command.TOP, POP3StatusIndicator.OK);
		communicationHandler.sendResponse(writer, POP3StatusIndicator.OK.toString(), "top of message follows");
		
		communicationHandler.sendStringWithLinesLimit(writer, toSend, lines);
	}
	
	private void NOOPCommand(CommunicationHandler communicationHandler, BufferedOutputStream writer, PersistanceManager persistanceManager, String clientId){
		
		POP3SessionStatus status = getStatus(persistanceManager, clientId);
		
		// Check the status of the POP3 session
		if(!status.equals(POP3SessionStatus.TRANSACTION)){
			setLastCommand(persistanceManager, clientId, POP3Command.NOOP, POP3StatusIndicator.ERR);
			communicationHandler.sendResponse(writer, POP3StatusIndicator.ERR.toString(), "This command is available only in TRANSACTION status.");
			return;
		}
		
		setLastCommand(persistanceManager, clientId, POP3Command.NOOP, POP3StatusIndicator.OK);
		communicationHandler.sendResponse(writer, POP3StatusIndicator.OK.toString(), "NOOP command received.");
	}
	
	private void RSETCommand(CommunicationHandler communicationHandler, BufferedOutputStream writer, PersistanceManager persistanceManager, String clientId){
		
		POP3SessionStatus status = getStatus(persistanceManager, clientId);
		
		// Check the status of the POP3 session
		if(!status.equals(POP3SessionStatus.TRANSACTION)){
			setLastCommand(persistanceManager, clientId, POP3Command.RSET, POP3StatusIndicator.ERR);
			communicationHandler.sendResponse(writer, POP3StatusIndicator.ERR.toString(), "This command is available only in TRANSACTION status.");
			return;
		}
		
		// Get UIDs for all the messages
		List<String> uids = persistanceManager.getMessageUIDs(clientId);
		
		List<FieldName> messageToDeleteField = FieldName.getMessageToDeleteTableFieldOnly();
		
		// Unmarks every messaged marked for deletion
		for(int i = 0; i < uids.size(); i++){
			persistanceManager.update(StorageLocation.POP3_MAILDROPS, uids.get(i), messageToDeleteField, POP3MessageDeletion.NO.toString());
		}

		setLastCommand(persistanceManager, clientId, POP3Command.RSET, POP3StatusIndicator.OK);
		communicationHandler.sendResponse(writer, POP3StatusIndicator.OK.toString(), "Maildrop reset completed.");
	}
	
	private void UIDLCommand(CommunicationHandler communicationHandler, BufferedOutputStream writer, String argument, PersistanceManager persistanceManager, String clientId) {
		POP3SessionStatus status = getStatus(persistanceManager, clientId);
		
		// Check the status of the POP3 session
		if(!status.equals(POP3SessionStatus.TRANSACTION)){
			setLastCommand(persistanceManager, clientId, POP3Command.UIDL, POP3StatusIndicator.ERR);
			communicationHandler.sendResponse(writer, POP3StatusIndicator.ERR.toString(), "This command is available only in TRANSACTION status.");
			return;
		}
		
		// Check the argument
		if(argument.isEmpty()){
			
			setLastCommand(persistanceManager, clientId, POP3Command.LIST, POP3StatusIndicator.OK);
			
			// Get information about all the messages in the maildrop
			List<String> uids = persistanceManager.getMessageUIDs(clientId);
			
			communicationHandler.sendResponse(writer, POP3StatusIndicator.OK.toString(), "");
			
			// Send response			
			if(uids.size() > 0){
				
				// Add message number
				for(int i = 0; i < uids.size(); i++){
					uids.set(i, i + " " + uids.get(i));
				}
				
				communicationHandler.sendListAsMultiLineResponse(writer, uids);

			}else{// No messages in the maildrop
				communicationHandler.sendResponse(writer, POP3StatusIndicator.OK.toString(), "");
				communicationHandler.sendBlankLineMultilineEnd(writer);
			}
			
		}else{
			
			setLastCommandWithOneArgument(persistanceManager, clientId, POP3Command.UIDL, POP3StatusIndicator.OK, argument);

			List<String> uids = persistanceManager.getMessageUIDs(clientId);
			
			// Check if such message exists
			if(Integer.parseInt(argument) >= uids.size() || Integer.parseInt(argument) < 0){
				communicationHandler.sendResponse(writer, POP3StatusIndicator.ERR.toString(), "no such message");
				return;
			}	
			
			// Get the UID of the message
			String messageUid = uids.get(Integer.parseInt(argument));

			String response = argument.toString() + " " + messageUid;
			
			// Send the response
			communicationHandler.sendResponse(writer, POP3StatusIndicator.OK.toString(), argument + " " + response);		
		}
	}
	
	private void unsupportedCommand(CommunicationHandler communicationHandler, PersistanceManager persistanceManager, String clientId, BufferedOutputStream writer){
		setLastCommand(persistanceManager, clientId, POP3Command.UNSUPPORTED, POP3StatusIndicator.ERR);
		communicationHandler.sendResponse(writer, POP3StatusIndicator.ERR.toString(), "Command is not supported");
	}
	
	@Override
	public void sendGreetings(CommunicationHandler communicationHandler, BufferedOutputStream writer, PersistanceManager persistanceManager, String clientId){
		
		// Store client ID in DB
		if(!persistanceManager.isPresent(StorageLocation.POP3_SESSIONS, FieldName.POP3_SESSION_ID, clientId)){
			
			
			// Set the status to AUTH directly to avoid another query
			persistanceManager.create(StorageLocation.POP3_SESSIONS, FieldName.getPOP3StatusTableFieldNames(), 
					clientId,
					POP3SessionStatus.AUTHORIZATION.toString(),
					POP3Command.EMPTY.toString(),
					POP3StatusIndicator.UNKNOWN.toString(),
					POP3Command.EMPTY.toString(),
					POP3Command.EMPTY.toString(),
					POP3Command.EMPTY.toString());

			communicationHandler.sendResponse(writer, POP3StatusIndicator.OK.toString(), "POP3 server ready to roll!");
		}
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
		List<FieldName> commandFields = FieldName.getPOP3StatusCommandTableFieldNames();
		
		persistanceManager.update(StorageLocation.POP3_SESSIONS, clientId, commandFields, 
				pop3Command.toString(), 
				pop3StatusIndicator.toString(), 
				POP3Command.EMPTY.toString(), 
				POP3Command.EMPTY.toString());
	}
	
	private void setLastCommandWithOneArgument(PersistanceManager persistanceManager, String clientId, POP3Command pop3Command, POP3StatusIndicator pop3StatusIndicator, String argument){
		
		List<FieldName> commandFields = FieldName.getPOP3StatusCommandTableFieldNames();
		
		persistanceManager.update(StorageLocation.POP3_SESSIONS, clientId, commandFields, 
				pop3Command.toString(), 
				pop3StatusIndicator.toString(), 
				argument, 
				POP3Command.EMPTY.toString());
	}
	
	private POP3SessionStatus getStatus(PersistanceManager persistanceManager, String clientId){
		return POP3SessionStatus.parseStatus(persistanceManager.read(StorageLocation.POP3_SESSIONS, FieldName.POP3_SESSION_STATUS, clientId));
	}

	private void setStatus(PersistanceManager persistanceManager, POP3SessionStatus status, String clientId){

		List<FieldName> statusFields = FieldName.getPOP3StatusTableFieldOnly();
		
		persistanceManager.update(StorageLocation.POP3_SESSIONS, clientId, statusFields, status.toString());
	}
}
