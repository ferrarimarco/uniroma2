package controller;
import java.io.*;
import java.net.*;

public class ConnectionListener {

	ServerSocket providerSocket;
	Socket connection;
	
	BufferedWriter writer;
	BufferedReader reader;
	
	String message;
	
	RequestHandler requestHandler;

	ConnectionListener(int portNumber, int backlog, RequestHandler requestHandler) {
		
		try {
			// 1. creating a server socket
			providerSocket = new ServerSocket(110, 10);
			
		} catch (IOException e) {
			e.printStackTrace();
		}

		this.requestHandler = requestHandler;
	}

	void run() {
		try {
			
			// 2. Wait for connection
			System.out.println("Waiting for connection");

			connection = providerSocket.accept();

			System.out.println("Connection received from " + connection.getInetAddress().getHostName());

			// 3. get Input and Output streams
			writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
			writer.flush();
			
			reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			
			sendMessage("Connection successful");
			
			// 4. The two parts communicate via the input and output streams
			while ((message = reader.readLine()) != null) {
				
				System.out.println("server riceve: " + message);
				
			}

		} catch (IOException ioException) {
			
			ioException.printStackTrace();
			
		}
	}

	void sendMessage(String msg) {
		try {
			writer.write(msg);
			writer.flush();
			System.out.println("server invia: " + msg);
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}
	
	void stop(){
		// 4: Closing connection
		try {
			reader.close();
			writer.close();
			providerSocket.close();
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}
}