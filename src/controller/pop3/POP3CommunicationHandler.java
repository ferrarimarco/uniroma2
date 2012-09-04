package controller.pop3;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.List;

public class POP3CommunicationHandler {

	private final String endline = "\r\n";
	private final String terminationOctet = ".";
	
	// TODO: set this value
	private final int maxCharsPerLine = 0;

	public void sendString(BufferedOutputStream writer, String string){
		
		// TODO: handle a multiline message automatically		
	}
	
	public void sendTOPResponse(String header, String body, int lines){
		// TODO: Cut the message according to the number of lines requested
	}
	
	public void sendListAsMultiLineResponse(BufferedOutputStream writer, List<String> list){
		
		if(list.size() > 0){
			for(int i = 0; i < list.size(); i++){
				// Check if sending the last line
				if(i < list.size() - 1){
					sendLine(writer, list.get(i), true, false);
				}else{// Send last line
					sendLine(writer, list.get(i), true, true);
				}
			}
		}
	}
	
	public void sendLine(BufferedOutputStream writer, String msg, boolean multiLine, boolean lastLine) {
		
		try {
			
			msg += endline;
			
			// Check if the line starts with the termination octet.
			if(multiLine && msg.startsWith(terminationOctet)){
				
				// Byte-stuff according to POP3 protocol specification
				msg = terminationOctet + msg;
			}			
			
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
				System.out.println("Server invia il carattere di terminazione.");
			}
			
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
