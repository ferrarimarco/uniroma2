package controller;

import controller.pop3.POP3RequestHandler;
import controller.smtp.SMTPRequestHandler;

public class Index {


	public static void main(String[] args) {

		RequestHandler pop3RequestHandler = new POP3RequestHandler();
		RequestHandler smtpRequestHandler = new SMTPRequestHandler();
		
		ConnectionListener pop3Server = new ConnectionListener(110, 10, pop3RequestHandler);
		ConnectionListener smtpServer = new ConnectionListener(25, 10, smtpRequestHandler);
		
		while (true) {
			pop3Server.run();
			smtpServer.run();
		}
	}
}
