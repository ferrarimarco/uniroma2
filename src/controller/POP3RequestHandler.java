package controller;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class POP3RequestHandler implements RequestHandler {
	
	// This variable is needed only because we don't have db access, yet
	private POP3Status status = POP3Status.UNKNOWN;
	
	private POP3CommandHandler pop3CommandHandler;
	private POP3CommunicationHandler pop3CommunicationHandler;
	
	public POP3RequestHandler() {
		pop3CommandHandler = new POP3CommandHandler();
		pop3CommunicationHandler = new POP3CommunicationHandler();
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
			if(getStatus().equals(POP3Status.GREETINGS)){
				pop3CommunicationHandler.sendResponse(writer, POP3StatusIndicator.OK, "POP3 server ready to roll!");
				setStatus(POP3Status.AUTHORIZATION);
			}
			
			String message = "";
			String command = "";
			String argument = "";
			String [] commandElements;
			
			while ((message = reader.readLine()) != null) {
				System.out.println("server riceve:" + message);
				
				commandElements = message.split("\\s+");
				
				command = commandElements[0];
				
				if(commandElements.length > 1){
					argument = commandElements[1];
				}
				
				// DEBUG
				System.out.println(java.util.Arrays.toString(commandElements));
				
				handleCommand(writer, command, argument);
			}

			// TODO: Done handling command
			//stop(reader, writer, socket);
			setStatus(POP3Status.UNKNOWN);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void stop(BufferedReader reader, BufferedOutputStream writer, Socket socket){

		// Close connection
		try {
			reader.close();
			writer.close();
			socket.close();
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}
	
	private void handleCommand(BufferedOutputStream writer, String command, String argument){
		
		// To hold the POP3Status to eventually set after the command
		POP3Status resultingStatus = POP3Status.UNKNOWN;
		
		switch(command){
			case "CAPA":
				resultingStatus = pop3CommandHandler.CAPACommand(writer, getStatus());
				break;
			case "capa":
				resultingStatus = pop3CommandHandler.CAPACommand(writer, getStatus());
				break;
			case "QUIT":
				resultingStatus = pop3CommandHandler.QUITCommand(writer, getStatus());
				break;
			case "quit":
				resultingStatus = pop3CommandHandler.QUITCommand(writer, getStatus());
				break;
			case "USER":
				resultingStatus = pop3CommandHandler.USERCommand(writer, getStatus(), argument);
			case "user":
				resultingStatus = pop3CommandHandler.USERCommand(writer, getStatus(), argument);
			default:
				pop3CommunicationHandler.sendResponse(writer, POP3StatusIndicator.ERR, "Command is not supported");
				break;	
		}
		
		// Set the status after the command according to the POP3 protocol specification
		setStatus(resultingStatus);
	}

	private POP3Status getStatus(){
		
		// TODO: Get status from DB (this code is just a placeholder for DB access)
		if(status.equals(POP3Status.UNKNOWN)){
			setStatus(POP3Status.GREETINGS);
		}
		
		return status;
	}

	private void setStatus(POP3Status status){

		// TODO: Write status in DB
		// TODO: read the status and write only if different?
		
		this.status = status;
	}
}
