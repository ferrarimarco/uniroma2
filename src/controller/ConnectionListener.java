package controller;

import java.io.*;
import java.net.*;

public class ConnectionListener {

	private ServerSocket serverSocket;
	private Socket connection;
	
	private RequestHandler requestHandler;
	
	public ConnectionListener(int portNumber, int backlog, RequestHandler requestHandler) {
		
		try {
			serverSocket = new ServerSocket(portNumber, backlog);
		} catch (IOException e) {
			e.printStackTrace();
		}

		this.requestHandler = requestHandler;
	}

	public void run() {
		try {
			
			// Wait for connection
			System.out.println("Waiting for connection on port " + serverSocket.getLocalPort());

			connection = serverSocket.accept();

			requestHandler.handleRequest(connection);
			
			connection.close();

		} catch (IOException ioException) {
			ioException.printStackTrace();			
		}
	}

	public void stop(){
		try {
			serverSocket.close();
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}
}