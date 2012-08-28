package controller.pop3;

import java.io.BufferedOutputStream;
import java.util.ArrayList;
import java.util.List;

public class POP3CommandHandler {
	
	private POP3CommunicationHandler pop3CommunicationHandler;
	
	public POP3CommandHandler(){
		pop3CommunicationHandler = new POP3CommunicationHandler();
	}
	
	public void USERCommand(BufferedOutputStream writer, POP3SessionStatus status, String argument){
		
		// Check the status of the POP3 session
		if(!status.equals(POP3SessionStatus.AUTHORIZATION)){
			pop3CommunicationHandler.sendResponse(writer, POP3StatusIndicator.ERR, "This command is available only in AUTHORIZATION status.");
			return;
		}
		
		// Check the previous command: USER is available only in AUTH status, after the POP3 server greeting or after an unsuccessful USER or PASS command
		// TODO: get the previous command from DB. Valid ones are: empty, USER (failed), PASS (failed). Other possibilities must send an error and return the current session status
		
		// Check the argument
		if(argument.isEmpty() || argument == null){
			pop3CommunicationHandler.sendResponse(writer, POP3StatusIndicator.ERR, "Missing command argument");
			return;
		}
		
		// TODO: search for the username supplied as "argument" in DB
		// TODO: if not found, send an error and return the current status
		// TODO: if found send OK and wait for the PASS command only. Then record the transaction in DB (last command: USER, output: OK, arg: argument)		
	}
	
	public POP3SessionStatus PASSCommand(BufferedOutputStream writer, POP3SessionStatus status, String argument){
		
		// Check the status of the POP3 session
		if(!status.equals(POP3SessionStatus.AUTHORIZATION)){
			pop3CommunicationHandler.sendResponse(writer, POP3StatusIndicator.ERR, "This command is available only in AUTHORIZATION status.");
			return status;
		}
		
		// Check the previous command: PASS is available only in AUTH status, after a successful USER command
		// TODO: get the previous command from DB. Valid ones are: USER (passed). Other possibilities must send an error and return the current session status
		
		// Check the argument
		if(argument.isEmpty() || argument == null){
			pop3CommunicationHandler.sendResponse(writer, POP3StatusIndicator.ERR, "Missing command argument");
			return status;
		}
		
		// TODO: try to match the current password with the username sent with the previous USER command (DB)
		// TODO: if they don't match, send an error and return the current status
		// TODO: if they match send OK and change status to TRANSACTION. Then record the transaction in DB (last command: PASS, output: OK, arg: argument)
		
		// TODO: check this return statement
		return status;
	}
	
	public POP3SessionStatus QUITCommand(BufferedOutputStream writer, POP3SessionStatus status){
		
		if(status.equals(POP3SessionStatus.GREETINGS)){
			pop3CommunicationHandler.sendResponse(writer, POP3StatusIndicator.ERR, "POP3 server is in GREETINGS status");
		}else{
			pop3CommunicationHandler.sendResponse(writer, POP3StatusIndicator.OK, "Closing session");

			if(status.equals(POP3SessionStatus.TRANSACTION)){
				return POP3SessionStatus.UPDATE;
			}
		}
		
		// TODO: unlock mailbox
		// TODO: delete marked messages
		
		return status;
	}

	public void CAPACommand(BufferedOutputStream writer, POP3SessionStatus status){

		if(status.equals(POP3SessionStatus.GREETINGS)){
			pop3CommunicationHandler.sendLine(writer, POP3StatusIndicator.ERR + " This command is not available in GREETINGS status", false, false);
		}else{
			pop3CommunicationHandler.sendLine(writer, POP3StatusIndicator.OK + " Capabilities follow.", false, false);
			
			List<String> capabilities = new ArrayList<String>();
			capabilities.add("CAPA");
			capabilities.add("USER");
			capabilities.add("PASS");
			capabilities.add("QUIT");
			capabilities.add("STAT");
			capabilities.add("LIST");
			capabilities.add("RETR");
			capabilities.add("DELE");
			capabilities.add("TOP");
			capabilities.add("NOOP");
			capabilities.add("RSET");
			
			pop3CommunicationHandler.sendListAsMultiLineResponse(writer, capabilities);
		}
		
		// TODO: Send capabilities
		//sendLine(msg, true, false);
	}
	
	public void STATCommand(BufferedOutputStream writer, POP3SessionStatus status){
		
		// Check the status of the POP3 session
		if(!status.equals(POP3SessionStatus.TRANSACTION)){
			pop3CommunicationHandler.sendResponse(writer, POP3StatusIndicator.ERR, "This command is available only in TRANSACTION status.");
		}
		
		int messages = 0;
		int dimension = 0;
		
		// Get the maildrop info about the user from DB
		// TODO: get # of messages and dimension for each message from DB
		
		pop3CommunicationHandler.sendResponse(writer, POP3StatusIndicator.OK, messages + " " + dimension);
	}
	
	public void LISTCommand(BufferedOutputStream writer, POP3SessionStatus status, String argument){
		
		// Check the status of the POP3 session
		if(!status.equals(POP3SessionStatus.TRANSACTION)){
			pop3CommunicationHandler.sendResponse(writer, POP3StatusIndicator.ERR, "This command is available only in TRANSACTION status.");
			return;
		}
		
		// Check the argument
		if(argument.isEmpty() || argument == null){
			
			// Get information about all the messages in the maildrop
			List<String> messages = new ArrayList<String>();
			//TODO: read info from DB
			
			pop3CommunicationHandler.sendResponse(writer, POP3StatusIndicator.OK, messages.size() + " messages");
			
			// Send response
			if(messages.size() > 0){
				for(int i = 0; i < messages.size(); i++){
					if(i < messages.size() - 1){
						int dimension = 0;
						// TODO: compute the dimension of the i-nth message
						
						pop3CommunicationHandler.sendLine(writer, i + " " + dimension, true, false);
					}else{// Last line of the response
						int dimension = 0;
						// TODO: compute the dimension of the i-nth message
						
						pop3CommunicationHandler.sendLine(writer, i + " " + dimension, true, true);
					}
				}
			}else{// No messages in the maildrop
				pop3CommunicationHandler.sendResponse(writer, POP3StatusIndicator.OK, "");
				pop3CommunicationHandler.sendLine(writer, "", true, true);
			}

			
		}else{
			
			// Check if such message exists
			// TODO: check for such message in DB
			
			
			// Get the dimension of the message
			int dimension = 0;
			// TODO: compute the exact dimension of the message
			
			// Send the response
			pop3CommunicationHandler.sendResponse(writer, POP3StatusIndicator.OK, argument + " " + dimension);			
		}
	}
	
	public void RETRCommand(BufferedOutputStream writer, POP3SessionStatus status, String argument){
		// Check the status of the POP3 session
		if(!status.equals(POP3SessionStatus.TRANSACTION)){
			pop3CommunicationHandler.sendResponse(writer, POP3StatusIndicator.ERR, "This command is available only in TRANSACTION status.");
			return;
		}
		
		// Check the argument
		if(argument.isEmpty() || argument == null){
			pop3CommunicationHandler.sendResponse(writer, POP3StatusIndicator.ERR, "Missing command argument");
			return;
		}
		
		// Check if the specified message exists
		// TODO: check the message identified by the argument in the DB
		// TODO: send an error if the message does not exists and return
		
		// Send the message in the multiline response
		pop3CommunicationHandler.sendResponse(writer, POP3StatusIndicator.OK, "message follows");
		
		// TODO: send the message contents
	}
	
	public void DELECommand(BufferedOutputStream writer, POP3SessionStatus status, String argument){
		
		// Check the status of the POP3 session
		if(!status.equals(POP3SessionStatus.TRANSACTION)){
			pop3CommunicationHandler.sendResponse(writer, POP3StatusIndicator.ERR, "This command is available only in TRANSACTION status.");
			return;
		}
		
		// Check the argument
		if(argument.isEmpty() || argument == null){
			pop3CommunicationHandler.sendResponse(writer, POP3StatusIndicator.ERR, "Missing command argument.");
			return;
		}
		
		// Search for such a message
		// TODO: search for the message
		
		// Mark the message, if found for deletion. If there is no such message, send a negative response and return.
		// TODO: mark the message for deletion
		
		// Send positive response
		pop3CommunicationHandler.sendResponse(writer, POP3StatusIndicator.OK, "Message marked for deletion.");
	}
	
	public void TOPCommand(BufferedOutputStream writer, POP3SessionStatus status, String argument, String secondArgument){
		
		// Check the status of the POP3 session
		if(!status.equals(POP3SessionStatus.TRANSACTION)){
			pop3CommunicationHandler.sendResponse(writer, POP3StatusIndicator.ERR, "This command is available only in TRANSACTION status.");
			return;
		}
		
		// Check the argument
		if(argument.isEmpty() || argument == null || secondArgument.isEmpty() || secondArgument == null){
			pop3CommunicationHandler.sendResponse(writer, POP3StatusIndicator.ERR, "Missing command argument.");
			return;
		}
		
		// Check if the specified message exists
		// TODO: check the message identified by the argument in the DB
		// TODO: send an error if the message does not exists and return
		
		// Send the message in the multiline response
		pop3CommunicationHandler.sendResponse(writer, POP3StatusIndicator.OK, "top of message follows");
		
		// TODO: send the message contents
	}
	
	public void NOOPCommand(BufferedOutputStream writer, POP3SessionStatus status){
		
		// Check the status of the POP3 session
		if(!status.equals(POP3SessionStatus.TRANSACTION)){
			pop3CommunicationHandler.sendResponse(writer, POP3StatusIndicator.ERR, "This command is available only in TRANSACTION status.");
			return;
		}
		
		pop3CommunicationHandler.sendResponse(writer, POP3StatusIndicator.OK, "NOOP command received.");
	}
	
	public void RSETCommand(BufferedOutputStream writer, POP3SessionStatus status){
		
		// Check the status of the POP3 session
		if(!status.equals(POP3SessionStatus.TRANSACTION)){
			pop3CommunicationHandler.sendResponse(writer, POP3StatusIndicator.ERR, "This command is available only in TRANSACTION status.");
			return;
		}
		
		// Unmarks every messaged marked for deletion
		// TODO: search for such messages in the DB and unmark each one
		
		pop3CommunicationHandler.sendResponse(writer, POP3StatusIndicator.OK, "Maildrop reset completed.");
	}
	
	public void unsupportedCommand(BufferedOutputStream writer){
		pop3CommunicationHandler.sendResponse(writer, POP3StatusIndicator.ERR, "Command is not supported");
	}
	
	public void sendGreetings(BufferedOutputStream writer){
		pop3CommunicationHandler.sendResponse(writer, POP3StatusIndicator.OK, "POP3 server ready to roll!");
	}
}
