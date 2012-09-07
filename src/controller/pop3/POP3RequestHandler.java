package controller.pop3;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import controller.AbstractRequestHandler;
import controller.RequestHandler;
import controller.persistance.PersistanceManager;
import controller.persistance.StorageManager;

public class POP3RequestHandler extends AbstractRequestHandler implements RequestHandler {
	
	@Override
	public void handleRequest(Socket socket) {
		
		POP3CommandHandler pop3CommandHandler = new POP3CommandHandler();
		POP3CommunicationHandler pop3CommunicationHandler = new POP3CommunicationHandler();
		
		PersistanceManager storageManager = new StorageManager();
		
		// TODO: the client uses multiple ports!!! so the port section may change!!!!
		
		String clientId = socket.getInetAddress().getHostAddress();
		// Temp solution: comment the port. This will not work if multiple users share the same public ip
		//clientId += ":" + socket.getPort();
		
		// TODO: DEBUG
		System.out.println("POP3 Connection received from " + clientId);
		
		try {
			// Get Input and Output streams
			BufferedOutputStream writer = new BufferedOutputStream(socket.getOutputStream());
			writer.flush();

			BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			// Send greetings (if necessary)
			pop3CommandHandler.sendGreetings(pop3CommunicationHandler, writer, storageManager, clientId);
			
			String message = "";
			String command = "";
			String argument = "";
			String secondArgument = "";
			String [] commandElements;
			
			while ((message = reader.readLine()) != null) {
				System.out.println("POP3 server riceve: " + message);
				
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
				
				pop3CommandHandler.handleCommand(pop3CommunicationHandler, pop3CommandHandler, writer, command, argument, secondArgument, storageManager, clientId);
			}

			// Done handling the command
			stop(reader, writer);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
