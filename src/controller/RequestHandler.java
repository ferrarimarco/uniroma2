package controller;

import java.net.Socket;

public interface RequestHandler {

	void handleRequest(Socket socket);
	
}
