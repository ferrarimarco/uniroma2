package controller.pop3;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import controller.RequestHandler;

public class POP3RequestHandler implements RequestHandler {
	
	// This variable is needed only because we don't have db access, yet
	private POP3Status status = POP3Status.UNKNOWN;
	
	private POP3CommandHandler pop3CommandHandler;
	
	public POP3RequestHandler() {
		pop3CommandHandler = new POP3CommandHandler();
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
				pop3CommandHandler.sendGreetings(writer);
				setStatus(POP3Status.AUTHORIZATION);
			}
			
			String message = "";
			String command = "";
			String argument = "";
			String secondArgument = "";
			String [] commandElements;
			
			while ((message = reader.readLine()) != null) {
				System.out.println("server riceve:" + message);
				
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
				
				handleCommand(writer, command, argument, secondArgument);
			}

			// TODO: Done handling command
			//stop(reader, writer, socket);
			
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
	
	private void handleCommand(BufferedOutputStream writer, String command, String argument, String secondArgument){
		
		// To hold the POP3Status to eventually set after the command
		POP3Status resultingStatus = POP3Status.UNKNOWN;
		
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
				pop3CommandHandler.unsupportedCommand(writer, getStatus());
				break;
		}
		
		// Set the status after the command according to the POP3 protocol specification
		if(resultingStatus != POP3Status.UNKNOWN){
			setStatus(resultingStatus);
		}
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
