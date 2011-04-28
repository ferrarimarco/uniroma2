package simulator;

public class Job {
	
	private double qTime;
	private double startServiceTime;
	private double endServiceTime;
	private int jobClass;
	
	public Job(double qTime, double startServiceTime, double endServiceTime, int jobClass)
	{
		this.qTime = qTime;
		this.startServiceTime = startServiceTime;
		this.endServiceTime = endServiceTime;
		this.jobClass = jobClass;
	}

	public Job()
	{
		this(0, 0, 0, 1);
	}

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

	public int getJobClass() {
		return jobClass;
	}

	public void setJobClass(int jobClass) {
		this.jobClass = jobClass;
	}
}
