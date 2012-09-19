package controller.smtp;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.NamingException;

import model.Message;
import controller.SpecialCharactersSequence;
import controller.net.Domain;
import controller.net.MXLookup;
import controller.net.OutgoingConnection;
import controller.persistance.FieldName;
import controller.persistance.PersistanceManager;
import controller.persistance.StorageLocation;

public class SMTPMessageSender {

	public boolean verifyEmailAccountExistance(String address, PersistanceManager persistanceManager){
		
		String[] emailAddressParts = address.split("@");
		String username = emailAddressParts[0];
		String hostname = emailAddressParts[1];		
		
		// Check if the user is one of our users
		if(Domain.parseDomain(hostname).equals(Domain.LOCAL_HOST)){
			// TODO: this is good also for VRFY?
			return persistanceManager.isPresent(StorageLocation.POP3_USERS, FieldName.POP3_USER_NAME, username);
		}else{
			return verifyExternalAddress(address, hostname);
		}
	}
	
	private boolean verifyExternalAddress(String address, String hostname){
		
		System.out.println("Contacting " + hostname);
		
		String mxRecord = hostname;
		
		try {
			mxRecord = MXLookup.doMXLookup(hostname);
		} catch (NamingException e) {
			return false;
		}
		
		System.out.println("MX Record query result: " + mxRecord);
		
		OutgoingConnection out = null;
		
		try {
			out = new OutgoingConnection(mxRecord, 25);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		System.out.println(mxRecord + " says: " + out.waitForAnswer());
		
		System.out.println("Invio: " + SMTPCommand.HELO.toString() + SpecialCharactersSequence.LINE_END.toString());
		out.sendData(SMTPCommand.HELO.toString() + SpecialCharactersSequence.LINE_END.toString());
		System.out.println(mxRecord + " says: " + out.waitForAnswer());
		
		System.out.println("Invio: " + SMTPCommand.MAIL.toString() + " FROM: <" + address + ">" + SpecialCharactersSequence.LINE_END);
		out.sendData(SMTPCommand.MAIL.toString() + " FROM: <" + address + ">" + SpecialCharactersSequence.LINE_END);
		System.out.println(mxRecord + " says: " + out.waitForAnswer());
		
		System.out.println("Invio: " + SMTPCommand.RCPT.toString() + " TO: <" + address + ">" + SpecialCharactersSequence.LINE_END);
		out.sendData(SMTPCommand.RCPT.toString() + " TO: <" + address + ">" + SpecialCharactersSequence.LINE_END);
		String answer = out.waitForAnswer();
		System.out.println(mxRecord + " says: " + answer);
		
		String code = answer.substring(0, 3);
		
		boolean result = false;
		
		if(SMTPCode.parseCode(code).equals(SMTPCode.OK)){
			result = true;
		}
		
		System.out.println("Invio: " + SMTPCommand.QUIT.toString() + SpecialCharactersSequence.LINE_END);
		out.sendData(SMTPCommand.QUIT.toString() + SpecialCharactersSequence.LINE_END.toString());
		System.out.println(mxRecord + " says: " + out.waitForAnswer());
		
		// TODO: last response is not stored. Why?
		
		// Close connection and streams
		try {
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	}
	
	public String sendMessage(Message message){
		
		List<String> toAddresses = message.getToAddresses();
		
		// Get the hostnames
		List<String> hostNames = new ArrayList<String>(toAddresses.size());
		
		for(int i = 0; i < toAddresses.size(); i++){
			String[] emailAddressParts = toAddresses.get(i).split("@");
			String hostname = emailAddressParts[1];
			
			if(!hostNames.contains(hostname) && !Domain.parseDomain(hostname).equals(Domain.LOCAL_HOST)){
				hostNames.add(hostname);
			}
		}
		
		// Divide destination addresses
		Map<String, List<String>> addressesPerHostname = new HashMap<String, List<String>>();
		
		for(int i = 0; i < hostNames.size(); i++){
			
			List<String> addressesForIthHostName = new ArrayList<String>();
			
			for(int j = 0; j < toAddresses.size(); j++){
				String[] emailAddressParts = toAddresses.get(j).split("@");
				String hostname = emailAddressParts[1];
				
				if(hostNames.get(i).equals(hostname)){
					addressesForIthHostName.add(toAddresses.get(j));
				}
			}
			
			addressesPerHostname.put(hostNames.get(i), addressesForIthHostName);
		}
		
		for(int i = 0; i < addressesPerHostname.size(); i++){
			
			// Get the MX Record for this hostname
			String hostname = hostNames.get(i);
			
			System.out.println("Contacting " + hostname);
			
			String mxRecord = hostname;
			
			try {
				mxRecord = MXLookup.doMXLookup(hostname);
			} catch (NamingException e) {
				return SMTPCode.MESSAGE_UNDELIVERABLE.toString();
			}
			
			System.out.println("MX Record query result: " + mxRecord);
			
			OutgoingConnection out = null;
			
			try {
				out = new OutgoingConnection(mxRecord, 25);
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
			
			
			System.out.println(mxRecord + " says: " + out.waitForAnswer());
			
			System.out.println("Invio: " + SMTPCommand.HELO.toString() + SpecialCharactersSequence.LINE_END.toString());
			out.sendData(SMTPCommand.HELO.toString() + SpecialCharactersSequence.LINE_END.toString());
			System.out.println(mxRecord + " says: " + out.waitForAnswer());
			
			System.out.println("Invio: " + SMTPCommand.MAIL.toString() + " FROM: <" + message.getFromAddress() + ">" + SpecialCharactersSequence.LINE_END);
			out.sendData(SMTPCommand.MAIL.toString() + " FROM: <" + message.getFromAddress() + ">" + SpecialCharactersSequence.LINE_END);
			System.out.println(mxRecord + " says: " + out.waitForAnswer());
			
			for(int j = 0; j < addressesPerHostname.get(hostname).size(); j++){
				String toAddress = addressesPerHostname.get(hostname).get(j);
				
				System.out.println("Invio: " + SMTPCommand.RCPT.toString() + " TO: <" + toAddress + ">" + SpecialCharactersSequence.LINE_END);
				out.sendData(SMTPCommand.RCPT.toString() + " TO: <" + toAddress + ">" + SpecialCharactersSequence.LINE_END);
				String answer = out.waitForAnswer();
				System.out.println(mxRecord + " says: " + answer);
			}
			
			System.out.println("Invio: " + SMTPCommand.DATA.toString() + SpecialCharactersSequence.LINE_END);
			out.sendData(SMTPCommand.DATA.toString() + SpecialCharactersSequence.LINE_END);
			System.out.println(mxRecord + " says: " + out.waitForAnswer());	
			
			System.out.println("Invio raw data: " + message.getRawData());
			out.sendData(SMTPCommand.DATA.toString() + SpecialCharactersSequence.LINE_END);
			
			System.out.println("Send termination sequence.");
			out.sendData(SpecialCharactersSequence.SMTP_DATA_END.toString());
			String answer = out.waitForAnswer();
			System.out.println(mxRecord + " says: " + answer);	
			
			String code = answer.substring(0, 3);
			
			System.out.println("Invio: " + SMTPCommand.QUIT.toString() + SpecialCharactersSequence.LINE_END);
			out.sendData(SMTPCommand.QUIT.toString() + SpecialCharactersSequence.LINE_END.toString());
			System.out.println(mxRecord + " says: " + out.waitForAnswer());
			
			if(!SMTPCode.parseCode(code).equals(SMTPCode.OK)){
				return code;
			}
			
			// Close connection and streams
			try {
				out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return SMTPCode.OK.toString();
		
	}
}
