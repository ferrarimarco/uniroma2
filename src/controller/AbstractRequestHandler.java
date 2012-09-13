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

		String clientId = getClientId(socket);

		// TODO: DEBUG
		System.out.println("Connection received from " + clientId + " on port " + socket.getLocalPort());

		PersistanceManager storageManager = new StorageManager();
		
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
			String[] commandElements;

			while ((message = reader.readLine()) != null) {
				System.out.println("Server receives: " + message);

				// Initialize
				command = "";
				argument = "";
				secondArgument = "";

				commandElements = message.split("\\s+");

				command = commandElements[0];

				if (commandElements.length > 1) {
					argument = commandElements[1];
				}

				// Check if there is a second argument
				if (commandElements.length > 2) {
					secondArgument = commandElements[2];
				}

				// TODO: DEBUG
				// System.out.println(java.util.Arrays.toString(commandElements));

				commandHandler.handleCommand(communicationHandler, writer, message, command, argument, secondArgument, storageManager, clientId);
			}

			// Done handling the command
			stop(reader, writer);

		} catch (IOException e) {
			e.printStackTrace();
			
			// Clear session status
			commandHandler.clearStatus(storageManager, clientId);
		}
	}

	protected void stop(BufferedReader reader, BufferedOutputStream writer) {
		try {
			reader.close();
			writer.close();
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}

	protected String getClientId(Socket socket) {
		// TODO: the client uses multiple ports!!! so the port section may
		// change!!!!

		String clientId = socket.getInetAddress().getHostAddress();
		// Temp solution: comment the port. This will not work if multiple users
		// share the same public ip
		// clientId += ":" + socket.getPort();

		return clientId;
	}

}
