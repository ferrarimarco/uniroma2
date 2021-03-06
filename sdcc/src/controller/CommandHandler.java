package controller;

import java.io.BufferedOutputStream;

import controller.persistance.PersistanceManager;

public interface CommandHandler {

	void handleCommand(CommunicationHandler communicationHandler, BufferedOutputStream writer, String line, String command, String argument, String secondArgument,
			PersistanceManager persistanceManager, String clientId);

	void sendGreetings(CommunicationHandler communicationHandler, BufferedOutputStream writer, PersistanceManager persistanceManager, String clientId);
	
	void clearStatus(PersistanceManager persistanceManager, String clientId);
	
	void sendAbnormalTerminationResponse(CommunicationHandler communicationHandler, BufferedOutputStream writer);
}
