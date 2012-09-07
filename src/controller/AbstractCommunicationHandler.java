package controller;

import java.io.BufferedOutputStream;
import java.util.List;

public abstract class AbstractCommunicationHandler implements CommunicationHandler {

	@Override
	public abstract void sendResponse(BufferedOutputStream writer, String statusIndicator, String response);

	@Override
	public abstract void sendString(BufferedOutputStream writer, String string);

	@Override
	public abstract void sendListAsMultiLineResponse(BufferedOutputStream writer, List<String> list);

	@Override
	public abstract void sendBlankLineMultilineEnd(BufferedOutputStream writer);

	@Override
	public abstract void sendStringWithLinesLimit(BufferedOutputStream writer, String string, int linesLimit);

}
