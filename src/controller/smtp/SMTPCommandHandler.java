package controller.smtp;

import java.io.BufferedOutputStream;
import java.util.List;

import controller.AbstractCommandHandler;
import controller.CommunicationHandler;
import controller.persistance.FieldName;
import controller.persistance.PersistanceManager;
import controller.persistance.StorageLocation;

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
			QUITCommand(communicationHandler, writer, argument, persistanceManager, clientId);
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
		
		if(!isValidAddress(address)){
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
		int recipientNumber = getRecipientNumber(clientId);
		
		if(recipientNumber >= MAX_RECIPIENTS){
			communicationHandler.sendResponse(writer,SMTPCode.EXCEEDED_STORAGE_ALLOCATION.toString(), "You cannot add any more recipients.");
			return;
		}
		
		String address = getAddressFromArgument(argument);
		
		// Check the address
		if(!isValidAddress(address)){
			communicationHandler.sendResponse(writer, SMTPCode.SYNTAX_ERROR.toString(), "Address is not valid.");
			return;
		}
		
		addRecipient(address, clientId);
		
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
		
		// Search for data termination sequence
		if(data.indexOf(dataTerminator) != -1){// End of message data
			
			// TODO: Add data to data field

			// TODO: change status to what?
			
			// TODO: process message
			
			communicationHandler.sendResponse(writer, SMTPCode.OK.toString(), "");
		}else{
			// TODO: add data to data field
		}
	}

	private void QUITCommand(CommunicationHandler communicationHandler,	BufferedOutputStream writer, String argument, PersistanceManager persistanceManager, String clientId) {
		// TODO Auto-generated method stub
		
	}

	private void unsupportedCommand(CommunicationHandler communicationHandler, PersistanceManager persistanceManager, String clientId, BufferedOutputStream writer){
		
		// TODO: save as last command unsupported
		
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
	
	private boolean isValidAddress(String address){
		// TODO: write implementation
		return false;
	}
	
	private int getRecipientNumber(String clientId){
		// TODO: write implementation
		
		return 0;
	}
	
	private void addRecipient(String recipient, String clientId){
		// TODO: write implementation
	}
	
}
