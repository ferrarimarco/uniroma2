package info.ferrarimarco.uniroma2.sii.heartmonitor.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "currentHeartbeatSessions")
public class CurrentHeartbeatSession {
	
	@Id
	private String userName;
	
	private String currentSessionId;
	
	private boolean requested;

	public CurrentHeartbeatSession() {}
	
	public CurrentHeartbeatSession(String userName, String currentSessionId) {
		this.userName = userName;
		this.currentSessionId = currentSessionId;
		setRequested(false);
	}

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

	public boolean isRequested() {
		return requested;
	}

	public void setRequested(boolean requested) {
		this.requested = requested;
	}

	@Override
	public String toString() {
		return "CurrentHeartbeatSession [userName=" + userName
				+ ", currentSessionId=" + currentSessionId + "]";
	}
}
