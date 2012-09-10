package controller;

import java.io.BufferedOutputStream;

import controller.persistance.PersistanceManager;

public abstract class AbstractCommandHandler implements CommandHandler {

	@Override
	public abstract void handleCommand(CommunicationHandler communicationHandler, BufferedOutputStream writer, String command, String argument, String secondArgument, PersistanceManager persistanceManager, String clientId);

	@Override
	public abstract void sendGreetings(CommunicationHandler communicationHandler, BufferedOutputStream writer, PersistanceManager persistanceManager, String clientId);

	@Override
	public abstract void processMessageData(CommunicationHandler communicationHandler, BufferedOutputStream writer, PersistanceManager persistanceManager, String clientId, String data);
}
