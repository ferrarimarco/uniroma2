package simulator;

public class Job {
	
	private Integer jobClass;
	private Integer identifier;
	
	public Job(Integer jobClass, Integer identifier)
	{
		this.jobClass = jobClass;
		this.identifier = identifier;
	}

	public int getJobClass() {
		return jobClass;
	}

	public void setJobClass(int jobClass) {
		this.jobClass = jobClass;
	}

	public Integer getIdentifier() {
		return identifier;
	}
}
