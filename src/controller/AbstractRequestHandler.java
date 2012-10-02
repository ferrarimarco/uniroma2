package controller;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import controller.persistance.PersistanceManager;
import controller.persistance.StorageManager;

public abstract class AbstractRequestHandler implements RequestHandler {

	private Socket socket;
	private CommandHandler commandHandler;
	private CommunicationHandler communicationHandler;
	
	public static final Logger log = Logger.getLogger("sd-mail-server-claudiani-ferrari.mailserver");
	private static FileHandler fileHandler;
	
	public AbstractRequestHandler(Socket socket, CommandHandler commandHandler, CommunicationHandler communicationHandler) {
		this.socket = socket;
		this.commandHandler = commandHandler;
		this.communicationHandler = communicationHandler;
		
		try {
			fileHandler = new FileHandler("/home/ubuntu/log.txt", true);
			
			fileHandler.setFormatter(new SimpleFormatter());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		log.addHandler(fileHandler);
		log.setLevel(Level.INFO);
	}
	
	@Override
	public void run() {
		handleRequest();
	}
	
	@Override
	public void handleRequest() {
		String clientId = getClientId(socket);

		// TODO: DEBUG
		log.info("Connection received from " + clientId + " on port " + socket.getLocalPort());
		System.out.println("Connection received from " + clientId + " on port " + socket.getLocalPort());

		PersistanceManager storageManager = new StorageManager();
		
		BufferedOutputStream writer = null;
		BufferedReader reader = null;
		
		try {
			// Get Input and Output streams
			writer = new BufferedOutputStream(socket.getOutputStream());
			writer.flush();

			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			// Send greetings (if necessary)
			commandHandler.sendGreetings(communicationHandler, writer, storageManager, clientId);

			String message = "";
			String command = "";
			String argument = "";
			String secondArgument = "";
			String[] commandElements;

			while ((message = reader.readLine()) != null) {
				
				// TODO: debug
				log.info("Server receives: " + message);
				System.out.println("Server receives: " + message);

				// Initialize
				command = "";
				argument = "";
				secondArgument = "";

				commandElements = message.split("\\s+");

				// Check if the sender sends an empty line
				if(commandElements.length > 0) {
					command = commandElements[0];
				}else {
					command = "";
				}

				if (commandElements.length > 1) {
					argument = commandElements[1];
				}

				// Check if there is a second argument
				if (commandElements.length > 2) {
					secondArgument = commandElements[2];
				}

				commandHandler.handleCommand(communicationHandler, writer, message, command, argument, secondArgument, storageManager, clientId);
			}

		} catch (Exception e) {
			e.printStackTrace();
			
			// Clear session status
			commandHandler.clearStatus(storageManager, clientId);
			
			// Send abnormal termination request
			commandHandler.sendAbnormalTerminationResponse(communicationHandler, writer);
		} finally {
			
			// Done handling the command
			stop(socket, reader, writer);

		}
	}

	private void stop(Socket socket, BufferedReader reader, BufferedOutputStream writer) {
		try {
			reader.close();
			writer.close();
			socket.close();
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}

	private String getClientId(Socket socket) {
		String clientId = socket.getInetAddress().getHostAddress();
		clientId += ":" + socket.getPort();

		return clientId;
	}

}
