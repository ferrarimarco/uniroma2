package controller.pop3;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import controller.RequestHandler;
import controller.persistance.PersistanceManager;
import controller.persistance.StorageManager;

public class POP3RequestHandler implements RequestHandler {

	@Override
	public void handleRequest(Socket socket) {
		
		POP3CommandHandler pop3CommandHandler = new POP3CommandHandler();
		POP3CommunicationHandler pop3CommunicationHandler = new POP3CommunicationHandler();
		
		PersistanceManager storageManager = new StorageManager();
		
		String clientId = socket.getInetAddress().getHostName();
		
		// TODO: DEBUG
		System.out.println("POP3 Connection received from " + socket.getInetAddress().getHostName());
		
		BufferedOutputStream writer;
		BufferedReader reader;
		
		try {
			// Get Input and Output streams
			writer = new BufferedOutputStream(socket.getOutputStream());
			writer.flush();

			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			// Send greetings (if necessary)
			pop3CommandHandler.sendGreetings(pop3CommunicationHandler, writer, storageManager, clientId);
			
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
				
				handleCommand(pop3CommunicationHandler, pop3CommandHandler, writer, command, argument, secondArgument, storageManager, clientId);
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
	
	private void handleCommand(POP3CommunicationHandler pop3CommunicationHandler, POP3CommandHandler pop3CommandHandler, BufferedOutputStream writer, String command, String argument, String secondArgument, PersistanceManager persistanceManager, String clientId){
		
		POP3Command pop3Command = POP3Command.parseCommand(command);
		
		if(pop3Command.equals(POP3Command.CAPA)){
			pop3CommandHandler.CAPACommand(pop3CommunicationHandler, writer, persistanceManager, clientId);
		}else if(pop3Command.equals(POP3Command.QUIT)){
			pop3CommandHandler.QUITCommand(pop3CommunicationHandler, writer, persistanceManager, clientId);
		}else if(pop3Command.equals(POP3Command.USER)){
			pop3CommandHandler.USERCommand(pop3CommunicationHandler, writer, argument, persistanceManager, clientId);
		}else if(pop3Command.equals(POP3Command.PASS)){
			pop3CommandHandler.PASSCommand(pop3CommunicationHandler, writer, argument, persistanceManager, clientId);
		}else if(pop3Command.equals(POP3Command.STAT)){
			pop3CommandHandler.STATCommand(pop3CommunicationHandler, writer, persistanceManager, clientId);
		}else if(pop3Command.equals(POP3Command.LIST)){
			pop3CommandHandler.LISTCommand(pop3CommunicationHandler, writer, argument, persistanceManager, clientId);
		}else if(pop3Command.equals(POP3Command.RETR)){
			pop3CommandHandler.RETRCommand(pop3CommunicationHandler, writer, argument, persistanceManager, clientId);
		}else if(pop3Command.equals(POP3Command.DELE)){
			pop3CommandHandler.DELECommand(pop3CommunicationHandler, writer, argument, persistanceManager, clientId);
		}else if(pop3Command.equals(POP3Command.NOOP)){
			pop3CommandHandler.NOOPCommand(pop3CommunicationHandler, writer, persistanceManager, clientId);
		}else if(pop3Command.equals(POP3Command.RSET)){
			pop3CommandHandler.RSETCommand(pop3CommunicationHandler, writer, persistanceManager, clientId);
		}else if(pop3Command.equals(POP3Command.TOP)){
			pop3CommandHandler.TOPCommand(pop3CommunicationHandler, writer, argument, secondArgument, persistanceManager, clientId);
		}else if(pop3Command.equals(POP3Command.UNSUPPORTED)){
			pop3CommandHandler.unsupportedCommand(pop3CommunicationHandler, persistanceManager, clientId, writer);
		}
	}
}
