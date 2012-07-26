package controller;
import java.io.*;
import java.net.*;

public class ConnectionListener {

	private ServerSocket serverSocket;
	private Socket connection;
	
	private RequestHandler requestHandler;

	// Un thread per ogni richiesta
	// Listener tiene traccia dei thread ed a quale client stanno parlando
	// Listener inoltra le richieste del client al thread corretto
	// Thread termina quando riceve comando POP3 che chiude sessione
	// Devo registrare nel DB lo stato della connessione, così tutti i nodi diventano idempotenti.
	// Ciascun nodo deve completamente rispondere ad una richiesta del client.
	
	public ConnectionListener(int portNumber, int backlog, RequestHandler requestHandler) {
		
		try {
			serverSocket = new ServerSocket(110, 10);
			
		} catch (IOException e) {
			e.printStackTrace();
		}

		this.requestHandler = requestHandler;
	}

	public void run() {
		try {
			
			// Wait for connection
			System.out.println("Waiting for connection");

			connection = serverSocket.accept();

			requestHandler.handleRequest(connection);

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