package controller;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import controller.pop3.POP3CommandHandler;
import controller.pop3.POP3CommunicationHandler;
import controller.pop3.POP3RequestHandler;
import controller.smtp.SMTPCommandHandler;
import controller.smtp.SMTPRequestHandler;

public class ConnectionListener implements Runnable {

	private ServerSocket serverSocket;
	private static final int POP3_PORT = 110;
	private static final int SMTP_PORT = 25;
	
	public ConnectionListener(int portNumber, int backlog) {
		
		try {
			serverSocket = new ServerSocket(portNumber, backlog);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		try {
			
			// Wait for connection
			System.out.println("Waiting for connection on port " + serverSocket.getLocalPort());

			RequestHandler requestHandler = null;
			
			if(serverSocket.getLocalPort() == POP3_PORT){
				requestHandler = new POP3RequestHandler();
			}else if(serverSocket.getLocalPort() == SMTP_PORT){
				requestHandler = new SMTPRequestHandler();
			}
			
			Socket connection;
			
			while(true){
				connection = serverSocket.accept();

				if(serverSocket.getLocalPort() == POP3_PORT){
					requestHandler.handleRequest(connection, new POP3CommandHandler(), new POP3CommunicationHandler());
				}else if(serverSocket.getLocalPort() == SMTP_PORT){
					requestHandler.handleRequest(connection, new SMTPCommandHandler(), new POP3CommunicationHandler());
				}

				connection.close();
			}

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