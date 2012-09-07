package controller;

import java.net.Socket;

public interface RequestHandler {

	void handleRequest(Socket socket, CommandHandler commandHandler, CommunicationHandler communicationHandler);
}
