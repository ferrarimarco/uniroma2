package simulator;

public class CalendarioEventi {

	//Stabilire la posizione degli eventi (ES: dove è fineCPU?)
	private double[] calendar;
	
	public final Integer cpuIndex;
	public final Integer diskIndex;
	public final Integer firstTerminalIndex;
	public final Integer firstHostIndex;
	public final Integer firstStIndex;
	
	private Integer minIndex;
	private Double minTime;
	private Double tempTime;
	
	public CalendarioEventi(Integer numeroJob){
		
		cpuIndex = 0;
		diskIndex = 1;
		firstTerminalIndex = 2;
		firstHostIndex = firstTerminalIndex + numeroJob;
		firstStIndex = firstHostIndex + numeroJob;
			
		calendar = new double[2+ (numeroJob * 3)];
		
		for(int i = 0; i < calendar.length; i++){
			calendar[i] = Double.MAX_VALUE;
		}
	}
	
	//Return indice del prossimo evento
	public Integer getNextEventIndex(){
		
		minIndex = 0;
		minTime = calendar[minIndex];
		
		for(int i = 1; i < calendar.length; i++){
			if(calendar[i] < minTime){
				minIndex = i;
				minTime = calendar[minIndex];
			}
		}
		
		return minIndex;
	}
	
	//Return valore dell'evento calendar[index]
	public Double getEventTime(Integer index){
		
		tempTime = calendar[index]; 
		
		//Per non ritornare indietro nel tempo
		calendar[index] = Double.MAX_VALUE;
		
		return tempTime;
	}
	
	public void updateEvent(Integer index, Double time){
		calendar[index] = time;
	}

	public double[] getCalendar() {
		return calendar;
	}
}
