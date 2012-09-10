package controller.smtp;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.List;

import controller.AbstractCommunicationHandler;

public class SMTPCommunicationHandler extends AbstractCommunicationHandler {

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

	@Override
	public void sendResponse(BufferedOutputStream writer, String statusIndicator, String response) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendString(BufferedOutputStream writer, String string) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendListAsMultiLineResponse(BufferedOutputStream writer, List<String> list) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendBlankLineMultilineEnd(BufferedOutputStream writer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendStringWithLinesLimit(BufferedOutputStream writer, String string, int linesLimit) {
		// TODO Auto-generated method stub
		
	}
	
}
