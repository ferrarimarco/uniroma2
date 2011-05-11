package simulator;

import exceptions.NoQueueCenterException;
import generators.UniformLongGenerator;

import java.util.LinkedList;

public class Centro {

	private String name;
	private TipoCentro type;
	private LinkedList<Job> queue;
	private Boolean isFree;
	private Job currentJob;	
	private Job tempJob;
	
	//Per contare i Job uscenti
	private Integer jobOut;
	
	//Per pescare job random dalla coda di disk
	private UniformLongGenerator unifGen;
	private Integer index;
	private Integer newRangeEnd;
	
	public Centro(String name, TipoCentro type, LinkedList<Job> queue){
		this.name = name;
		this.type = type;
		this.queue = queue;
		isFree = false;
		
		if(type == TipoCentro.DISK){
			unifGen = new UniformLongGenerator(0L, 0L, 103L);
		}
	}
	
	public Job getJob(){
		if(queue != null){
			if(type == TipoCentro.CPU){
				tempJob = queue.pop();
			}else if(type == TipoCentro.DISK){
				//Disciplina coda disk è RAND
				index = unifGen.generateNextValue().intValue();
				tempJob = queue.get(index);
				queue.remove(index);
				
				//Aggiornamo i ranges del generatore
				this.updateRanges();
			}
		}else{
			throw new NoQueueCenterException();
		}
		
		return tempJob;
	}
	
	public void addJobToQueue(Job j){
		queue.add(j);
		this.updateRanges();
	}

	private void updateRanges(){
		newRangeEnd = queue.size();
		unifGen.setRangeEnd(newRangeEnd.longValue());
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
