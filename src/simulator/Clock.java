package simulator;

public class Clock {
	
	private double wallTimeStart, wallTimeStop;
	private double simTime;
	
	public Clock()
	{
		this.wallTimeStart = System.currentTimeMillis();
		setSimTime(0.0);
		
	}


	public void advance(double deltaT)
	{
	this.simTime += deltaT;	
	}
	
	public double getSimTime()
	{
		return this.simTime;
	}
	
	public void setSimTime(double simTime)
	{
		this.simTime = simTime;	
	}
	
	public void stopTheClock()
	{
		this.wallTimeStop = System.currentTimeMillis();
	}
	
	public double getWallTimeDuration()
	{
		stopTheClock();
		return this.wallTimeStop - this.wallTimeStart;
	}
	
}
	