package simulator;

public class Clock {
	
	//Tempo reale
	private double wallTimeStart, wallTimeStop;
	
	//Tempo simulato
	private double simTime;
	
	public Clock()
	{
		this.wallTimeStart = System.currentTimeMillis();
		simTime = 0.0;
		
	}

	public void advance(double deltaT)
	{
		simTime += deltaT;	
	}
	
	public void stopTheClock()
	{
		wallTimeStop = System.currentTimeMillis();
	}
	
	public double getWallTimeDuration()
	{
		stopTheClock();
		return wallTimeStop - wallTimeStart;
	}
	
	public double getSimTime()
	{
		return simTime;
	}
	
	public void setSimTime(double simTime)
	{
		if(simTime < this.simTime){
			System.out.println("ERRORE CLOCK!!!");
			throw new RuntimeException("Errore clock.");
		}
			
		this.simTime = simTime;	
	}
	
}
	