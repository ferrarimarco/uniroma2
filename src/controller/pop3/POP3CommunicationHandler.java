package controller.pop3;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.List;

import controller.AbstractCommunicationHandler;

public class POP3CommunicationHandler extends AbstractCommunicationHandler {

	private static final String terminationOctet = ".";
	
	// 512 - 2 (for endline)
	private final int maxCharsPerLine = 510;
	
	@Override
	public void sendStringWithLinesLimit(BufferedOutputStream writer, String string, int linesLimit){
		
		if(string.length() <= maxCharsPerLine){// String fits one line
			sendLine(writer, string, false, false);
		}else{// Need more than one line
			
			// Compute how many lines are needed
			int lines = 0;
			
			lines = string.length() / (maxCharsPerLine);
			if(string.length() % (maxCharsPerLine) > 0){
				lines++;
			}
			
			// Check if the message fits in the lines limit specified
			if(linesLimit < lines){
				lines = linesLimit;
			}
			
			for(int i = 0; i < lines; i++){
				// TODO: check this count!
				if(i == lines - 2){
					sendString(writer, string.substring(i * maxCharsPerLine, i * maxCharsPerLine + maxCharsPerLine));
				}else{
					sendString(writer, string.substring(i * maxCharsPerLine, string.length()));
				}
			}
		}
	}
	
	@Override
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
	
	private void sendLine(BufferedOutputStream writer, String msg, boolean multiLine, boolean lastLine) {
		
		try {
			
			System.out.println("POP3 server invia:" + msg);
			
			msg += endline;
			
			// Check if the line starts with the termination octet.
			if(multiLine && msg.startsWith(terminationOctet)){
				
				// Byte-stuff according to POP3 protocol specification
				msg = terminationOctet + msg;
			}			
			
			writer.write(msg.getBytes());

			/* DEBUG: PRINT EXA CHAR VALUES
			for(int i = 0; i < msg.getBytes().length; i++){
				System.out.println(String.format("0x%02X", msg.getBytes()[i]));
			}
			*/
			
			if(multiLine && lastLine){
				writer.write((terminationOctet + endline).getBytes());
				
				System.out.println("Server invia il carattere di terminazione.");
			}
			
			writer.flush();
			
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}
	
	@Override
	public void sendBlankLineMultilineEnd(BufferedOutputStream writer){
		sendLine(writer, "", true, true);
	}
	
	@Override
	public void sendResponse(BufferedOutputStream writer, String statusIndicator, String response){
		
		if (response.length() > 0) {
			sendLine(writer, statusIndicator + " " + response, false, false);
		}else{
			sendLine(writer, statusIndicator, false, false);
		}
	}
	
}
