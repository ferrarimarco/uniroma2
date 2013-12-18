package info.ferrarimarco.uniroma2.sii.heartmonitor.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "latestValues")
public class HeartbeatSessionValue {
	
	private String referringSessionId;
	
	@Id
	private int sequenceNumber;
	
	private int bpm;
	private int ibi;
	
	public HeartbeatSessionValue() {}
	
	public HeartbeatSessionValue(int sequenceNumber, String referringSessionId, int bpm, int ibi) {
		this.referringSessionId = referringSessionId;
		this.setSequenceNumber(sequenceNumber);
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

	@Override
	public String toString() {
		return "HeartbeatSessionValue [getBpm()=" + getBpm() + ", getIbi()=" + getIbi() + "]";
	}
}
