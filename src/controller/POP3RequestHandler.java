package controller;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class POP3RequestHandler implements RequestHandler {

	private BufferedOutputStream writer;
	private BufferedReader reader;
	
	private String message;
	
	private Socket socket;
	
	private POP3Status status;
	
	public POP3RequestHandler() {
		status = this.getStatus();
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
			
			// Get the state of the POP3 session
			status = this.getStatus();
			
			if(status.equals(POP3Status.GREETINGS)){
				sendMessage(this.okStatusIndicator() + " POP3 server ready to roll!");
				this.setStatus(POP3Status.AUTHORIZATION);
			}
						
			while ((message = reader.readLine()) != null) {
				
				System.out.println("server riceve: " + message);
				sendMessage(this.okStatusIndicator() + " Capabilities follow.");
				sendMessage("TOP");
				sendMessage("USER");
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void sendMessage(String msg) {
		try {
			msg += "\r\n";
			
			writer.write(msg.getBytes());
			writer.flush();
			System.out.println("server invia:" + msg);
			for(int i = 0; i < msg.getBytes().length; i++){
				System.out.println(String.format("0x%02X", msg.getBytes()[i]));
			}
			System.out.println("----------");
			
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}
	
	public void stop(){
		// Closing connection
		try {
			reader.close();
			writer.close();
			socket.close();
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}
	
	private POP3Status getStatus(){
		
		// TODO: Get status from DB (status = greetings for debug purposes)
		String status = "greetings";
		
		switch(status){
			case "greetings":
				return POP3Status.GREETINGS;
			case "authorization":
				return POP3Status.AUTHORIZATION;
			case "transaction":
				return POP3Status.TRANSACTION;			
		}
		
		return POP3Status.UNKNOWN;
	}
	
	// Write in DB?
	private void setStatus(POP3Status status){

		// TODO: Write status in DB
		
		this.status = status;
	}
	
	private String okStatusIndicator(){
		return "+OK";
	}
	
	private String errorStatusIndicator(){
		return "-ERR";
	}

}
