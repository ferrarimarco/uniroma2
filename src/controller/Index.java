package controller;

public class Index {

	public static final int POP3_PORT = 110;
	public static final int SMTP_PORT = 25;
	public static final int HEALTH_CHECK_PORT = 65000;
	
	public static void main(String[] args) {

		ConnectionListener pop3Server = new ConnectionListener(POP3_PORT, 10);
		ConnectionListener smtpServer = new ConnectionListener(SMTP_PORT, 10);
		ConnectionListener healthCheckServer = new ConnectionListener(HEALTH_CHECK_PORT, 5);

		Thread pop3Thread = new Thread(pop3Server);
		pop3Thread.start();

		Thread smtpThread = new Thread(smtpServer);
		smtpThread.start();
		
		Thread healthCheckThread = new Thread (healthCheckServer);
		healthCheckThread.start();
	}
}
