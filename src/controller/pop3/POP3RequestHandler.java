package controller.pop3;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import controller.RequestHandler;

public class POP3RequestHandler implements RequestHandler {
	
	// This variable is needed only because we don't have db access, yet
	private POP3SessionStatus status = POP3SessionStatus.UNKNOWN;
	
	@Override
	public void handleRequest(Socket socket) {
		
		POP3CommandHandler pop3CommandHandler = new POP3CommandHandler();
		
		System.out.println("POP3 Connection received from " + socket.getInetAddress().getHostName());
		
		BufferedOutputStream writer;
		BufferedReader reader;
		
		try {
			// Get Input and Output streams
			writer = new BufferedOutputStream(socket.getOutputStream());
			writer.flush();

			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			// Check the status of the POP3 session
			if(getStatus().equals(POP3SessionStatus.GREETINGS)){
				pop3CommandHandler.sendGreetings(writer);
				setStatus(POP3SessionStatus.AUTHORIZATION);
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
				
				// DEBUG
				//System.out.println(java.util.Arrays.toString(commandElements));
				
				handleCommand(pop3CommandHandler, writer, command, argument, secondArgument);
			}

			// TODO: Done handling command
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
	
	private void handleCommand(POP3CommandHandler pop3CommandHandler, BufferedOutputStream writer, String command, String argument, String secondArgument){
		
		// To hold the POP3Status to eventually set after the command
		POP3SessionStatus resultingStatus = POP3SessionStatus.UNKNOWN;
		
		switch(command.toUpperCase()){
			case "CAPA":
				pop3CommandHandler.CAPACommand(writer, getStatus());
				break;
			case "QUIT":
				resultingStatus = pop3CommandHandler.QUITCommand(writer, getStatus());
				break;
			case "USER":
				pop3CommandHandler.USERCommand(writer, getStatus(), argument);
				break;
			case "PASS":
				resultingStatus = pop3CommandHandler.PASSCommand(writer, getStatus(), argument);
				break;
			case "STAT":
				pop3CommandHandler.STATCommand(writer, getStatus());
				break;
			case "LIST":
				pop3CommandHandler.LISTCommand(writer, getStatus(), argument);
				break;
			case "RETR":
				pop3CommandHandler.RETRCommand(writer, getStatus(), argument);
				break;
			case "DELE":
				pop3CommandHandler.DELECommand(writer, getStatus(), argument);
				break;
			case "NOOP":
				pop3CommandHandler.NOOPCommand(writer, getStatus());
				break;
			case "RSET":
				pop3CommandHandler.RSETCommand(writer, getStatus());
				break;
			case "TOP":
				pop3CommandHandler.TOPCommand(writer, getStatus(), argument, secondArgument);
				break;
			default:
				pop3CommandHandler.unsupportedCommand(writer);
				break;
		}
		
		// Set the status after the command according to the POP3 protocol specification
		if(resultingStatus != POP3SessionStatus.UNKNOWN){
			setStatus(resultingStatus);
		}
	}

	private POP3SessionStatus getStatus(){
		
		// TODO: Get status from DB (this code is just a placeholder for DB access)
		if(status.equals(POP3SessionStatus.UNKNOWN)){
			setStatus(POP3SessionStatus.GREETINGS);
		}
		
		return status;
	}

	private void setStatus(POP3SessionStatus status){

		// TODO: Write status in DB
		// TODO: read the status and write only if different?
		
		this.status = status;
	}
}
