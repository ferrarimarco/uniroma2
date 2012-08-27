package controller.smtp;

import java.io.BufferedOutputStream;


public class SMTPCommandHandler {

	private SMTPCommunicationHandler smtpCommunicationHandler;
	
	public SMTPCommandHandler(){
		smtpCommunicationHandler = new SMTPCommunicationHandler();
	}
	
	public void unsupportedCommand(BufferedOutputStream writer){
		smtpCommunicationHandler.sendResponse(writer, SMTPCode.UNSUPPORTED_COMMAND, "Command is not supported");
	}
	
	public void sendGreetings(BufferedOutputStream writer){
		smtpCommunicationHandler.sendResponse(writer, SMTPCode.GREETINGS, "SMTP server ready to roll!");
	}
	
}
