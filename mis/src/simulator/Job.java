package simulator;

import java.io.Serializable;

public class Job implements Serializable {
	
	private Integer jobClass;
	private Integer identifier;
	private Boolean foundData;

	//Tempo di uscita da terminale
	private Double termExitTime;
	
	public Job(Integer jobClass, Integer identifier)
	{
		this.jobClass = jobClass;
		this.identifier = identifier;
		this.termExitTime = 0.0;
		this.foundData = false;
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

	public void setTermExitTime(Double termExitTime) {
		this.termExitTime = termExitTime;
	}

	public Double getTermExitTime() {
		return termExitTime;
	}

	public Boolean getFoundData() {
		return foundData;
	}

	public void setFoundData(Boolean foundData) {
		this.foundData = foundData;
	}
}
