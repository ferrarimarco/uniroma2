package controller.smtp;

import java.io.BufferedOutputStream;
import java.io.IOException;

public class SMTPCommunicationHandler {

	private final String endline = "\r\n";

	public void sendLine(BufferedOutputStream writer, String msg) {
		
		try {
			
			msg += endline;			
			
			writer.write(msg.getBytes());
			writer.flush();
			
			System.out.println("SMTP server invia:" + msg);
			
			/* DEBUG: PRINT EXA CHAR VALUES
			for(int i = 0; i < msg.getBytes().length; i++){
				System.out.println(String.format("0x%02X", msg.getBytes()[i]));
			}
			*/
			
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}
	
	public void sendResponse(BufferedOutputStream writer, SMTPCode code, String response){
		
		if (response.length() > 0) {
			sendLine(writer, code.toString() + " " + response);
		}else{
			sendLine(writer, code.toString());
		}
	}
	
}
