package model;

import java.util.ArrayList;
import java.util.List;

import controller.AbstractRequestHandler;
import controller.SpecialCharactersSequence;
import controller.net.Domain;

public class Message {

	private String header;
	private String body;
	private String uid;
	private int size;
	private List<String> toAddresses;
	private String fromAddress;
	
	public void setToAddresses(List<String> toAddresses) {
		this.toAddresses = toAddresses;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public void setReady(boolean isReady) {
		this.isReady = isReady;
	}

	private String rawData;
	private boolean isReady;
	private List<String> toUsers;
	
	public Message(){
		toAddresses = new ArrayList<String>();
		rawData = "";
		
		isReady = false;
		
		toUsers = new ArrayList<String>();
	}
	
	public void setRawData(String newData){
		rawData = newData;
	}
	
	public String getRawData(){
		return rawData;
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
	
	public String getMessageSize(){
		if(isReady){
			return Integer.toString(size);
		}else{
			processMessage();
			return getMessageSize();
		}
	}
	
	public void setMessageSize(String size){
		this.size = Integer.parseInt(size);
	}
	
	public List<String> getToUsers(){
		if(isReady){
			return toUsers;
		}else{
			processMessage();
			return getToUsers();
		}		
	}
	
	public List<String> getToAddresses(){
		return toAddresses;
	}	
	
	private void processMessage(){
		
		String messageData = rawData;

		// TODO: make this search dynamic
		
		// TODO: DEBUG
		// TODO: print rawData and debug indexes when there is an email that contains HTML tags
		AbstractRequestHandler.log.info("----- BEGIN MESSAGE DEBUG -----");
		
		AbstractRequestHandler.log.info("Rawdata: " + rawData);
		
		// Search for Message-ID:<
		int startIndexId = messageData.indexOf("Message-ID:<");
		AbstractRequestHandler.log.info("startIndexId (searching for Message-ID:): " + startIndexId);
		
		int offset = 12;
		
		// Try with Message-Id:<
		if(startIndexId == -1) {
			startIndexId = messageData.indexOf("Message-Id:<");
			AbstractRequestHandler.log.info("startIndexId (searching for Message-Id:): " + startIndexId);	
		}
		
		// Try with Message-ID: <
		if(startIndexId == -1) {
			startIndexId = messageData.indexOf("Message-ID: <");
			offset++;
			
			AbstractRequestHandler.log.info("Message-ID:< not found. Switching to Message-ID: <: " + startIndexId);
		}
		
		// Try with Message-Id: <
		if(startIndexId == -1) {
			startIndexId = messageData.indexOf("Message-Id: <");
			offset++;
			
			AbstractRequestHandler.log.info("Message-Id:< not found. Switching to Message-Id: <: " + startIndexId);
		}
		
		int endIndexId = messageData.indexOf(">", startIndexId);
		AbstractRequestHandler.log.info("endIndexId: " + endIndexId);
		
		uid = messageData.substring(startIndexId + offset, endIndexId);
		AbstractRequestHandler.log.info("uid: " + uid);
		
		int startIndexHeader = 0;
		int endIndexHeader = messageData.indexOf(SpecialCharactersSequence.LINE_END.toString() + SpecialCharactersSequence.LINE_END.toString());
		AbstractRequestHandler.log.info("endIndexHeader: " + endIndexHeader);
		
		header = messageData.substring(startIndexHeader, endIndexHeader);

		AbstractRequestHandler.log.info("HEADER: " + header);
		
		// TODO: delete the final CRLF.CRLF
		body = messageData.substring(endIndexHeader + 4, messageData.length() - 2);

		AbstractRequestHandler.log.info("BODY: " + body);
		
		// TODO: check \r\n..\r\n.\r\n 
		// Handle byte stuffing in body
		body = body.replace(SpecialCharactersSequence.LINE_END + "..", SpecialCharactersSequence.LINE_END + ".");
		
		int messageSize = (header + SpecialCharactersSequence.LINE_END.toString() + body).length();
		size = messageSize;

		// Get users list to deliver the message
		for (int i = 0; i < toAddresses.size(); i++) {

			int startIndexUsers = 0;
			int endIndexUsers = toAddresses.get(i).indexOf("@");
			
			// Search for < character (Name <email@domain.ext>)
			if (toAddresses.get(i).indexOf("<") != -1) {
				startIndexUsers = toAddresses.get(i).indexOf("<") + 1;
			}
			
			int startDomainIndex = toAddresses.get(i).indexOf("@") + 1;
			int endDomainIndex = toAddresses.get(i).length();
			
			if (toAddresses.get(i).indexOf(">") != -1) {
				endDomainIndex = toAddresses.get(i).indexOf(">");
			}
			
			// Check if the address is one of our addresses
			String domain = toAddresses.get(i).substring(startDomainIndex, endDomainIndex);
			
			if(Domain.parseDomain(domain).equals(Domain.LOCAL_HOST)){
				toUsers.add(toAddresses.get(i).substring(startIndexUsers, endIndexUsers));
				toAddresses.remove(i);
			}
		}
		
		isReady = true;
	}

	public String getFromAddress() {
		return fromAddress;
	}

	public void setFromAddress(String fromAddress) {
		this.fromAddress = fromAddress;
	}
}
