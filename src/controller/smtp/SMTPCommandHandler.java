package controller.smtp;

import java.io.BufferedOutputStream;


public class SMTPCommandHandler {

	private SMTPCommunicationHandler smtpCommunicationHandler;
	
	public SMTPCommandHandler(){
		smtpCommunicationHandler = new SMTPCommunicationHandler();
	}
	
	public void unsupportedCommand(BufferedOutputStream writer){
		smtpCommunicationHandler.sendResponse(writer, SMTPCode.ERR, "Command is not supported");
	}
	
	public void sendGreetings(BufferedOutputStream writer){
		smtpCommunicationHandler.sendResponse(writer, SMTPCode.OK, "SMTP server ready to roll!");
	}
	
}
