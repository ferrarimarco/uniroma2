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
	
	private String shortId;
	
	private DateTime time;
	
	private String userId;
	
	private List<Integer> values;
	
	private boolean isClosed;

	public HeartbeatSession(String userId) {
		this.userId = userId;
		this.time = new DateTime();
		
		values = new ArrayList<Integer>();
	}

	public void addValue(Integer value){
		values.add(value);
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
		setShortId(id.substring(0, 12));
	}
	
	public String getShortId() {
		return shortId;
	}

	public void setShortId(String shortId) {
		this.shortId = shortId;
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

	public List<Integer> getValues() {
		return values;
	}

	
	public void setValues(List<Integer> values) {
		this.values = values;
	}

	public boolean isClosed() {
		return isClosed;
	}

	public void setClosed(boolean isClosed) {
		this.isClosed = isClosed;
	}

	@Override
	public String toString() {
		return "HeartbeatSession [id=" + id + ", time=" + time + ", userId=" + userId + "]";
	}
}
