package info.ferrarimarco.uniroma2.sii.heartmonitor.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "currentHeartbeatSessions")
public class CurrentHeartbeatSession {
	
	@Id
	private String userName;
	
	private String currentSessionId;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getCurrentSessionId() {
		return currentSessionId;
	}

	public void setCurrentSessionId(String currentSessionId) {
		this.currentSessionId = currentSessionId;
	}

	@Override
	public String toString() {
		return "CurrentHeartbeatSession [userName=" + userName
				+ ", currentSessionId=" + currentSessionId + "]";
	}
}
