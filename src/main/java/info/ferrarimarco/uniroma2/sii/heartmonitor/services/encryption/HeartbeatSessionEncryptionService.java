package info.ferrarimarco.uniroma2.sii.heartmonitor.services.encryption;

import info.ferrarimarco.uniroma2.sii.heartmonitor.model.HeartbeatSession;

import org.springframework.stereotype.Service;

@Service
public class HeartbeatSessionEncryptionService {

	public String encryptHeartbeatSession(HeartbeatSession session){
		return "ENCRYPTED_HB_SESSION";
	}
	
	public HeartbeatSession decryptHeartbeatSession(String encryptedHeartbeatSession){
		return new HeartbeatSession("");
	}
	
	public String encryptHeartbeatSessionValue(String value){
		return "ENCRYPTED_HB_S_VALUE";
	}
	
	public String decryptHeartbeatSessionValue(String encryptedValue){
		return "DECRYPTED_HB_S_VALUE";
	}
	
	public String encryptHeartbeatSessionId(String value){
		return "ENCRYPTED_HB_S_ID";
	}
	
	public String decryptHeartbeatSessionId(String encryptedValue){
		return "DECRYPTED_HB_S_ID";
	}
	
}
