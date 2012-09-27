package controller;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.List;

public abstract class AbstractCommunicationHandler implements CommunicationHandler {

	// 512 - 2 (for endline)
	private final int maxCharsPerLinePOP3 = 510;

	private void sendLine(BufferedOutputStream writer, String msg, boolean multiLine, boolean lastLine) {

		try {

			msg += SpecialCharactersSequence.LINE_END.toString();

			System.out.println("Server invia:" + msg);

			// Check if the line starts with the termination octet.
			if (multiLine && msg.startsWith(SpecialCharactersSequence.POP3_TERMINATION_OCTET.toString())) {

				// Byte-stuff according to POP3 protocol specification
				msg = SpecialCharactersSequence.POP3_TERMINATION_OCTET.toString() + msg;
			}

			writer.write(msg.getBytes());

			/*
			 * // TODO: DEBUG: PRINT EXA CHAR VALUES for(int i = 0; i < msg.getBytes().length; i++){ System.out.println(String.format("0x%02X", msg.getBytes()[i])); }
			 */

			if (multiLine && lastLine) {
				writer.write((SpecialCharactersSequence.POP3_TERMINATION_OCTET.toString() + SpecialCharactersSequence.LINE_END.toString()).getBytes());

				System.out.println("Server invia il carattere di terminazione.");
			}

			writer.flush();

		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}

	@Override
	public void sendResponse(BufferedOutputStream writer, String statusIndicator, String response) {

		if (response.length() > 0) {
			sendLine(writer, statusIndicator + " " + response, false, false);
		} else {
			sendLine(writer, statusIndicator, false, false);
		}
	}

	@Override
	public void sendString(BufferedOutputStream writer, String string) {
		sendStringWithLinesLimit(writer, string, Integer.MAX_VALUE);
	}

	@Override
	public void sendBlankLineMultilineEnd(BufferedOutputStream writer) {
		sendLine(writer, "", true, true);
	}

	@Override
	public void sendStringWithLinesLimit(BufferedOutputStream writer, String string, int linesLimit) {

		if (string.length() <= maxCharsPerLinePOP3) {// String fits one line
			sendLine(writer, string, true, true);
		} else {// Need more than one line

			// Compute how many lines are needed
			int lines = 0;

			lines = string.length() / (maxCharsPerLinePOP3);
			if (string.length() % (maxCharsPerLinePOP3) > 0) {
				lines++;
			}

			// Check if the message fits in the lines limit specified
			if (linesLimit < lines) {
				lines = linesLimit;
			}

			for (int i = 0; i < lines; i++) {
				// TODO: check this count!
				if (i <= lines - 2) {
					sendLine(writer, string.substring(i * maxCharsPerLinePOP3, i * maxCharsPerLinePOP3 + maxCharsPerLinePOP3), true, false);
				} else {
					sendLine(writer, string.substring(i * maxCharsPerLinePOP3, string.length()), true, true);
				}
			}
		}
	}

	@Override
	public void sendListAsMultiLineResponse(BufferedOutputStream writer, List<String> list) {

		if (list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				// Check if sending the last line
				if (i < list.size() - 1) {
					sendLine(writer, list.get(i), true, false);
				} else {// Send last line
					sendLine(writer, list.get(i), true, true);
				}
			}
		}
	}

}
