package controller.smtp;

import java.io.BufferedOutputStream;
import java.util.List;

import controller.AbstractCommandHandler;
import controller.CommunicationHandler;
import controller.persistance.FieldName;
import controller.persistance.PersistanceManager;
import controller.persistance.StorageLocation;

public class SMTPCommandHandler extends AbstractCommandHandler {
	
	@Override
	public void handleCommand(CommunicationHandler communicationHandler, BufferedOutputStream writer, String command, String argument, String secondArgument, PersistanceManager persistanceManager, String clientId){
		
		SMTPCommand smtpCommand = SMTPCommand.parseCommand(command);
		
		if(smtpCommand.equals(SMTPCommand.EHLO)){
			EHLOCommand(communicationHandler, writer, persistanceManager, clientId);
		}else if(smtpCommand.equals(SMTPCommand.HELO)){
			HELOCommand(communicationHandler, writer, persistanceManager, clientId);
		}else if(smtpCommand.equals(SMTPCommand.MAIL)){
			MAILCommand(communicationHandler, writer, argument, persistanceManager, clientId);
		}else if(smtpCommand.equals(SMTPCommand.RCPT)){
			RCPTCommand(communicationHandler, writer, argument, persistanceManager, clientId);
		}else if(smtpCommand.equals(SMTPCommand.DATA)){
			DATACommand(communicationHandler, writer, persistanceManager, clientId);
		}else if(smtpCommand.equals(SMTPCommand.QUIT)){
			QUITCommand(communicationHandler, writer, argument, persistanceManager, clientId);
		}else if(smtpCommand.equals(SMTPCommand.UNSUPPORTED)){
			unsupportedCommand(communicationHandler, persistanceManager, clientId, writer);
		}
	}
	
	private void EHLOCommand(CommunicationHandler communicationHandler,
			BufferedOutputStream writer, PersistanceManager persistanceManager, String clientId) {
		// TODO Auto-generated method stub
		
	}
	
	private void HELOCommand(CommunicationHandler communicationHandler,
			BufferedOutputStream writer, PersistanceManager persistanceManager, String clientId) {
		// TODO Auto-generated method stub
		
	}
	
	private void MAILCommand(CommunicationHandler communicationHandler,
			BufferedOutputStream writer, String argument, PersistanceManager persistanceManager,
			String clientId) {
		// TODO Auto-generated method stub
		
	}

	private void RCPTCommand(CommunicationHandler communicationHandler,
			BufferedOutputStream writer, String argument, PersistanceManager persistanceManager,
			String clientId) {
		// TODO Auto-generated method stub
		
	}

	private void DATACommand(CommunicationHandler communicationHandler,
			BufferedOutputStream writer, PersistanceManager persistanceManager, String clientId) {
		// TODO Auto-generated method stub
		
	}

	private void QUITCommand(CommunicationHandler communicationHandler,
			BufferedOutputStream writer, String argument, PersistanceManager persistanceManager,
			String clientId) {
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
	
	private SMTPCommand getPreviousCommand(PersistanceManager persistanceManager, String clientId){
		String result = persistanceManager.read(StorageLocation.SMTP_SESSIONS, FieldName.SMTP_LAST_COMMAND, clientId);

		if(result.isEmpty()){
			return SMTPCommand.EMPTY;
		}else{
			return SMTPCommand.parseCommand(result);
		}
	}
	
	private String getPreviousCommandFirstArgument(PersistanceManager persistanceManager, String clientId){
		return persistanceManager.read(StorageLocation.SMTP_SESSIONS, FieldName.SMTP_LAST_COMMAND_FIRST_ARGUMENT, clientId);
	}
	
	private SMTPCode getPreviousCommandResult(PersistanceManager persistanceManager, String clientId){
		String result = persistanceManager.read(StorageLocation.SMTP_SESSIONS, FieldName.SMTP_LAST_COMMAND_RESULT, clientId);

		if(result.isEmpty()){
			return SMTPCode.UNKNOWN;
		}else{
			return SMTPCode.parseCode(result);
		}
	}
	
	private void setLastCommand(PersistanceManager persistanceManager, String clientId, SMTPCommand smtpCommand, SMTPCode smtpCode){
		
		List<FieldName> commandFields = FieldName.getSMTPStatusCommandTableFieldNames();
		
		persistanceManager.update(StorageLocation.SMTP_SESSIONS, clientId, commandFields, 
				smtpCommand.toString(), 
				smtpCode.toString(), 
				SMTPCommand.EMPTY.toString());
	}
	
	private void setLastCommandWithOneArgument(PersistanceManager persistanceManager, String clientId, SMTPCommand smtpCommand, SMTPCode smtpCode, String argument){
		
		List<FieldName> commandFields = FieldName.getSMTPStatusCommandTableFieldNames();
		
		persistanceManager.update(StorageLocation.SMTP_SESSIONS, clientId, commandFields, 
				smtpCommand.toString(), 
				smtpCode.toString(), 
				argument);
	}
	
	private SMTPSessionStatus getStatus(PersistanceManager persistanceManager, String clientId){
		return SMTPSessionStatus.parseStatus(persistanceManager.read(StorageLocation.SMTP_SESSIONS, FieldName.SMTP_SESSION_STATUS, clientId));
	}

	private void setStatus(PersistanceManager persistanceManager, SMTPSessionStatus status, String clientId){

		List<FieldName> statusFields = FieldName.getSMTPStatusTableFieldOnly();
		
		persistanceManager.update(StorageLocation.SMTP_SESSIONS, clientId, statusFields, status.toString());
	}
	
}
