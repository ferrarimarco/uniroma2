package controller.smtp;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import controller.AbstractRequestHandler;
import controller.RequestHandler;
import controller.persistance.PersistanceManager;
import controller.persistance.StorageManager;

public class SMTPRequestHandler extends AbstractRequestHandler implements RequestHandler {

	@Override
	public void handleRequest(Socket socket) {
		
		SMTPCommandHandler smtpCommandHandler = new SMTPCommandHandler();
		SMTPCommunicationHandler smtpCommunicationHandler = new SMTPCommunicationHandler();
		
		PersistanceManager storageManager = new StorageManager();
		
		// TODO: the client uses multiple ports!!! so the port section may change!!!!
		
		String clientId = socket.getInetAddress().getHostAddress();
		// Temp solution: comment the port. This will not work if multiple users share the same public ip
		//clientId += ":" + socket.getPort();
		
		// TODO: DEBUG
		System.out.println("SMTP Connection received from " + clientId);

		try {
			// Get Input and Output streams
			BufferedOutputStream writer = new BufferedOutputStream(socket.getOutputStream());
			writer.flush();

			BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			// Send greetings if necessary
			smtpCommandHandler.sendGreetings(smtpCommunicationHandler, writer, storageManager, clientId);
			
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
				
				// TODO: DEBUG
				//System.out.println(java.util.Arrays.toString(commandElements));
				
				handleCommand(smtpCommunicationHandler, smtpCommandHandler, writer, command, argument, secondArgument, storageManager, clientId);
			}

			// Done handling command
			stop(reader, writer);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void handleCommand(SMTPCommunicationHandler smtpCommunicationHandler, SMTPCommandHandler smtpCommandHandler, BufferedOutputStream writer, String command, String argument, String secondArgument, PersistanceManager persistanceManager, String clientId){

		// TODO: change to IF with SMTPCommand enum
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
	}
}
