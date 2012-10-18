package controller.pop3;

import java.io.BufferedOutputStream;
import java.util.ArrayList;
import java.util.List;

import controller.AbstractCommandHandler;
import controller.AbstractRequestHandler;
import controller.CommunicationHandler;
import controller.SpecialCharactersSequence;
import controller.persistance.FieldName;
import controller.persistance.PersistanceManager;
import controller.persistance.StorageLocation;

public class POP3CommandHandler extends AbstractCommandHandler {

	// TODO: check when the client does not send the QUIT command if it receives errors.
	
	@Override
	public void handleCommand(CommunicationHandler communicationHandler, BufferedOutputStream writer, String line, String command, String argument, String secondArgument,
			PersistanceManager persistanceManager, String clientId) {

		POP3Command pop3Command = POP3Command.parseCommand(command);
		
		// TODO: debug
		AbstractRequestHandler.log.info("Server receives: " + line);
		
		if (pop3Command.equals(POP3Command.CAPA)) {
			CAPACommand(communicationHandler, writer, persistanceManager, clientId);
		} else if (pop3Command.equals(POP3Command.QUIT)) {
			QUITCommand(communicationHandler, writer, persistanceManager, clientId);
		} else if (pop3Command.equals(POP3Command.USER)) {
			USERCommand(communicationHandler, writer, argument, persistanceManager, clientId);
		} else if (pop3Command.equals(POP3Command.PASS)) {
			PASSCommand(communicationHandler, writer, argument, persistanceManager, clientId);
		} else if (pop3Command.equals(POP3Command.STAT)) {
			STATCommand(communicationHandler, writer, persistanceManager, clientId);
		} else if (pop3Command.equals(POP3Command.LIST)) {
			LISTCommand(communicationHandler, writer, argument, persistanceManager, clientId);
		} else if (pop3Command.equals(POP3Command.RETR)) {
			RETRCommand(communicationHandler, writer, argument, persistanceManager, clientId);
		} else if (pop3Command.equals(POP3Command.DELE)) {
			DELECommand(communicationHandler, writer, argument, persistanceManager, clientId);
		} else if (pop3Command.equals(POP3Command.NOOP)) {
			NOOPCommand(communicationHandler, writer, persistanceManager, clientId);
		} else if (pop3Command.equals(POP3Command.RSET)) {
			RSETCommand(communicationHandler, writer, persistanceManager, clientId);
		} else if (pop3Command.equals(POP3Command.TOP)) {
			TOPCommand(communicationHandler, writer, argument, secondArgument, persistanceManager, clientId);
		} else if (pop3Command.equals(POP3Command.UIDL)) {
			UIDLCommand(communicationHandler, writer, argument, persistanceManager, clientId);
		} else if (pop3Command.equals(POP3Command.UNSUPPORTED)) {
			unsupportedCommand(communicationHandler, persistanceManager, clientId, writer);
		}
	}

	private void USERCommand(CommunicationHandler communicationHandler, BufferedOutputStream writer, String argument, PersistanceManager persistanceManager, String clientId) {

		POP3SessionStatus status = getStatus(persistanceManager, clientId);

		// Check the status of the POP3 session: USER is available only in AUTH status
		if (!status.equals(POP3SessionStatus.AUTHORIZATION)) {
			setLastCommand(persistanceManager, clientId, POP3Command.USER, POP3StatusIndicator.ERR);
			communicationHandler.sendResponse(writer, POP3StatusIndicator.ERR.toString(), "This command is available only in AUTHORIZATION status.");
			return;
		}

		// Check the previous command: USER is available only after the POP3
		// server greeting or after an unsuccessful USER or PASS command
		POP3Command previousCommand = getPreviousCommand(persistanceManager, clientId);
		POP3StatusIndicator previousCommandResult = getPreviousCommandResult(persistanceManager, clientId);

		if (previousCommand.equals(POP3Command.USER) && previousCommandResult.equals(POP3StatusIndicator.OK)) {
			setLastCommand(persistanceManager, clientId, POP3Command.USER, POP3StatusIndicator.ERR);
			communicationHandler.sendResponse(writer, POP3StatusIndicator.ERR.toString(), "Invalid command sequence. PASS expected. You have to start over.");
			return;
		}

		// Check the argument
		if (argument.isEmpty()) {
			setLastCommand(persistanceManager, clientId, POP3Command.USER, POP3StatusIndicator.ERR);
			communicationHandler.sendResponse(writer, POP3StatusIndicator.ERR.toString(), "Missing command argument.");
			return;
		}

		if (!persistanceManager.isPresent(StorageLocation.POP3_USERS, FieldName.POP3_USER_NAME, argument)) {
			setLastCommand(persistanceManager, clientId, POP3Command.USER, POP3StatusIndicator.ERR);
			communicationHandler.sendResponse(writer, POP3StatusIndicator.ERR.toString(), "There is no such user.");
			return;
		}

		setLastCommandWithOneArgument(persistanceManager, clientId, POP3Command.USER, POP3StatusIndicator.OK, argument);
		communicationHandler.sendResponse(writer, POP3StatusIndicator.OK.toString(), "User " + argument + " found.");
	}

	private void PASSCommand(CommunicationHandler communicationHandler, BufferedOutputStream writer, String argument, PersistanceManager persistanceManager, String clientId) {

		POP3SessionStatus status = getStatus(persistanceManager, clientId);

		// Check the status of the POP3 session: PASS is available only in AUTH status
		if (!status.equals(POP3SessionStatus.AUTHORIZATION)) {
			setLastCommand(persistanceManager, clientId, POP3Command.PASS, POP3StatusIndicator.ERR);
			communicationHandler.sendResponse(writer, POP3StatusIndicator.ERR.toString(), "This command is available only in AUTHORIZATION status.");
			return;
		}

		// Check the previous command: PASS is available only after a successful USER command
		POP3Command previousCommand = getPreviousCommand(persistanceManager, clientId);
		POP3StatusIndicator previousCommandResult = getPreviousCommandResult(persistanceManager, clientId);

		if (!previousCommand.equals(POP3Command.USER) && !previousCommandResult.equals(POP3StatusIndicator.OK)) {
			setLastCommand(persistanceManager, clientId, POP3Command.PASS, POP3StatusIndicator.ERR);
			communicationHandler.sendResponse(writer, POP3StatusIndicator.ERR.toString(), "Invalid sequence. USER not selected. You have to start over.");
			return;
		}

		// Check the argument
		if (argument.isEmpty()) {
			setLastCommand(persistanceManager, clientId, POP3Command.PASS, POP3StatusIndicator.ERR);
			communicationHandler.sendResponse(writer, POP3StatusIndicator.ERR.toString(), "Missing command argument");
			return;
		}

		// Get the password for the current user name
		String userName = getPreviousCommandFirstArgument(persistanceManager, clientId);
		String password = persistanceManager.read(StorageLocation.POP3_PASSWORDS, FieldName.POP3_USER_PASSWORD, userName);

		if (!password.equals(argument)) {// Wrong password
			setLastCommand(persistanceManager, clientId, POP3Command.PASS, POP3StatusIndicator.ERR);
			communicationHandler.sendResponse(writer, POP3StatusIndicator.ERR.toString(), "Wrong password for user " + userName);
			return;
		}

		List<FieldName> usernameField = FieldName.getPOP3UsernameTableFieldNames();

		persistanceManager.update(StorageLocation.POP3_SESSIONS, clientId, usernameField, userName);

		setLastCommandWithOneArgument(persistanceManager, clientId, POP3Command.PASS, POP3StatusIndicator.OK, argument);

		setStatus(persistanceManager, POP3SessionStatus.TRANSACTION, clientId);

		// Get the UIDs from the POP3_UIDS location (to complete session initialization)
		// Subsequent calls to getMessageUIDs will have the POP3_SESSIONS location to avoid another DB query
		List<String> uidsAndDimensions = persistanceManager.getMessageUIDs(StorageLocation.POP3_UIDS, clientId, userName, false);
		
		AbstractRequestHandler.log.info("uidsAndDimensions List length "+ uidsAndDimensions.size());
		
		List<String> uids = new ArrayList<String>(uidsAndDimensions.size() / 2);
		List<String> dimensions = new ArrayList<String>(uidsAndDimensions.size() / 2);
		
		for(int i = 0; i < uidsAndDimensions.size(); i = i + 2) {
			uids.add(uidsAndDimensions.get(i));
			dimensions.add(uidsAndDimensions.get(i + 1));
		}
		
		String[] uidsArray = uids.toArray(new String[uids.size()]);
		String[] dimensionArray = dimensions.toArray(new String[dimensions.size()]);
		
		persistanceManager.update(StorageLocation.POP3_SESSIONS, clientId, FieldName.getPOP3UIDS(), uidsArray);
		persistanceManager.update(StorageLocation.POP3_SESSIONS, clientId, FieldName.getPOP3Dimensions(), dimensionArray);
		
		String messagesCons = persistanceManager.read(StorageLocation.POP3_USERS, FieldName.USER_MESSAGES_NUMBER, userName);
		String totalDim = persistanceManager.read(StorageLocation.POP3_USERS, FieldName.MESSAGES_TOTAL_DIMENSION, userName);

		int messages = Integer.parseInt(messagesCons);
		int totalDimension = Integer.parseInt(totalDim);

		persistanceManager.update(StorageLocation.POP3_SESSIONS, clientId, FieldName.getMaildropStatData(), Integer.toString(messages), Integer.toString(totalDimension));
		
		communicationHandler.sendResponse(writer, POP3StatusIndicator.OK.toString(), "Authentication successful.");
	}

	private void QUITCommand(CommunicationHandler communicationHandler, BufferedOutputStream writer, PersistanceManager persistanceManager, String clientId) {

		POP3SessionStatus status = getStatus(persistanceManager, clientId);

		if (status.equals(POP3SessionStatus.GREETINGS)) {
			communicationHandler.sendResponse(writer, POP3StatusIndicator.ERR.toString(), "POP3 server is in GREETINGS status");
			return;
		} else {
			setStatus(persistanceManager, POP3SessionStatus.UPDATE, clientId);
			communicationHandler.sendResponse(writer, POP3StatusIndicator.OK.toString(), "Closing session");
		}
		
		String userName = getClientUserName(persistanceManager, clientId);
		
		// Get uids of the messages marked for deletion
		List<String> uids = persistanceManager.getMessageUIDs(StorageLocation.POP3_SESSIONS, clientId, userName, true);
		
		for(int i = 0; i < uids.size(); i++) {
			// Delete messages marked for deletion
			persistanceManager.delete(StorageLocation.POP3_MAILDROPS, uids.get(i));			
			persistanceManager.delete(StorageLocation.POP3_UIDS, userName, uids.get(i));
		}
		
		// Update maildrop data
		String deletedMessagesCount = "-" + persistanceManager.read(StorageLocation.POP3_SESSIONS, FieldName.POP3_DELETED_MESSAGES_NUMBER, clientId);
		String deletedMessagesSize = "-" + persistanceManager.read(StorageLocation.POP3_SESSIONS, FieldName.POP3_DELETED_MESSAGES_SIZE, clientId);
		persistanceManager.update(StorageLocation.POP3_USERS, userName, FieldName.getMaildropStatData(), deletedMessagesCount, deletedMessagesSize);

		// Delete session information
		persistanceManager.delete(StorageLocation.POP3_SESSIONS, clientId);
	}

	private void CAPACommand(CommunicationHandler communicationHandler, BufferedOutputStream writer, PersistanceManager persistanceManager, String clientId) {

		POP3SessionStatus status = getStatus(persistanceManager, clientId);

		if (status.equals(POP3SessionStatus.GREETINGS)) {
			communicationHandler.sendResponse(writer, POP3StatusIndicator.ERR.toString(), "This command is not available in GREETINGS status");
			return;
		} else {
			communicationHandler.sendResponse(writer, POP3StatusIndicator.OK.toString(), "Capabilities follow.");
			List<String> capabilities = POP3Command.getPOP3CapaCommands();
			communicationHandler.sendListAsMultiLineResponse(writer, capabilities);
		}
	}

	private void STATCommand(CommunicationHandler communicationHandler, BufferedOutputStream writer, PersistanceManager persistanceManager, String clientId) {

		POP3SessionStatus status = getStatus(persistanceManager, clientId);

		// Check the status of the POP3 session
		if (!status.equals(POP3SessionStatus.TRANSACTION)) {
			communicationHandler.sendResponse(writer, POP3StatusIndicator.ERR.toString(), "This command is available only in TRANSACTION status.");
			return;
		}

		int messages = 0;
		int totalDimension = 0;
		
		messages = Integer.parseInt(persistanceManager.read(StorageLocation.POP3_SESSIONS, FieldName.USER_MESSAGES_NUMBER, clientId));
		totalDimension = Integer.parseInt(persistanceManager.read(StorageLocation.POP3_SESSIONS, FieldName.MESSAGES_TOTAL_DIMENSION, clientId));

		communicationHandler.sendResponse(writer, POP3StatusIndicator.OK.toString(), messages + " " + totalDimension);
	}

	private void LISTCommand(CommunicationHandler communicationHandler, BufferedOutputStream writer, String argument, PersistanceManager persistanceManager, String clientId) {

		POP3SessionStatus status = getStatus(persistanceManager, clientId);

		// Check the status of the POP3 session
		if (!status.equals(POP3SessionStatus.TRANSACTION)) {
			communicationHandler.sendResponse(writer, POP3StatusIndicator.ERR.toString(), "This command is available only in TRANSACTION status.");
			return;
		}

		String userName = getClientUserName(persistanceManager, clientId);
		
		// Get UIDs for all the messages
		List<String> uids = persistanceManager.getMessageUIDs(StorageLocation.POP3_SESSIONS, clientId, userName, false);
		
		// No messages in the maildrop
		if (uids.size() == 0) {
			communicationHandler.sendResponse(writer, POP3StatusIndicator.OK.toString(), "");
			communicationHandler.sendBlankLineMultilineEnd(writer);
			return;
		}
		
		List<String> dimensions = persistanceManager.scanForMessageDimensions(StorageLocation.POP3_SESSIONS, clientId, userName, false);
		
		// Check the argument
		if (argument.isEmpty()) {
			
			// Send the first part of the response
			communicationHandler.sendResponse(writer, POP3StatusIndicator.OK.toString(), uids.size() + " messages");
			
			for (int i = 0; i < dimensions.size(); i++) {
				if(i <= dimensions.size() - 2) {
					communicationHandler.sendLine(writer, (i + 1) + " " + dimensions.get(i), true, false);
				}else {
					communicationHandler.sendLine(writer, (i + 1) + " " + dimensions.get(i), true, true);
				}
			}
			
			// Update DELE command count
			persistanceManager.update(StorageLocation.POP3_SESSIONS, clientId, FieldName.getPOP3DelesFieldOnly(), Integer.toString(0));
		} else {

			int messageNumber = Integer.parseInt(argument) - 1;
			
			// Check how many DELE commands were issued in this session
			int deleCommands = checkDELECommand(persistanceManager, clientId);
			
			// Update message number
			messageNumber -= deleCommands;
			
			// Check if such message exists
			if (messageNumber >= uids.size() || messageNumber < 0) {
				communicationHandler.sendResponse(writer, POP3StatusIndicator.ERR.toString(), "no such message");
				return;
			}

			// Get the dimension of the message
			int dimension = Integer.parseInt(dimensions.get(messageNumber));

			// Send the response
			communicationHandler.sendResponse(writer, POP3StatusIndicator.OK.toString(), argument + " " + dimension);
		}
	}

	private void RETRCommand(CommunicationHandler communicationHandler, BufferedOutputStream writer, String argument, PersistanceManager persistanceManager, String clientId) {

		POP3SessionStatus status = getStatus(persistanceManager, clientId);

		// Check the status of the POP3 session
		if (!status.equals(POP3SessionStatus.TRANSACTION)) {
			communicationHandler.sendResponse(writer, POP3StatusIndicator.ERR.toString(), "This command is available only in TRANSACTION status.");
			return;
		}

		// Check the argument
		if (argument.isEmpty() || Integer.parseInt(argument) < 0) {
			communicationHandler.sendResponse(writer, POP3StatusIndicator.ERR.toString(), "Missing command argument");
			return;
		}

		String userName = getClientUserName(persistanceManager, clientId);
		
		// Get UIDs for all the messages
		List<String> uids = persistanceManager.getMessageUIDs(StorageLocation.POP3_SESSIONS, clientId, userName, false);

		int messageNumber = Integer.parseInt(argument) - 1;

		// Check how many DELE commands were issued in this session
		int deleCommands = checkDELECommand(persistanceManager, clientId);

		// Update message number
		messageNumber -= deleCommands;
		
		// Check if the specified message exists
		if (messageNumber >= uids.size()) {
			communicationHandler.sendResponse(writer, POP3StatusIndicator.ERR.toString(), "no such message");
			return;
		}

		// Get message data
		String messageHeader = persistanceManager.read(StorageLocation.POP3_MAILDROPS, FieldName.POP3_MESSAGE_HEADER, uids.get(messageNumber));
		String message = persistanceManager.read(StorageLocation.POP3_MAILDROPS, FieldName.POP3_MESSAGE_DATA, uids.get(messageNumber));

		// Send the message in the multiline response
		communicationHandler.sendResponse(writer, POP3StatusIndicator.OK.toString(), "message follows");

		String toSend = messageHeader + SpecialCharactersSequence.LINE_END + SpecialCharactersSequence.LINE_END + message;

		communicationHandler.sendString(writer, toSend);
	}

	private void DELECommand(CommunicationHandler communicationHandler, BufferedOutputStream writer, String argument, PersistanceManager persistanceManager, String clientId) {

		POP3SessionStatus status = getStatus(persistanceManager, clientId);

		// Check the status of the POP3 session
		if (!status.equals(POP3SessionStatus.TRANSACTION)) {
			communicationHandler.sendResponse(writer, POP3StatusIndicator.ERR.toString(), "This command is available only in TRANSACTION status.");
			return;
		}

		// Check the argument
		if (argument.isEmpty() || Integer.parseInt(argument) < 0) {
			communicationHandler.sendResponse(writer, POP3StatusIndicator.ERR.toString(), "Missing command argument.");
			return;
		}

		String userName = getClientUserName(persistanceManager, clientId);
		
		// Get UIDs for all the messages
		List<String> uids = persistanceManager.getMessageUIDs(StorageLocation.POP3_SESSIONS, clientId, userName, false);

		// Get message size list
		List<String> dimensionsList = persistanceManager.scanForMessageDimensions(StorageLocation.POP3_SESSIONS, clientId, userName, false);
		
		int messageNumber = Integer.parseInt(argument) - 1;

		// Check how many DELE commands were issued in this session
		int deleCommands = checkDELECommand(persistanceManager, clientId);

		// Update message number
		messageNumber -= deleCommands;
		
		// Check if the specified message exists
		if (messageNumber >= uids.size()) {
			communicationHandler.sendResponse(writer, POP3StatusIndicator.ERR.toString(), "no such message");
			return;
		}

		List<FieldName> messageToDeleteField = FieldName.getMessageToDeleteTableFieldOnly();

		// Mark the message for deletion
		persistanceManager.update(StorageLocation.POP3_SESSIONS, clientId, messageToDeleteField, uids.get(messageNumber), POP3MessageDeletion.YES.toString());
		
		// Update DELE command count
		persistanceManager.update(StorageLocation.POP3_SESSIONS, clientId, FieldName.getPOP3DelesFieldOnly(), Integer.toString(deleCommands + 1));
		
		// Update maildrop data
		int messageCountNumber = Integer.parseInt(persistanceManager.read(StorageLocation.POP3_SESSIONS, FieldName.USER_MESSAGES_NUMBER, clientId));
		int messageTotalSizeNumber = Integer.parseInt(persistanceManager.read(StorageLocation.POP3_SESSIONS, FieldName.MESSAGES_TOTAL_DIMENSION, clientId));
		
		// Update maildrop data
		messageCountNumber--;		
		int messageSizeNumber = Integer.parseInt(dimensionsList.get(messageNumber));
		messageTotalSizeNumber -= messageSizeNumber;
		
		String messagesCount = Integer.toString(messageCountNumber);
		String messageTotalSize = Integer.toString(messageTotalSizeNumber);
		
		persistanceManager.update(StorageLocation.POP3_SESSIONS, clientId, FieldName.getMaildropStatData(), messagesCount, messageTotalSize);
		
		// Update deleted messages stats
		int deletedMessagesCount = Integer.parseInt(persistanceManager.read(StorageLocation.POP3_SESSIONS, FieldName.POP3_DELETED_MESSAGES_NUMBER, clientId));
		deletedMessagesCount++;
		
		int deletedMessagesSize = Integer.parseInt(persistanceManager.read(StorageLocation.POP3_SESSIONS, FieldName.POP3_DELETED_MESSAGES_SIZE, clientId));
		deletedMessagesSize += messageSizeNumber;
		
		persistanceManager.update(StorageLocation.POP3_SESSIONS, clientId, FieldName.getDeletedMessagesStats(), Integer.toString(deletedMessagesCount), Integer.toString(deletedMessagesSize));
		
		// Send positive response
		communicationHandler.sendResponse(writer, POP3StatusIndicator.OK.toString(), "Message marked for deletion.");
	}

	private void TOPCommand(CommunicationHandler communicationHandler, BufferedOutputStream writer, String argument, String secondArgument, PersistanceManager persistanceManager, String clientId) {

		POP3SessionStatus status = getStatus(persistanceManager, clientId);

		// Check the status of the POP3 session
		if (!status.equals(POP3SessionStatus.TRANSACTION)) {
			communicationHandler.sendResponse(writer, POP3StatusIndicator.ERR.toString(), "This command is available only in TRANSACTION status.");
			return;
		}

		// Check the argument
		if (argument.isEmpty() || secondArgument.isEmpty() || Integer.parseInt(secondArgument) < 0) {
			communicationHandler.sendResponse(writer, POP3StatusIndicator.ERR.toString(), "Missing command argument.");
			return;
		}

		String userName = getClientUserName(persistanceManager, clientId);
		
		// Get UIDs for all the messages
		List<String> uids = persistanceManager.getMessageUIDs(StorageLocation.POP3_SESSIONS, clientId, userName, false);

		int messageNumber = Integer.parseInt(argument) - 1;

		// Check how many DELE commands were issued in this session
		int deleCommands = checkDELECommand(persistanceManager, clientId);

		// Update message number
		messageNumber -= deleCommands;
		
		// Check if the specified message exists
		if (messageNumber >= uids.size()) {
			communicationHandler.sendResponse(writer, POP3StatusIndicator.ERR.toString(), "no such message");
			return;
		}

		// Get message data
		String messageHeader = persistanceManager.read(StorageLocation.POP3_MAILDROPS, FieldName.POP3_MESSAGE_HEADER, uids.get(messageNumber));
		String message = persistanceManager.read(StorageLocation.POP3_MAILDROPS, FieldName.POP3_MESSAGE_DATA, uids.get(messageNumber));

		String toSend = messageHeader + SpecialCharactersSequence.LINE_END + message;

		int lines = Integer.parseInt(secondArgument);

		// Send the message in the multiline response
		communicationHandler.sendResponse(writer, POP3StatusIndicator.OK.toString(), "top of message follows");

		communicationHandler.sendStringWithLinesLimit(writer, toSend, lines);
	}

	private void NOOPCommand(CommunicationHandler communicationHandler, BufferedOutputStream writer, PersistanceManager persistanceManager, String clientId) {

		POP3SessionStatus status = getStatus(persistanceManager, clientId);

		// Check the status of the POP3 session
		if (!status.equals(POP3SessionStatus.TRANSACTION)) {
			communicationHandler.sendResponse(writer, POP3StatusIndicator.ERR.toString(), "This command is available only in TRANSACTION status.");
			return;
		}

		// setLastCommand(persistanceManager, clientId, POP3Command.NOOP,
		// POP3StatusIndicator.OK);
		communicationHandler.sendResponse(writer, POP3StatusIndicator.OK.toString(), "NOOP command received.");
	}

	private void RSETCommand(CommunicationHandler communicationHandler, BufferedOutputStream writer, PersistanceManager persistanceManager, String clientId) {

		POP3SessionStatus status = getStatus(persistanceManager, clientId);

		// Check the status of the POP3 session
		if (!status.equals(POP3SessionStatus.TRANSACTION)) {
			communicationHandler.sendResponse(writer, POP3StatusIndicator.ERR.toString(), "This command is available only in TRANSACTION status.");
			return;
		}

		String userName = getClientUserName(persistanceManager, clientId);
		
		// Get UIDs for all the messages
		List<String> uids = persistanceManager.getMessageUIDs(StorageLocation.POP3_SESSIONS, clientId, userName, true);

		List<FieldName> messageToDeleteField = FieldName.getMessageToDeleteTableFieldOnly();

		// Get info to update maildrop data
		int messageCountNumber = Integer.parseInt(persistanceManager.read(StorageLocation.POP3_USERS, FieldName.USER_MESSAGES_NUMBER, userName));
		int messageTotalSizeNumber = Integer.parseInt(persistanceManager.read(StorageLocation.POP3_USERS, FieldName.MESSAGES_TOTAL_DIMENSION, userName));
		
		// Unmarks every messaged marked for deletion
		for (int i = 0; i < uids.size(); i++) {
			persistanceManager.update(StorageLocation.POP3_SESSIONS, clientId, messageToDeleteField, uids.get(i), POP3MessageDeletion.NO.toString());
		}
		
		// Get the total messages dimension
		List<String> dimensions = persistanceManager.scanForMessageDimensions(StorageLocation.POP3_SESSIONS, clientId, userName, true);
		
		for(int i = 0; i < dimensions.size(); i++){
			messageTotalSizeNumber += Integer.parseInt(dimensions.get(i));
		}
		
		// Update maildrop stat data
		String messagesCount = Integer.toString(messageCountNumber + uids.size());
		String messageTotalSize = Integer.toString(messageTotalSizeNumber);
		
		persistanceManager.update(StorageLocation.POP3_USERS, userName, FieldName.getMaildropStatData(), messagesCount, messageTotalSize);
		persistanceManager.update(StorageLocation.POP3_SESSIONS, clientId, FieldName.getDeletedMessagesStats(), Integer.toString(0), Integer.toString(0));
		
		// Update DELE command count
		persistanceManager.update(StorageLocation.POP3_SESSIONS, clientId, FieldName.getPOP3DelesFieldOnly(), Integer.toString(0));

		communicationHandler.sendResponse(writer, POP3StatusIndicator.OK.toString(), "Maildrop reset completed.");
	}

	private void UIDLCommand(CommunicationHandler communicationHandler, BufferedOutputStream writer, String argument, PersistanceManager persistanceManager, String clientId) {
		POP3SessionStatus status = getStatus(persistanceManager, clientId);

		// Check the status of the POP3 session
		if (!status.equals(POP3SessionStatus.TRANSACTION)) {
			communicationHandler.sendResponse(writer, POP3StatusIndicator.ERR.toString(), "This command is available only in TRANSACTION status.");
			return;
		}

		// Check the argument
		if (argument.isEmpty()) {

			String userName = getClientUserName(persistanceManager, clientId);
			
			// Get information about all the messages in the maildrop
			List<String> uids = persistanceManager.getMessageUIDs(StorageLocation.POP3_SESSIONS, clientId, userName, false);

			communicationHandler.sendResponse(writer, POP3StatusIndicator.OK.toString(), "");

			// Update DELE command count
			persistanceManager.update(StorageLocation.POP3_SESSIONS, clientId, FieldName.getPOP3DelesFieldOnly(), Integer.toString(0));
			
			// Send response
			if (uids.size() > 0) {

				// Add message number
				for (int i = 0; i < uids.size(); i++) {
					uids.set(i, (i + 1) + " " + uids.get(i));
				}

				communicationHandler.sendListAsMultiLineResponse(writer, uids);

			} else {// No messages in the maildrop
				communicationHandler.sendBlankLineMultilineEnd(writer);
				return;
			}

		} else {

			String userName = getClientUserName(persistanceManager, clientId);
			
			List<String> uids = persistanceManager.getMessageUIDs(StorageLocation.POP3_SESSIONS, clientId, userName, false);

			int messageNumber = Integer.parseInt(argument) - 1;

			// Check how many DELE commands were issued in this session
			int deleCommands = checkDELECommand(persistanceManager, clientId);

			// Update message number
			messageNumber -= deleCommands;
			
			// Check if such message exists
			if (messageNumber >= uids.size() || messageNumber < 0) {
				communicationHandler.sendResponse(writer, POP3StatusIndicator.ERR.toString(), "no such message");
				return;
			}

			// Get the UID of the message
			String messageUid = uids.get(messageNumber);

			String response = (messageNumber + 1) + " " + messageUid;

			// Send the response
			communicationHandler.sendResponse(writer, POP3StatusIndicator.OK.toString(), argument + " " + response);
		}
	}

	private void unsupportedCommand(CommunicationHandler communicationHandler, PersistanceManager persistanceManager, String clientId, BufferedOutputStream writer) {
		communicationHandler.sendResponse(writer, POP3StatusIndicator.ERR.toString(), "Command is not supported");
	}

	@Override
	public void sendGreetings(CommunicationHandler communicationHandler, BufferedOutputStream writer, PersistanceManager persistanceManager, String clientId) {

		// Check if there is already a session record (dirty session)
		if (persistanceManager.isPresent(StorageLocation.POP3_SESSIONS, FieldName.POP3_SESSION_ID, clientId)) {
			
			// Clean the dirty session
			persistanceManager.delete(StorageLocation.POP3_SESSIONS, clientId);
		}
		
		// Create a new session record
		// Set the status to AUTH directly to avoid another query
		persistanceManager.create(StorageLocation.POP3_SESSIONS, FieldName.getPOP3StatusTableFieldNames(), clientId, POP3SessionStatus.AUTHORIZATION.toString(), POP3Command.EMPTY.toString(), POP3StatusIndicator.UNKNOWN.toString(), POP3Command.EMPTY.toString(), POP3Command.EMPTY.toString(), POP3Command.EMPTY.toString(), Integer.toString(0));
		
		// Send response
		communicationHandler.sendResponse(writer, POP3StatusIndicator.OK.toString(), "POP3 server ready to roll!");
	}

	private POP3Command getPreviousCommand(PersistanceManager persistanceManager, String clientId) {
		String result = persistanceManager.read(StorageLocation.POP3_SESSIONS, FieldName.POP3_LAST_COMMAND, clientId);

		if (result.isEmpty()) {
			return POP3Command.EMPTY;
		} else {
			return POP3Command.parseCommand(result);
		}
	}

	private String getPreviousCommandFirstArgument(PersistanceManager persistanceManager, String clientId) {
		return persistanceManager.read(StorageLocation.POP3_SESSIONS, FieldName.POP3_LAST_COMMAND_FIRST_ARGUMENT, clientId);
	}

	private POP3StatusIndicator getPreviousCommandResult(PersistanceManager persistanceManager, String clientId) {
		String result = persistanceManager.read(StorageLocation.POP3_SESSIONS, FieldName.POP3_LAST_COMMAND_RESULT, clientId);

		if (result.isEmpty()) {
			return POP3StatusIndicator.UNKNOWN;
		} else {
			return POP3StatusIndicator.parseStatusIndicator(result);
		}
	}

	private void setLastCommand(PersistanceManager persistanceManager, String clientId, POP3Command pop3Command, POP3StatusIndicator pop3StatusIndicator) {
		List<FieldName> commandFields = FieldName.getPOP3StatusCommandTableFieldNames();

		persistanceManager.update(StorageLocation.POP3_SESSIONS, clientId, commandFields, pop3Command.toString(), pop3StatusIndicator.toString(), POP3Command.EMPTY.toString(),
				POP3Command.EMPTY.toString());
	}

	private void setLastCommandWithOneArgument(PersistanceManager persistanceManager, String clientId, POP3Command pop3Command, POP3StatusIndicator pop3StatusIndicator, String argument) {

		List<FieldName> commandFields = FieldName.getPOP3StatusCommandTableFieldNames();

		persistanceManager.update(StorageLocation.POP3_SESSIONS, clientId, commandFields, pop3Command.toString(), pop3StatusIndicator.toString(), argument, POP3Command.EMPTY.toString());
	}

	private POP3SessionStatus getStatus(PersistanceManager persistanceManager, String clientId) {
		return POP3SessionStatus.parseStatus(persistanceManager.read(StorageLocation.POP3_SESSIONS, FieldName.POP3_SESSION_STATUS, clientId));
	}

	private void setStatus(PersistanceManager persistanceManager, POP3SessionStatus status, String clientId) {

		List<FieldName> statusFields = FieldName.getPOP3StatusTableFieldOnly();

		persistanceManager.update(StorageLocation.POP3_SESSIONS, clientId, statusFields, status.toString());
	}

	@Override
	public void clearStatus(PersistanceManager persistanceManager, String clientId) {
		persistanceManager.delete(StorageLocation.POP3_SESSIONS, clientId);		
	}
	
	private int checkDELECommand(PersistanceManager persistanceManager, String clientId) {
		
		// Check how many DELE commands were issued in this session
		return Integer.parseInt(persistanceManager.read(StorageLocation.POP3_SESSIONS, FieldName.POP3_HOW_MANY_DELES, clientId));

	}
	
	private String getClientUserName(PersistanceManager persistanceManage, String clientId) {
		return persistanceManage.read(StorageLocation.POP3_SESSIONS, FieldName.POP3_SESSION_USER_NAME, clientId);
	}
	
	public void sendAbnormalTerminationResponse(CommunicationHandler communicationHandler, BufferedOutputStream writer) {
		// POP3 does not send anything
		System.out.println("POP3 Abnormal Termination response is EMPTY.");
	}
}
