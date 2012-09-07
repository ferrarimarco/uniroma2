package controller;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;

public abstract class AbstractRequestHandler implements RequestHandler {

	@Override
	public abstract void handleRequest(Socket socket);
	
	protected void stop(BufferedReader reader, BufferedOutputStream writer){
		try {
			reader.close();
			writer.close();
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}
	
}
