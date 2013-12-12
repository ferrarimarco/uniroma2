package info.ferrarimarco.uniroma2.sii.heartmonitor.model;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "heartbeatSessions")
public class HeartbeatSession {

	@Id
	private String id;
	
	private DateTime time;
	
	private String userId;
	
	private List<HeartbeatSessionValue> values;
	
	private boolean isClosed;
	
	private short expectedSequenceNumber;

	public HeartbeatSession(String userId) {
		this.userId = userId;
		this.time = new DateTime();
		this.expectedSequenceNumber = 0;
		
		values = new ArrayList<>();
	}

	public void addValue(Integer bpm, Integer ibi){
		values.add(new HeartbeatSessionValue(bpm, ibi));
		expectedSequenceNumber++;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public DateTime getTime() {
		return time;
	}

	public void setTime(DateTime time) {
		this.time = time;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public List<HeartbeatSessionValue> getValues() {
		return values;
	}
	
	public void setValues(List<HeartbeatSessionValue> values) {
		this.values = values;
	}

	public boolean isClosed() {
		return isClosed;
	}

	public void setClosed(boolean isClosed) {
		this.isClosed = isClosed;
	}

	public short getExpectedSequenceNumber() {
		return expectedSequenceNumber;
	}

	@Override
	public String toString() {
		return "HeartbeatSession [id=" + id + ", time=" + time + ", userId=" + userId + "]";
	}
}
