package simulator;

public class Job {
	
	private double qTime, startServiceTime, endServiceTime;
	
	public Job(double qTime, double startServiceTime, double endServiceTime)
	{
		setQTime(qTime);
		setEndServiceTime(startServiceTime);
		setEndServiceTime(endServiceTime);
	}

	public Job()
	{
		this(0, 0, 0);
	}
	
	//mutator methods
	
	public void setQTime(double qTime)
	{
	
		this.qTime = qTime;
	}
	
	public void setStartServiceTime(double startServiceTime)
	{
		this.startServiceTime = startServiceTime;
	}

	public void setEndServiceTime(double endServiceTime)
	{
		this.endServiceTime = endServiceTime;
	}
	
	public double getQTime()
	{
		return qTime;
	}
	
	public double getStartServiceTime()
	{
		return startServiceTime;
	}

	public double getEndServiceTime()
	{
		return endServiceTime;
	}
}
