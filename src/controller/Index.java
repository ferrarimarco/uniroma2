package controller;

public class Index {

	public static void main(String[] args) {

		ConnectionListener pop3Server = new ConnectionListener(110, 10);
		ConnectionListener smtpServer = new ConnectionListener(25, 10);
		
		Thread pop3Thread = new Thread(pop3Server);
		pop3Thread.start();
		
		Thread smtpThread = new Thread(smtpServer);
		smtpThread.start();
	}
}
