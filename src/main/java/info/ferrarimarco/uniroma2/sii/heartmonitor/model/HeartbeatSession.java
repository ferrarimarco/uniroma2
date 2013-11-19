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
	
	private String user;
	
	private List<Integer> values;

	public HeartbeatSession(DateTime time) {
		this.time = time;
		
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
	}

	
	public DateTime getTime() {
		return time;
	}


	public void setTime(DateTime time) {
		this.time = time;
	}


	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public List<Integer> getValues() {
		return values;
	}

	
	public void setValues(List<Integer> values) {
		this.values = values;
	}

	@Override
	public String toString() {
		return "HeartbeatSession [id=" + id + ", time=" + time + ", user=" + user + "]";
	}
}
