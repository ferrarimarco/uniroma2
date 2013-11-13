package info.ferrarimarco.uniroma2.sii.heartmonitor.model;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.springframework.data.annotation.Id;


public class HeartbeatSession {

	@Id
	private String id;
	
	private DateTime time;
	
	private List<Integer> values;

	public HeartbeatSession(String id, DateTime time) {
		this.id = id;
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


	public List<Integer> getValues() {
		return values;
	}

	
	public void setValues(List<Integer> values) {
		this.values = values;
	}

	@Override
	public String toString() {
		return "HeatbeatSession [id=" + id + ", time=" + time + "]";
	}
}
