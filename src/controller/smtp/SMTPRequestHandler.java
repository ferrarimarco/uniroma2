package controller.smtp;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import controller.AbstractRequestHandler;
import controller.CommandHandler;
import controller.CommunicationHandler;
import controller.persistance.FieldName;
import controller.persistance.PersistanceManager;
import controller.persistance.StorageLocation;
import controller.persistance.StorageManager;

public class SMTPRequestHandler extends AbstractRequestHandler {

	
	@Override
	public void handleRequest(Socket socket, CommandHandler commandHandler, CommunicationHandler communicationHandler) {
		
		PersistanceManager storageManager = new StorageManager();
		
		// TODO: the client uses multiple ports!!! so the port section may change!!!!
		
		String clientId = socket.getInetAddress().getHostAddress();
		// Temp solution: comment the port. This will not work if multiple users share the same public ip
		//clientId += ":" + socket.getPort();
		
		// TODO: DEBUG
		System.out.println("Connection received from " + clientId);
		
		// Get current status
		SMTPSessionStatus currentStatus = SMTPSessionStatus.parseStatus(storageManager.read(StorageLocation.SMTP_SESSIONS, FieldName.SMTP_SESSION_STATUS, clientId));
		
		if(currentStatus.equals(SMTPSessionStatus.TRANSACTION_DATA)){

			try {
				// Get Input and Output streams
				BufferedOutputStream writer = new BufferedOutputStream(socket.getOutputStream());
				writer.flush();

				BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				
				String data;

				while ((data = reader.readLine()) != null) {
					System.out.println("Server receives: " + data);
					commandHandler.processMessageData(communicationHandler, writer, storageManager, clientId, data);
				}	

				// Done handling data
				super.stop(reader, writer);

			} catch (IOException e) {
				e.printStackTrace();
			}
		}else{
			super.handleRequest(socket, commandHandler, communicationHandler);
		}
	}
}
