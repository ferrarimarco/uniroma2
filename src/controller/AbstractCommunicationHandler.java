package controller;

import java.io.BufferedOutputStream;
import java.util.List;

public abstract class AbstractCommunicationHandler implements CommunicationHandler {

	protected static final String endline = "\r\n";
	
	@Override
	public abstract void sendResponse(BufferedOutputStream writer, String statusIndicator, String response);

	@Override
	public void sendString(BufferedOutputStream writer, String string){
		sendStringWithLinesLimit(writer, string, Integer.MAX_VALUE);
	}
	
	@Override
	public abstract void sendListAsMultiLineResponse(BufferedOutputStream writer, List<String> list);

	@Override
	public abstract void sendBlankLineMultilineEnd(BufferedOutputStream writer);

	@Override
	public abstract void sendStringWithLinesLimit(BufferedOutputStream writer, String string, int linesLimit);

}
