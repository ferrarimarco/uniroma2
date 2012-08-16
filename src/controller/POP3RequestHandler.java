package controller;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class POP3RequestHandler implements RequestHandler {

	private BufferedOutputStream writer;
	private BufferedReader reader;
	
	private Socket socket;
	
	private POP3Status status;
	
	private final String endline = "\r\n";
	private final String terminationOctet = ".";
	
	public POP3RequestHandler() {
		status = POP3Status.UNKNOWN;
	}
	
	@Override
	public void handleRequest(Socket socket) {

		this.socket = socket;

		System.out.println("Connection received from " + socket.getInetAddress().getHostName());

		try {
			// Get Input and Output streams
			writer = new BufferedOutputStream(this.socket.getOutputStream());
			writer.flush();

			reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
			
			// Get the status of the POP3 session
			status = this.getStatus();

			if(status.equals(POP3Status.GREETINGS)){
				sendResponse(POP3StatusIndicator.OK, "POP3 server ready to roll!");
				setStatus(POP3Status.AUTHORIZATION);
			}
			
			String message = "";
			
			while ((message = reader.readLine()) != null) {
				System.out.println("server riceve:" + message);
				handleCommand(message);
			}

			stop();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void sendLine(String msg, boolean multiLine, boolean lastLine) {
		
		try {
			
			msg += endline;
			
			writer.write(msg.getBytes());
			writer.flush();
			
			System.out.println("server invia:" + msg);
			
			/* DEBUG: PRINT EXA CHAR VALUES
			for(int i = 0; i < msg.getBytes().length; i++){
				System.out.println(String.format("0x%02X", msg.getBytes()[i]));
			}
			*/
			
			if(multiLine && lastLine){
				writer.write((terminationOctet + endline).getBytes());
				writer.flush();
				System.out.println("Invio il carattere di terminazione.");
			}
			
			System.out.println("----------");
			
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}
	
	private void sendResponse(POP3StatusIndicator statusIndicator, String response){
		
		if (response.length() > 0) {
			sendLine(statusIndicator.toString() + " " + response, false, false);
		}else{
			sendLine(statusIndicator.toString(), false, false);
		}
	}
	
	public void stop(){
		// Close connection
		try {
			reader.close();
			writer.close();
			socket.close();
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}
	
	private void handleCommand(String command){
		switch(command){
			case "CAPA":
				CAPACommand();
				break;
			case "capa":
				CAPACommand();
				break;
			case "QUIT":
				QUITCommand();
				break;
			case "quit":
				QUITCommand();
				break;
			default:
				sendResponse(POP3StatusIndicator.ERR, "Command is not supported");
				break;	
		}
	}

	private void QUITCommand(){

		// TODO: unlock mailbox

		// Get the status of the POP3 session
		status = this.getStatus();

		if(status.equals(POP3Status.GREETINGS)){
			sendResponse(POP3StatusIndicator.ERR, "POP3 server is in GREETINGS status");
		}else{
			sendResponse(POP3StatusIndicator.OK, "Closing session");

			if(status.equals(POP3Status.TRANSACTION)){
				setStatus(POP3Status.UPDATE);
			}
		}
	}

	private void CAPACommand(){
		// Get the state of the POP3 session
		status = this.getStatus();

		if(status.equals(POP3Status.GREETINGS)){
			sendLine(POP3StatusIndicator.ERR + " POP3 server is in GREETINGS status", false, false);

			return;
		}else{
			sendLine(POP3StatusIndicator.OK + " Capabilities follow.", false, false);
		}

		// TODO: Send capabilities
		//sendLine(msg, true, false);
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
		
		this.status = status;
	}
}
