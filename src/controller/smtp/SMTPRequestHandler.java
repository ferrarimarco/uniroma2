package controller.smtp;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import controller.RequestHandler;

public class SMTPRequestHandler implements RequestHandler {

	// This variable is needed only because we don't have db access, yet
	private SMTPSessionStatus status = SMTPSessionStatus.UNKNOWN;
	
	private SMTPCommandHandler smtpCommandHandler;
	
	public SMTPRequestHandler(){
		smtpCommandHandler = new SMTPCommandHandler();
	}
	
	@Override
	public void handleRequest(Socket socket) {
		System.out.println("Connection received from " + socket.getInetAddress().getHostName());
		
		BufferedOutputStream writer;
		BufferedReader reader;
		
		try {
			// Get Input and Output streams
			writer = new BufferedOutputStream(socket.getOutputStream());
			writer.flush();

			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			// Check the status of the POP3 session
			if(getStatus().equals(SMTPSessionStatus.GREETINGS)){
				smtpCommandHandler.sendGreetings(writer);
				setStatus(SMTPSessionStatus.TRANSACTION);
			}
			
			String message = "";
			String command = "";
			String argument = "";
			String secondArgument = "";
			String [] commandElements;
			
			while ((message = reader.readLine()) != null) {
				System.out.println("SMTP server riceve:" + message);
				
				commandElements = message.split("\\s+");
				
				command = commandElements[0];
				
				if(commandElements.length > 1){
					argument = commandElements[1];
				}
				
				// Check if there is a second argument
				if(commandElements.length > 2){
					secondArgument = commandElements[2];
				}
				
				// DEBUG
				//System.out.println(java.util.Arrays.toString(commandElements));
				
				handleCommand(writer, command, argument, secondArgument);
			}

			// TODO: Done handling command
			//stop(reader, writer, socket);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void handleCommand(BufferedOutputStream writer, String command, String argument, String secondArgument){
		
		// To hold the POP3Status to eventually set after the command
		SMTPSessionStatus resultingStatus = SMTPSessionStatus.UNKNOWN;
		
		switch(command.toUpperCase()){
		case "EHLO":
			break;
		case "HELO":
			break;
		case "MAIL":
			break;
		case "RCPT":
			break;
		case "DATA":
			break;
		case "QUIT":
			break;
		default:
			smtpCommandHandler.unsupportedCommand(writer);
			break;
		}
		
		// Set the status after the command according to the POP3 protocol specification
		if(resultingStatus != SMTPSessionStatus.UNKNOWN){
			setStatus(resultingStatus);
		}
	}
	
	private SMTPSessionStatus getStatus(){
		
		// TODO: Get status from DB (this code is just a placeholder for DB access)
		if(status.equals(SMTPSessionStatus.UNKNOWN)){
			setStatus(SMTPSessionStatus.GREETINGS);
		}
		
		return status;
	}
	
	private void setStatus(SMTPSessionStatus status){
		
		// TODO: Write status in DB
		// TODO: read the status and write only if different?
		
		this.status = status;
	}

}
