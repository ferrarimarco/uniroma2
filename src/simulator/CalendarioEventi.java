package simulator;

public class CalendarioEventi {

	//38 eventi
	private double[] calendar;
	private TipoEvento[] tipoEvento;
	
	private int minIndex;
	private double minTime;
	
	public CalendarioEventi(){
		
		//Array contenente tutti i tipi di evento
		tipoEvento = TipoEvento.values();
		
		calendar = new double[tipoEvento.length];
	}
	
	public Event getNextEvent(){
		
		minIndex = 0;
		minTime = calendar[minIndex];
		
		for(int i = 1; i < calendar.length; i++){
			if(calendar[i] < minTime){
				minIndex = i;
				minTime = calendar[minIndex];
			}
		}
		
		//Per non ritornare indietro nel tempo
		calendar[minIndex] = Double.MAX_VALUE;
		
		return new Event(tipoEvento[minIndex] , minTime);
	}
	
	public Event getEventByType(int type){
		return new Event(tipoEvento[type], calendar[type]);
	}
	
	public void updateEvent(Event event){
		calendar[event.getEventType().getEventId()] = event.getTime();
	}

	public double[] getCalendar() {
		return calendar;
	}
}
