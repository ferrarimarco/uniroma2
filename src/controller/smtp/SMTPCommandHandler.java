package controller.smtp;

import java.io.BufferedOutputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.codec.binary.Hex;

import controller.AbstractCommandHandler;
import controller.CommunicationHandler;
import controller.persistance.FieldName;
import controller.persistance.PersistanceManager;
import controller.persistance.StorageLocation;
import controller.pop3.POP3MessageDeletion;

public class SMTPCommandHandler extends AbstractCommandHandler {
	
	// TODO: study this value
	private static final int MAX_RECIPIENTS = 10;
	
	private static final String dataTerminator = "\r\n.\r\n";
	
	@Override
	public void handleCommand(CommunicationHandler communicationHandler, BufferedOutputStream writer, String command, String argument, String secondArgument, PersistanceManager persistanceManager, String clientId){
		
		SMTPCommand smtpCommand = SMTPCommand.parseCommand(command);
		
		if(smtpCommand.equals(SMTPCommand.EHLO)){
			EHLOCommand(communicationHandler, writer, persistanceManager, clientId, argument);
		}else if(smtpCommand.equals(SMTPCommand.HELO)){
			HELOCommand(communicationHandler, writer, persistanceManager, clientId, argument);
		}else if(smtpCommand.equals(SMTPCommand.MAIL)){
			MAILCommand(communicationHandler, writer, persistanceManager, clientId, argument);
		}else if(smtpCommand.equals(SMTPCommand.RCPT)){
			RCPTCommand(communicationHandler, writer, persistanceManager, clientId, argument);
		}else if(smtpCommand.equals(SMTPCommand.DATA)){
			DATACommand(communicationHandler, writer, persistanceManager, clientId);
		}else if(smtpCommand.equals(SMTPCommand.QUIT)){
			QUITCommand(communicationHandler, writer, persistanceManager, clientId);
		}else if(smtpCommand.equals(SMTPCommand.RSET)){
			RSETCommand(communicationHandler, writer, persistanceManager, clientId);
		}else if(smtpCommand.equals(SMTPCommand.NOOP)){
			NOOPCommand(communicationHandler, writer, persistanceManager, clientId);
		}else if(smtpCommand.equals(SMTPCommand.UNSUPPORTED)){
			unsupportedCommand(communicationHandler, persistanceManager, clientId, writer);
		}
	}

	private void EHLOCommand(CommunicationHandler communicationHandler,	BufferedOutputStream writer, PersistanceManager persistanceManager, String clientId, String argument) {
		
		communicationHandler.sendResponse(writer, SMTPCode.OK.toString(), "Welcome!");
		
	}
	
	private void HELOCommand(CommunicationHandler communicationHandler,	BufferedOutputStream writer, PersistanceManager persistanceManager, String clientId, String argument) {
		
		EHLOCommand(communicationHandler, writer, persistanceManager, clientId, argument);
	}
	
	private void MAILCommand(CommunicationHandler communicationHandler,	BufferedOutputStream writer, PersistanceManager persistanceManager, String clientId, String argument) {
		
		// Check SMTP session status
		SMTPSessionStatus currentStatus = getStatus(persistanceManager, clientId);
		
		if(!currentStatus.equals(SMTPSessionStatus.GREETINGS)){
			communicationHandler.sendResponse(writer, SMTPCode.BAD_SEQUENCE.toString(), "Bad command sequence.");
			return;
		}
		
		setStatus(persistanceManager, SMTPSessionStatus.TRANSACTION, clientId);
		
		clearTempTable(persistanceManager, clientId);
		
		String address = getAddressFromArgument(argument);
		
		if(!isValidAddress(address, persistanceManager)){
			communicationHandler.sendResponse(writer, SMTPCode.SYNTAX_ERROR.toString(), "Address is not valid.");
			return;
		}
		
		// Insert FROM data in temp table
		persistanceManager.update(StorageLocation.SMTP_TEMP_MESSAGE_STORE, clientId, FieldName.getSMTPTempTableFromFieldOnly(), address);
		
		communicationHandler.sendResponse(writer, SMTPCode.OK.toString(), "");
		
	}

	private void RCPTCommand(CommunicationHandler communicationHandler,	BufferedOutputStream writer, PersistanceManager persistanceManager,	String clientId, String argument) {
		
		// Check SMTP session status
		SMTPSessionStatus currentStatus = getStatus(persistanceManager, clientId);
		
		if(!currentStatus.equals(SMTPSessionStatus.TRANSACTION)){
			communicationHandler.sendResponse(writer, SMTPCode.BAD_SEQUENCE.toString(), "Bad command sequence.");
			return;
		}
		
		// Check if the address can be added
		int recipientNumber = getRecipientNumber(persistanceManager, clientId);
		
		if(recipientNumber >= MAX_RECIPIENTS){
			communicationHandler.sendResponse(writer,SMTPCode.EXCEEDED_STORAGE_ALLOCATION.toString(), "You cannot add any more recipients.");
			return;
		}
		
		String address = getAddressFromArgument(argument);
		
		// Check the address
		if(!isValidAddress(address, persistanceManager)){
			communicationHandler.sendResponse(writer, SMTPCode.SYNTAX_ERROR.toString(), "Address is not valid.");
			return;
		}
		
		persistanceManager.addToSet(StorageLocation.SMTP_TEMP_MESSAGE_STORE, clientId, FieldName.SMTP_TEMP_TO, address);
		
		communicationHandler.sendResponse(writer, SMTPCode.OK.toString(), "");
		
	}

	private void DATACommand(CommunicationHandler communicationHandler, BufferedOutputStream writer, PersistanceManager persistanceManager, String clientId) {
		
		// Check SMTP session status
		SMTPSessionStatus currentStatus = getStatus(persistanceManager, clientId);
		
		if(!currentStatus.equals(SMTPSessionStatus.TRANSACTION) || !currentStatus.equals(SMTPSessionStatus.TRANSACTION_DATA)){
			communicationHandler.sendResponse(writer, SMTPCode.BAD_SEQUENCE.toString(), "Bad command sequence.");
			return;
		}
		
		// Set status to receiving data
		if(currentStatus.equals(SMTPSessionStatus.TRANSACTION)){
			setStatus(persistanceManager, SMTPSessionStatus.TRANSACTION_DATA, clientId);
		}
		
		communicationHandler.sendResponse(writer, SMTPCode.INTERMEDIATE_REPLY.toString(), "Start mail input; end with [CRLF].[CRLF]");

	}
	
	public void processMessageData(CommunicationHandler communicationHandler, BufferedOutputStream writer, PersistanceManager persistanceManager, String clientId, String data){
		
		// Get previous data
		String message = persistanceManager.read(StorageLocation.SMTP_TEMP_MESSAGE_STORE, FieldName.SMTP_TEMP_DATA, clientId);

		// Add the new line to the message body
		message += data;
		
		// Update message body
		persistanceManager.update(StorageLocation.SMTP_TEMP_MESSAGE_STORE, clientId, FieldName.getSMTPTempTableDataFieldOnly(), message);
		
		// Search for data termination sequence
		if(data.indexOf(dataTerminator) != -1){// End of message data
			
			processMessage(persistanceManager, clientId);
			
			communicationHandler.sendResponse(writer, SMTPCode.OK.toString(), "");
			
			setStatus(persistanceManager, SMTPSessionStatus.GREETINGS, clientId);
		}
	}

	private void processMessage(PersistanceManager persistanceManager, String clientId) {
		
        // Date
        DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
        Date date = new Date();
        String origDate = "Date:" + dateFormat.format(date);
        
		String from = "From:" + persistanceManager.read(StorageLocation.SMTP_TEMP_MESSAGE_STORE, FieldName.SMTP_TEMP_FROM, clientId);
		
		List<String> toList = persistanceManager.getSet(StorageLocation.SMTP_TEMP_MESSAGE_STORE, FieldName.SMTP_TEMP_TO, clientId);
		
		String to = "To:";
		
		for(int i = 0; i < toList.size(); i++){
			
			to += toList.get(i);
			
			if(i < toList.size() - 1){
				to += toList.get(i) + ", ";
			}
		}

		String messageBody = persistanceManager.read(StorageLocation.SMTP_TEMP_MESSAGE_STORE, FieldName.SMTP_TEMP_DATA, clientId);
		
		String toHash = from + to + messageBody;
		
		// Compute message UID
		MessageDigest cript = null;
		
		try {
			cript = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		cript.reset();
		
        try {
			cript.update(toHash.getBytes("utf8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
        
        String uid = new String(Hex.encodeHex(cript.digest())).substring(0, 20);
		
		String header = origDate + "\r\n" + from + "\r\n" + to + "\r\n" + uid;
		String body = persistanceManager.read(StorageLocation.SMTP_TEMP_MESSAGE_STORE, FieldName.SMTP_TEMP_DATA, clientId);
		
		int messageSize = (header + "\r\n" + body).length();
		
		// Get users list to deliver the message
		List<String> users = new ArrayList<String>(toList.size());
		
		for(int i = 0; i < toList.size(); i++){
			
			int startIndex = 0;
			int endIndex = toList.get(i).indexOf("@");
			
			// Search for < character (Name <email@domain.ext>)
			if(toList.get(i).indexOf("<") != -1){
				startIndex = toList.get(i).indexOf("<") + 1;
			}
			
			users.set(i, toList.get(i).substring(startIndex, endIndex));
		}
		
		
		for(int i = 0; i < users.size(); i++){
			persistanceManager.create(StorageLocation.POP3_MAILDROPS, FieldName.getPOP3MessagesTableFieldNames(),
					uid,
					users.get(i),
					POP3MessageDeletion.NO.toString(),
					Integer.toString(messageSize),
					header,
					body);
		}
		
		// Delete the temp message
		persistanceManager.delete(StorageLocation.SMTP_TEMP_MESSAGE_STORE, clientId);
	}

	private void QUITCommand(CommunicationHandler communicationHandler,	BufferedOutputStream writer, PersistanceManager persistanceManager, String clientId) {
		clearTempTable(persistanceManager, clientId);
		
		persistanceManager.delete(StorageLocation.SMTP_SESSIONS, clientId);
		
		communicationHandler.sendResponse(writer, SMTPCode.QUIT_OK_RESPONSE.toString(), "Bye!");		
	}

	private void NOOPCommand(CommunicationHandler communicationHandler,	BufferedOutputStream writer, PersistanceManager persistanceManager, String clientId) {
		communicationHandler.sendResponse(writer, SMTPCode.GREETINGS.toString(), "");
	}

	private void RSETCommand(CommunicationHandler communicationHandler,	BufferedOutputStream writer, PersistanceManager persistanceManager, String clientId) {
		clearTempTable(persistanceManager, clientId);

		communicationHandler.sendResponse(writer, SMTPCode.GREETINGS.toString(), "");
	}
	
	private void unsupportedCommand(CommunicationHandler communicationHandler, PersistanceManager persistanceManager, String clientId, BufferedOutputStream writer){
		communicationHandler.sendResponse(writer, SMTPCode.UNSUPPORTED_COMMAND.toString(), "Command is not supported");
	}
	
	@Override
	public void sendGreetings(CommunicationHandler communicationHandler, BufferedOutputStream writer, PersistanceManager persistanceManager, String clientId){
		
		// Store client ID in DB
		if(!persistanceManager.isPresent(StorageLocation.SMTP_SESSIONS, FieldName.SMTP_SESSION_ID, clientId)){

			persistanceManager.create(StorageLocation.SMTP_SESSIONS, FieldName.getSMTPStatusTableFieldNames(), 
					clientId,
					SMTPSessionStatus.GREETINGS.toString(),
					SMTPCommand.EMPTY.toString(),
					SMTPCode.EMPTY.toString(),
					SMTPCommand.EMPTY.toString(),
					SMTPCommand.EMPTY.toString());
			
			communicationHandler.sendResponse(writer, SMTPCode.GREETINGS.toString(), "SMTP server ready to roll!");
		}
	}
	
	private SMTPSessionStatus getStatus(PersistanceManager persistanceManager, String clientId){
		return SMTPSessionStatus.parseStatus(persistanceManager.read(StorageLocation.SMTP_SESSIONS, FieldName.SMTP_SESSION_STATUS, clientId));
	}

	private void setStatus(PersistanceManager persistanceManager, SMTPSessionStatus status, String clientId){

		List<FieldName> statusFields = FieldName.getSMTPStatusTableFieldOnly();
		
		persistanceManager.update(StorageLocation.SMTP_SESSIONS, clientId, statusFields, status.toString());
	}
	
	private void clearTempTable(PersistanceManager persistanceManager, String clientId){
		persistanceManager.delete(StorageLocation.SMTP_TEMP_MESSAGE_STORE, clientId);
	}
	
	private String getAddressFromArgument(String argument){
		
		int startIndex = argument.indexOf('<');
		int endIndex = argument.indexOf('>');
		
		return argument.substring(startIndex + 1, endIndex);
	}
	
	private boolean isValidAddress(String address, PersistanceManager persistanceManager){
		// TODO: check format
		
		int startIndex = 0;
		
		if(address.indexOf("<") != -1){
			startIndex = address.indexOf("<") + 1;
		}
		
		if(address.indexOf("@") == -1){
			return false;
		}
		
		String username = address.substring(startIndex, address.indexOf("@"));
		
		return persistanceManager.isPresent(StorageLocation.POP3_USERS, FieldName.POP3_USER_NAME, username);
	}
	
	private int getRecipientNumber(PersistanceManager persistanceManager, String clientId){
		
		List<String> recipients = persistanceManager.getSet(StorageLocation.SMTP_TEMP_MESSAGE_STORE, FieldName.SMTP_TEMP_TO, clientId);
		
		return recipients.size();
	}	
}
