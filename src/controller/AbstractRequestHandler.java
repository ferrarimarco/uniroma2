package controller;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import controller.persistance.PersistanceManager;
import controller.persistance.StorageManager;

public abstract class AbstractRequestHandler implements RequestHandler {

	@Override
	public void handleRequest(Socket socket, CommandHandler commandHandler, CommunicationHandler communicationHandler) {

		PersistanceManager storageManager = new StorageManager();
		
		// TODO: the client uses multiple ports!!! so the port section may change!!!!
		
		String clientId = socket.getInetAddress().getHostAddress();
		// Temp solution: comment the port. This will not work if multiple users share the same public ip
		//clientId += ":" + socket.getPort();
		
		// TODO: DEBUG
		System.out.println("Connection received from " + clientId);
		
		try {
			// Get Input and Output streams
			BufferedOutputStream writer = new BufferedOutputStream(socket.getOutputStream());
			writer.flush();

			BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			// Send greetings (if necessary)
			commandHandler.sendGreetings(communicationHandler, writer, storageManager, clientId);
			
			String message = "";
			String command = "";
			String argument = "";
			String secondArgument = "";
			String [] commandElements;
			
			while ((message = reader.readLine()) != null) {
				System.out.println("Server receives: " + message);
				
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
				
				commandHandler.handleCommand(communicationHandler, writer, command, argument, secondArgument, storageManager, clientId);
			}

			// Done handling the command
			stop(reader, writer);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	protected void stop(BufferedReader reader, BufferedOutputStream writer){
		try {
			reader.close();
			writer.close();
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}
	
}
