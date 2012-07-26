package controller;

public class Index {


	public static void main(String[] args) {

		POP3RequestHandler pop3requestHandler = new POP3RequestHandler();
		
		ConnectionListener testServer = new ConnectionListener(110, 10, pop3requestHandler);
		
		while (true) {
			testServer.run();
		}
	}

}
