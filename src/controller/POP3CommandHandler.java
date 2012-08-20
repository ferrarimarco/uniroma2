package controller;

import java.io.BufferedOutputStream;

public class POP3CommandHandler {
	
	private POP3CommunicationHandler pop3CommunicationHandler;
	
	public POP3CommandHandler(){
		pop3CommunicationHandler = new POP3CommunicationHandler();
	}
	
	public POP3Status USERCommand(BufferedOutputStream writer, POP3Status status, String argument){
		// TODO: write implementation
		
		return status;
	}
	
	public POP3Status QUITCommand(BufferedOutputStream writer, POP3Status status){

		// TODO: unlock mailbox

		if(status.equals(POP3Status.GREETINGS)){
			pop3CommunicationHandler.sendResponse(writer, POP3StatusIndicator.ERR, "POP3 server is in GREETINGS status");
		}else{
			pop3CommunicationHandler.sendResponse(writer, POP3StatusIndicator.OK, "Closing session");

			if(status.equals(POP3Status.TRANSACTION)){
				return POP3Status.UPDATE;
			}
		}
		
		return status;
	}

	public POP3Status CAPACommand(BufferedOutputStream writer, POP3Status status){

		if(status.equals(POP3Status.GREETINGS)){
			pop3CommunicationHandler.sendLine(writer, POP3StatusIndicator.ERR + " POP3 server is in GREETINGS status", false, false);
		}else{
			pop3CommunicationHandler.sendLine(writer, POP3StatusIndicator.OK + " Capabilities follow.", false, false);
		}
		
		// TODO: Send capabilities
		//sendLine(msg, true, false);
		
		return status;
	}
}
