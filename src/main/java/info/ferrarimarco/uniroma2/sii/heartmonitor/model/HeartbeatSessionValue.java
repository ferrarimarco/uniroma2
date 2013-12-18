package info.ferrarimarco.uniroma2.sii.heartmonitor.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "latestValues")
public class HeartbeatSessionValue {
	
	@Id
	private String referringSessionId;
	
	private int bpm;
	private int ibi;
	
	public HeartbeatSessionValue() {}
	
	public HeartbeatSessionValue(String referringSessionId, int bpm, int ibi) {
		this.referringSessionId = referringSessionId;
		this.bpm = bpm;
		this.ibi = ibi;
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

	@Override
	public String toString() {
		return "HeartbeatSessionValue [getBpm()=" + getBpm() + ", getIbi()=" + getIbi() + "]";
	}
}
