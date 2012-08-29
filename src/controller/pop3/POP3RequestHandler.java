package controller.pop3;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import controller.RequestHandler;
import controller.persistance.PersistanceManager;
import controller.persistance.StorageLocation;
import controller.persistance.StorageManager;

public class POP3RequestHandler implements RequestHandler {

	@Override
	public void handleRequest(Socket socket) {
		
		POP3CommandHandler pop3CommandHandler = new POP3CommandHandler();
		
		PersistanceManager storageManager = new StorageManager();
		
		// Store client ID
		String clientId = socket.getInetAddress().getHostName();
		storeClientId(storageManager, clientId);
		
		// TODO: DEBUG
		System.out.println("POP3 Connection received from " + socket.getInetAddress().getHostName());
		
		BufferedOutputStream writer;
		BufferedReader reader;
		
		try {
			// Get Input and Output streams
			writer = new BufferedOutputStream(socket.getOutputStream());
			writer.flush();

			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			// Check the status of the POP3 session
			if(getStatus(storageManager, clientId).equals(POP3SessionStatus.GREETINGS)){
				pop3CommandHandler.sendGreetings(writer, storageManager, clientId);
				setStatus(storageManager, POP3SessionStatus.AUTHORIZATION, clientId);
			}
			
			String message = "";
			String command = "";
			String argument = "";
			String secondArgument = "";
			String [] commandElements;
			
			while ((message = reader.readLine()) != null) {
				System.out.println("POP3 server riceve:" + message);
				
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
				
				handleCommand(pop3CommandHandler, writer, command, argument, secondArgument, storageManager, clientId);
			}

			// Done handling the command
			stop(reader, writer);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void stop(BufferedReader reader, BufferedOutputStream writer){
		try {
			reader.close();
			writer.close();
			
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}
	
	private void handleCommand(POP3CommandHandler pop3CommandHandler, BufferedOutputStream writer, String command, String argument, String secondArgument, PersistanceManager persistanceManger, String clientId){
		
		// To hold the POP3Status to eventually set after the command
		POP3SessionStatus resultingStatus = POP3SessionStatus.UNKNOWN;
		
		switch(command.toUpperCase()){
			case "CAPA":
				pop3CommandHandler.CAPACommand(writer, getStatus(persistanceManger, clientId), persistanceManger, clientId);
				break;
			case "QUIT":
				resultingStatus = pop3CommandHandler.QUITCommand(writer, getStatus(persistanceManger, clientId), persistanceManger, clientId);
				break;
			case "USER":
				pop3CommandHandler.USERCommand(writer, getStatus(persistanceManger, clientId), argument, persistanceManger, clientId);
				break;
			case "PASS":
				resultingStatus = pop3CommandHandler.PASSCommand(writer, getStatus(persistanceManger, clientId), argument, persistanceManger, clientId);
				break;
			case "STAT":
				pop3CommandHandler.STATCommand(writer, getStatus(persistanceManger, clientId), persistanceManger, clientId);
				break;
			case "LIST":
				pop3CommandHandler.LISTCommand(writer, getStatus(persistanceManger, clientId), argument, persistanceManger, clientId);
				break;
			case "RETR":
				pop3CommandHandler.RETRCommand(writer, getStatus(persistanceManger, clientId), argument, persistanceManger, clientId);
				break;
			case "DELE":
				pop3CommandHandler.DELECommand(writer, getStatus(persistanceManger, clientId), argument, persistanceManger, clientId);
				break;
			case "NOOP":
				pop3CommandHandler.NOOPCommand(writer, getStatus(persistanceManger, clientId), persistanceManger, clientId);
				break;
			case "RSET":
				pop3CommandHandler.RSETCommand(writer, getStatus(persistanceManger, clientId), persistanceManger, clientId);
				break;
			case "TOP":
				pop3CommandHandler.TOPCommand(writer, getStatus(persistanceManger, clientId), argument, secondArgument, persistanceManger, clientId);
				break;
			default:
				pop3CommandHandler.unsupportedCommand(writer);
				break;
		}
		
		// Set the status after the command according to the POP3 protocol specification
		if(resultingStatus != POP3SessionStatus.UNKNOWN){
			setStatus(persistanceManger, resultingStatus, clientId);
		}
	}

	private POP3SessionStatus getStatus(PersistanceManager persistanceManger, String clientId){
		
		return POP3SessionStatus.parseStatus(persistanceManger.read(StorageLocation.POP3_STATUS, clientId));
	}

	private void setStatus(PersistanceManager persistanceManger, POP3SessionStatus status, String clientId){

		POP3SessionStatus currentStatus = getStatus(persistanceManger, clientId);
		
		if(!currentStatus.equals(status)){
			persistanceManger.update(StorageLocation.POP3_STATUS, clientId, status.toString());
		}
	}
	
	private void storeClientId(PersistanceManager persistanceManager, String clientId){
		
		// Search for clientId
		if(persistanceManager.read(StorageLocation.CLIENT_ID, clientId).length() == 0){// If no such entry found, create one
			persistanceManager.create(StorageLocation.POP3_STATUS, clientId, POP3SessionStatus.GREETINGS.toString());
		}
	}
}
