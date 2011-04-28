package simulator;

public class Event {

	private TipoEvento eventType;
	private Double time;
	
	public Event(TipoEvento eventType, Double time){
		this.eventType = eventType;
		this.time = time;
	}

	public TipoEvento getEventType() {
		return eventType;
	}

	public void setEventType(TipoEvento eventType) {
		this.eventType = eventType;
	}

	public Double getTime() {
		return time;
	}

	public void setTime(Double time) {
		this.time = time;
	}
}
