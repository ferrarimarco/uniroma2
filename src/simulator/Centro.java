package simulator;

import java.util.LinkedList;

public class Centro {

	private String name;
	private TipoCentro type;
	private LinkedList<Job> queue;
	private Boolean isFree;
	private Job currentJob;
	private TipoEvento t;
	
	private Job tempJob;
	
	//Per contare i Job uscenti
	private Integer jobOut;
	
	public Centro(String name, TipoCentro type, LinkedList<Job> queue){
		this.name = name;
		this.type = type;
		this.queue = queue;
		isFree = false;
	}
	
	//Get first Job and remove from Queue
	public Job popJobFromQueue(){
		return queue.pop();
	}
	
	public void addJobToQueueLast(Job j){
		queue.add(j);
	}
	
	public Job getJobFromQueueAndRemove(Integer index){
		tempJob = queue.get(index);
		queue.remove(index);
		return tempJob;
	}

	public Boolean getIsFree() {
		return isFree;
	}

	public void setIsFree(Boolean isFree) {
		this.isFree = isFree;
	}

	public Job getCurrentJob() {
		return currentJob;
	}

	public void setCurrentJob(Job currentJob) {
		this.currentJob = currentJob;
	}

	public void setJobOut(Integer jobOut) {
		this.jobOut = jobOut;
	}

	public Integer getJobOut() {
		return jobOut;
	}
}
