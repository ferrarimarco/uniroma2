package controller.pop3;

import java.net.Socket;

import controller.AbstractRequestHandler;
import controller.CommandHandler;
import controller.CommunicationHandler;

public class POP3RequestHandler extends AbstractRequestHandler {

	public POP3RequestHandler(Socket socket, CommandHandler commandHandler, CommunicationHandler communicationHandler) {
		super(socket, commandHandler, communicationHandler);
	}

}
