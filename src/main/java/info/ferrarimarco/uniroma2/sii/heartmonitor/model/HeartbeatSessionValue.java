package info.ferrarimarco.uniroma2.sii.heartmonitor.model;


public class HeartbeatSessionValue {
	
	private int bpm;
	private int ibi;
	
	public HeartbeatSessionValue(int bpm, int ibi) {
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
