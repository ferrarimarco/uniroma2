package controller.smtp;

import java.io.BufferedOutputStream;
import java.util.List;

import model.Message;
import controller.AbstractCommandHandler;
import controller.CommunicationHandler;
import controller.SpecialCharactersSequence;
import controller.persistance.FieldName;
import controller.persistance.PersistanceManager;
import controller.persistance.StorageLocation;
import controller.pop3.POP3MessageDeletion;

public class SMTPCommandHandler extends AbstractCommandHandler {

	// TODO: study this value
	private static final int MAX_RECIPIENTS = 10;

	// TODO: check when the client does not send the QUIT command if it receives errors.
	
	@Override
	public void handleCommand(CommunicationHandler communicationHandler, BufferedOutputStream writer, String line, String command, String argument, String secondArgument,
			PersistanceManager persistanceManager, String clientId) {

		SMTPSessionStatus currentStatus = getStatus(persistanceManager, clientId);

		if (!currentStatus.equals(SMTPSessionStatus.TRANSACTION_DATA)) {

			SMTPCommand smtpCommand = SMTPCommand.parseCommand(command);

			if (smtpCommand.equals(SMTPCommand.EHLO)) {
				EHLOCommand(communicationHandler, writer, persistanceManager, clientId, argument);
			} else if (smtpCommand.equals(SMTPCommand.HELO)) {
				HELOCommand(communicationHandler, writer, persistanceManager, clientId, argument);
			} else if (smtpCommand.equals(SMTPCommand.MAIL)) {
				MAILCommand(communicationHandler, writer, persistanceManager, clientId, argument);
			} else if (smtpCommand.equals(SMTPCommand.RCPT)) {
				RCPTCommand(communicationHandler, writer, persistanceManager, clientId, argument);
			} else if (smtpCommand.equals(SMTPCommand.DATA)) {
				DATACommand(communicationHandler, writer, persistanceManager, clientId);
			} else if (smtpCommand.equals(SMTPCommand.QUIT)) {
				QUITCommand(communicationHandler, writer, persistanceManager, clientId);
			} else if (smtpCommand.equals(SMTPCommand.RSET)) {
				RSETCommand(communicationHandler, writer, persistanceManager, clientId);
			} else if (smtpCommand.equals(SMTPCommand.NOOP)) {
				NOOPCommand(communicationHandler, writer, persistanceManager, clientId);
			} else if (smtpCommand.equals(SMTPCommand.UNSUPPORTED)) {
				unsupportedCommand(communicationHandler, persistanceManager, clientId, writer);
			}
		} else {
			processMessageData(communicationHandler, writer, persistanceManager, clientId, line);
		}
	}

	private void EHLOCommand(CommunicationHandler communicationHandler, BufferedOutputStream writer, PersistanceManager persistanceManager, String clientId, String argument) {

		setStatus(persistanceManager, SMTPSessionStatus.GREETINGS, clientId);		
		communicationHandler.sendResponse(writer, SMTPCode.OK.toString(), "Welcome!");
	}

	private void HELOCommand(CommunicationHandler communicationHandler, BufferedOutputStream writer, PersistanceManager persistanceManager, String clientId, String argument) {

		EHLOCommand(communicationHandler, writer, persistanceManager, clientId, argument);
	}

	private void MAILCommand(CommunicationHandler communicationHandler, BufferedOutputStream writer, PersistanceManager persistanceManager, String clientId, String argument) {

		// Check SMTP session status
		SMTPSessionStatus currentStatus = getStatus(persistanceManager, clientId);

		if (!currentStatus.equals(SMTPSessionStatus.GREETINGS)) {
			communicationHandler.sendResponse(writer, SMTPCode.BAD_SEQUENCE.toString(), "Bad command sequence.");
			return;
		}

		setStatus(persistanceManager, SMTPSessionStatus.TRANSACTION, clientId);

		clearTempTable(persistanceManager, clientId);

		String address = getAddressFromArgument(argument);

		if (!isValidAddress(address, persistanceManager)) {
			communicationHandler.sendResponse(writer, SMTPCode.SYNTAX_ERROR.toString(), "Address is not valid.");
			return;
		}

		// Initialize data
		persistanceManager.create(StorageLocation.SMTP_TEMP_MESSAGE_STORE, FieldName.getSMTPTempTableFromFieldOnly(), clientId, address);
		
		communicationHandler.sendResponse(writer, SMTPCode.OK.toString(), "OK");

	}

	private void RCPTCommand(CommunicationHandler communicationHandler, BufferedOutputStream writer, PersistanceManager persistanceManager, String clientId, String argument) {

		// Check SMTP session status
		SMTPSessionStatus currentStatus = getStatus(persistanceManager, clientId);

		if (!currentStatus.equals(SMTPSessionStatus.TRANSACTION)) {
			communicationHandler.sendResponse(writer, SMTPCode.BAD_SEQUENCE.toString(), "Bad command sequence.");
			return;
		}

		// Check if the address can be added
		int recipientNumber = getRecipientNumber(persistanceManager, clientId);

		if (recipientNumber >= MAX_RECIPIENTS) {
			communicationHandler.sendResponse(writer, SMTPCode.EXCEEDED_STORAGE_ALLOCATION.toString(), "You cannot add any more recipients.");
			return;
		}

		String address = getAddressFromArgument(argument);

		// Check the address
		if (!isValidAddress(address, persistanceManager)) {
			communicationHandler.sendResponse(writer, SMTPCode.SYNTAX_ERROR.toString(), "Address is not valid.");
			return;
		}

		persistanceManager.addToSet(StorageLocation.SMTP_TEMP_MESSAGE_STORE, clientId, FieldName.SMTP_TEMP_TO_ADDRESSES, address);

		communicationHandler.sendResponse(writer, SMTPCode.OK.toString(), "OK");
	}

	private void DATACommand(CommunicationHandler communicationHandler, BufferedOutputStream writer, PersistanceManager persistanceManager, String clientId) {

		// Check SMTP session status
		SMTPSessionStatus currentStatus = getStatus(persistanceManager, clientId);

		if (!currentStatus.equals(SMTPSessionStatus.TRANSACTION)) {
			communicationHandler.sendResponse(writer, SMTPCode.BAD_SEQUENCE.toString(), "Bad command sequence.");
			return;
		}

		// Set status to receiving data
		if (currentStatus.equals(SMTPSessionStatus.TRANSACTION)) {
			setStatus(persistanceManager, SMTPSessionStatus.TRANSACTION_DATA, clientId);
		}

		communicationHandler.sendResponse(writer, SMTPCode.INTERMEDIATE_REPLY.toString(), "Start mail input; end with [CRLF].[CRLF]");

	}

	private void processMessageData(CommunicationHandler communicationHandler, BufferedOutputStream writer, PersistanceManager persistanceManager, String clientId, String data) {

		// Get previous data
		String message = persistanceManager.read(StorageLocation.SMTP_TEMP_MESSAGE_STORE, FieldName.SMTP_TEMP_RAW_DATA, clientId);
		
		// Add the new line to the message body
		message += data + SpecialCharactersSequence.LINE_END.toString();

		// Search for data termination sequence
		if (message.indexOf(SpecialCharactersSequence.SMTP_DATA_END.toString()) == -1) {

			// Update message body
			persistanceManager.update(StorageLocation.SMTP_TEMP_MESSAGE_STORE, clientId, FieldName.getSMTPTempTableDataFieldOnly(), message);

		} else {// End of message data

			SMTPCode result = processMessage(persistanceManager, clientId);

			communicationHandler.sendResponse(writer, result.toString(), "OK");

			setStatus(persistanceManager, SMTPSessionStatus.GREETINGS, clientId);
		}
	}

	private SMTPCode processMessage(PersistanceManager persistanceManager, String clientId) {
		
		// To hold local user names
		List<String> users = persistanceManager.getSet(StorageLocation.SMTP_TEMP_MESSAGE_STORE, FieldName.SMTP_TEMP_TO_USERS, clientId);
		
		// To hold external addresses
		List<String> toAddresses = persistanceManager.getSet(StorageLocation.SMTP_TEMP_MESSAGE_STORE, FieldName.SMTP_TEMP_TO_ADDRESSES, clientId);
		
		String messageId = persistanceManager.read(StorageLocation.SMTP_TEMP_MESSAGE_STORE, FieldName.SMTP_TEMP_ID, clientId);
		String header = persistanceManager.read(StorageLocation.SMTP_TEMP_MESSAGE_STORE, FieldName.SMTP_TEMP_HEADER, clientId);
		String body = persistanceManager.read(StorageLocation.SMTP_TEMP_MESSAGE_STORE, FieldName.SMTP_TEMP_BODY, clientId);
		String messageSize = persistanceManager.read(StorageLocation.SMTP_TEMP_MESSAGE_STORE, FieldName.SMTP_TEMP_MESSAGE_SIZE, clientId);
		
		// Now process the message for external users

		SMTPCode result = SMTPCode.OK;
		
		if(toAddresses.size() > 0){
			
			Message message = new Message();
			
			String rawData = persistanceManager.read(StorageLocation.SMTP_TEMP_MESSAGE_STORE, FieldName.SMTP_TEMP_RAW_DATA, clientId);
			String fromAddress = persistanceManager.read(StorageLocation.SMTP_TEMP_MESSAGE_STORE, FieldName.SMTP_TEMP_FROM, clientId);
			
			message.setRawData(rawData);
			message.setHeader(header);
			message.setBody(body);
			message.setUid(messageId);
			message.setMessageSize(messageSize);
			message.setFromAddress(fromAddress);
			message.setToAddresses(toAddresses);
			
			result = processExternalMessage(message);
		}
		
		// Process the message for our users
		for (int i = 0; i < users.size(); i++) {

			// Update messageId to handle the same message written to multiple senders
			String user = users.get(i);
			String newMessageId = messageId + "_" + user;
			header = header.replace(messageId, newMessageId);
			
			persistanceManager.create(StorageLocation.POP3_MAILDROPS, FieldName.getPOP3MessagesTableFieldNames(), newMessageId, user, POP3MessageDeletion.NO.toString(), messageSize, header, body);
		}
		
		// Delete the temp message
		persistanceManager.delete(StorageLocation.SMTP_TEMP_MESSAGE_STORE, clientId);
		
		return result;
	}
	
	private SMTPCode processExternalMessage(Message message){
		SMTPMessageSender smtpMessageSender = new SMTPMessageSender();
		
		String result = smtpMessageSender.sendMessage(message);
		
		return SMTPCode.parseCode(result);
	}

	private void QUITCommand(CommunicationHandler communicationHandler, BufferedOutputStream writer, PersistanceManager persistanceManager, String clientId) {
		
		clearStatus(persistanceManager, clientId);

		communicationHandler.sendResponse(writer, SMTPCode.QUIT_OK_RESPONSE.toString(), "Bye!");
	}

	private void NOOPCommand(CommunicationHandler communicationHandler, BufferedOutputStream writer, PersistanceManager persistanceManager, String clientId) {
		communicationHandler.sendResponse(writer, SMTPCode.GREETINGS.toString(), "OK");
	}

	private void RSETCommand(CommunicationHandler communicationHandler, BufferedOutputStream writer, PersistanceManager persistanceManager, String clientId) {
		clearTempTable(persistanceManager, clientId);

		communicationHandler.sendResponse(writer, SMTPCode.GREETINGS.toString(), "OK");
	}

	private void unsupportedCommand(CommunicationHandler communicationHandler, PersistanceManager persistanceManager, String clientId, BufferedOutputStream writer) {
		communicationHandler.sendResponse(writer, SMTPCode.UNSUPPORTED_COMMAND.toString(), "Command is not supported");
	}

	@Override
	public void sendGreetings(CommunicationHandler communicationHandler, BufferedOutputStream writer, PersistanceManager persistanceManager, String clientId) {

		// Store client ID in DB
		if (!persistanceManager.isPresent(StorageLocation.SMTP_SESSIONS, FieldName.SMTP_SESSION_ID, clientId)) {

			persistanceManager.create(StorageLocation.SMTP_SESSIONS, FieldName.getSMTPStatusTableFieldNames(), clientId, SMTPSessionStatus.GREETINGS.toString(), SMTPCommand.EMPTY.toString(),
					SMTPCode.EMPTY.toString(), SMTPCommand.EMPTY.toString(), SMTPCommand.EMPTY.toString());

			communicationHandler.sendResponse(writer, SMTPCode.GREETINGS.toString(), "SMTP server ready to roll!");
		}
	}

	private SMTPSessionStatus getStatus(PersistanceManager persistanceManager, String clientId) {
		return SMTPSessionStatus.parseStatus(persistanceManager.read(StorageLocation.SMTP_SESSIONS, FieldName.SMTP_SESSION_STATUS, clientId));
	}

	private void setStatus(PersistanceManager persistanceManager, SMTPSessionStatus status, String clientId) {

		List<FieldName> statusFields = FieldName.getSMTPStatusTableFieldOnly();

		persistanceManager.update(StorageLocation.SMTP_SESSIONS, clientId, statusFields, status.toString());
	}

	private void clearTempTable(PersistanceManager persistanceManager, String clientId) {
		persistanceManager.delete(StorageLocation.SMTP_TEMP_MESSAGE_STORE, clientId);
	}

	private String getAddressFromArgument(String argument) {

		int startIndex = argument.indexOf('<');
		int endIndex = argument.indexOf('>');

		return argument.substring(startIndex + 1, endIndex);
	}

	private boolean isValidAddress(String address, PersistanceManager persistanceManager) {

		int startIndex = 0;
		int endIndex = address.length();

		// Get the address only
		if (address.indexOf("<") != -1) {
			startIndex = address.indexOf("<") + 1;
			endIndex = address.indexOf(">");
		}

		address = address.substring(startIndex, endIndex);
		
		// Check email format
		if (!address.matches("[^@]+@([-\\p{Alnum}]+\\.)*\\p{Alnum}+")) {
			return false;
		}

		SMTPMessageSender smtpMessageSender = new SMTPMessageSender();
		return smtpMessageSender.verifyEmailAccountExistance(address, persistanceManager);
	}

	private int getRecipientNumber(PersistanceManager persistanceManager, String clientId) {

		List<String> recipients = persistanceManager.getSet(StorageLocation.SMTP_TEMP_MESSAGE_STORE, FieldName.SMTP_TEMP_TO_ADDRESSES, clientId);

		return recipients.size();
	}

	@Override
	public void clearStatus(PersistanceManager persistanceManager, String clientId) {
		
		clearTempTable(persistanceManager, clientId);

		persistanceManager.delete(StorageLocation.SMTP_SESSIONS, clientId);		
	}
}
