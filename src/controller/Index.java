package controller;

import controller.pop3.POP3RequestHandler;

public class Index {


	public static void main(String[] args) {

		RequestHandler pop3requestHandler = new POP3RequestHandler();
		
		ConnectionListener testServer = new ConnectionListener(110, 10, pop3requestHandler);
		
		while (true) {
			testServer.run();
		}
	}
}
