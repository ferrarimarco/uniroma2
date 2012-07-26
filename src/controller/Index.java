package controller;

public class Index {


	public static void main(String[] args) {

		ConnectionListener testServer = new ConnectionListener(110, 10, null);
		
		while (true) {
			testServer.run();
		}
	}

}
