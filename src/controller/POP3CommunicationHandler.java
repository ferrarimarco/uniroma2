package controller;

import java.io.BufferedOutputStream;
import java.io.IOException;

public class POP3CommunicationHandler {

	private final String endline = "\r\n";
	private final String terminationOctet = ".";
	
	private final int maxLineLength = 0;
	
	public POP3CommunicationHandler(){
		
	}
	
	// TODO: handle a multiline message automatically
	// TODO: handle the termination octet in the message
	
	public void sendListAsMultiLineResponse(){
		
	}
	
	public void sendStringAsMultiLineResponse(){
		
	}
	
	public void sendLine(BufferedOutputStream writer, String msg, boolean multiLine, boolean lastLine) {
		
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
	
	public void sendResponse(BufferedOutputStream writer, POP3StatusIndicator statusIndicator, String response){
		
		if (response.length() > 0) {
			sendLine(writer, statusIndicator.toString() + " " + response, false, false);
		}else{
			sendLine(writer, statusIndicator.toString(), false, false);
		}
	}
	
}
