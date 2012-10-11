package controller;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import controller.pop3.POP3CommandHandler;
import controller.pop3.POP3CommunicationHandler;
import controller.pop3.POP3RequestHandler;
import controller.smtp.SMTPCommandHandler;
import controller.smtp.SMTPCommunicationHandler;
import controller.smtp.SMTPRequestHandler;

public class ConnectionListener implements Runnable {

	private ServerSocket serverSocket;

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
			while (true) {
				
				AbstractRequestHandler.log.info("Waiting for connection on port " + serverSocket.getLocalPort());
				
				Socket connection = serverSocket.accept();
				
				RequestHandler requestHandler = null;

				if (serverSocket.getLocalPort() == Index.POP3_PORT) {
					requestHandler = new POP3RequestHandler(connection, new POP3CommandHandler(), new POP3CommunicationHandler());
				} else if (serverSocket.getLocalPort() == Index.SMTP_PORT) {
					requestHandler = new SMTPRequestHandler(connection, new SMTPCommandHandler(), new SMTPCommunicationHandler());
				}else if(serverSocket.getLocalPort() == Index.HEALTH_CHECK_PORT) {
					AbstractRequestHandler.log.info("Received health check on port " + serverSocket.getLocalPort());
					connection.close();
				}
				
				Thread reqHandlerT = new Thread(requestHandler);
				reqHandlerT.start();
			}

		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}

	public void stop() {
		try {
			serverSocket.close();
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}
}