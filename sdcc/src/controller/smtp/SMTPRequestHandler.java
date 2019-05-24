package controller.smtp;

import java.net.Socket;

import controller.AbstractRequestHandler;
import controller.CommandHandler;
import controller.CommunicationHandler;

public class SMTPRequestHandler extends AbstractRequestHandler {

	public SMTPRequestHandler(Socket socket, CommandHandler commandHandler, CommunicationHandler communicationHandler) {
		super(socket, commandHandler, communicationHandler);
	}

}
