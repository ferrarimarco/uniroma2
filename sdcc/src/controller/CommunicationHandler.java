package controller;

import java.io.BufferedOutputStream;
import java.util.List;

public interface CommunicationHandler {
	void sendResponse(BufferedOutputStream writer, String statusIndicator, String response);

	void sendString(BufferedOutputStream writer, String string);

	void sendListAsMultiLineResponse(BufferedOutputStream writer, List<String> list);

	void sendBlankLineMultilineEnd(BufferedOutputStream writer);

	void sendStringWithLinesLimit(BufferedOutputStream writer, String string, int linesLimit);
	
	void sendLine(BufferedOutputStream writer, String msg, boolean multiLine, boolean lastLine);
}
