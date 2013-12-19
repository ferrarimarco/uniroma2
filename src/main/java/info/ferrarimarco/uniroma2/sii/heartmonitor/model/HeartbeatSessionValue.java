package info.ferrarimarco.uniroma2.sii.heartmonitor.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "latestValues")
public class HeartbeatSessionValue {

	@Id
	private String id;
	
	private String referringSessionId;
	private String referringUserName;
	private int sequenceNumber;
	private int bpm;
	private int ibi;
	
	public HeartbeatSessionValue() {}
	
	public HeartbeatSessionValue(int sequenceNumber, String referringSessionId, String referringUserName, int bpm, int ibi) {
		this.referringSessionId = referringSessionId;
		this.referringUserName = referringUserName;
		this.sequenceNumber = sequenceNumber;
		
		id = referringSessionId + sequenceNumber;
		
		this.bpm = bpm;
		this.ibi = ibi;
	}
	
	public String getReferringSessionId() {
		return referringSessionId;
	}
	
	public void setReferringSessionId(String referringSessionId) {
		this.referringSessionId = referringSessionId;
	}

	public int getBpm() {
		return bpm;
	}
	
	public void setBpm(int bpm) {
		this.bpm = bpm;
	}
	
	public int getIbi() {
		return ibi;
	}
	
	public void setIbi(int ibi) {
		this.ibi = ibi;
	}

	public int getSequenceNumber() {
		return sequenceNumber;
	}

	public void setSequenceNumber(int sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	public String getReferringUserName() {
		return referringUserName;
	}

	public void setReferringUserName(String referringUserName) {
		this.referringUserName = referringUserName;
	}

	@Override
	public String toString() {
		return "HeartbeatSessionValue [id=" + id + ", referringSessionId=" + referringSessionId + ", referringUserName=" + referringUserName + ", sequenceNumber=" + sequenceNumber + ", bpm=" + bpm + ", ibi=" + ibi + "]";
	}
}
