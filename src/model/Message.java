package model;

import java.util.ArrayList;
import java.util.List;

import controller.SpecialCharactersSequence;

public class Message {

	private String header;
	private String body;
	private String uid;
	private int size;
	private List<String> toAddresses;
	
	private String rawData;
	private boolean isReady;
	private List<String> toUsers;
	
	public Message(){
		toAddresses = new ArrayList<String>();
		rawData = "";
		
		isReady = false;
		
		toUsers = new ArrayList<String>();
	}
	
	public void addRawData(String newData){
		rawData += newData;
	}
	
	public String getHeader(){
		if(isReady){
			return header;
		}else{
			processMessage();
			return getHeader();
		}
	}
	
	public String getBody(){
		if(isReady){
			return body;
		}else{
			processMessage();
			return getBody();
		}
	}
	
	public String getUID(){
		if(isReady){
			return uid;
		}else{
			processMessage();
			return getUID();
		}
	}
	
	public void addToAddress(String newUser){
		toAddresses.add(newUser);
	}
	
	public int getMessageSize(){
		if(isReady){
			return size;
		}else{
			processMessage();
			return getMessageSize();
		}
	}
	
	public List<String> getToUsers(){
		if(isReady){
			return toUsers;
		}else{
			processMessage();
			return getToUsers();
		}		
	}
	
	private void processMessage(){
		
		String messageData = rawData;

		// Search for Message-ID
		int startIndexId = messageData.indexOf("Message-ID:<");
		int endIndexId = messageData.indexOf(">", startIndexId);
		uid = messageData.substring(startIndexId + 14, endIndexId);

		int startIndexHeader = 0;
		int endIndexHeader = messageData.indexOf(SpecialCharactersSequence.LINE_END.toString() + SpecialCharactersSequence.LINE_END.toString());
		header = messageData.substring(startIndexHeader, endIndexHeader);

		body = messageData.substring(endIndexHeader + 4, messageData.length() - 2);

		// Handle byte stuffing in body
		body = body.replace(SpecialCharactersSequence.LINE_END + "..", SpecialCharactersSequence.LINE_END + ".");
		
		System.out.println("body: " + body);

		int messageSize = (header + SpecialCharactersSequence.LINE_END.toString() + body).length();
		size = messageSize;

		// Get users list to deliver the message
		//List<String> users = toUsers;

		for (int i = 0; i < toAddresses.size(); i++) {

			int startIndexUsers = 0;
			int endIndexUsers = toAddresses.get(i).indexOf("@");

			// Search for < character (Name <email@domain.ext>)
			if (toAddresses.get(i).indexOf("<") != -1) {
				startIndexUsers = toUsers.get(i).indexOf("<") + 1;
			}

			toUsers.add(toUsers.get(i).substring(startIndexUsers, endIndexUsers));
		}
		
		isReady = true;
	}
}
