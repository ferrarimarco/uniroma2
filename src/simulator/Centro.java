package simulator;

import generators.UniformDoubleGenerator;
import generators.UniformLongGenerator;

import interfaces.Generator;

import java.util.LinkedList;

public class Centro {

	private String name;
	private TipoCentro type;
	private LinkedList<Job> queue;
	private Boolean free;
	private Job currentJob;	
	private Job tempJob;
	
	//Per contare i Job uscenti
	private Integer jobOut;
	
	//Per pescare job random dalla coda di disk
	private UniformLongGenerator unifGen;
	private Integer index;
	private Integer newRangeEnd;
	
	//Per decidere se ho trovato record su disk
	private UniformDoubleGenerator isFoundGen;
	
	//Per prevedere la durata del servizio
	private Generator generator;
	private Double tempMean;
	
	public Centro(String name, TipoCentro type, LinkedList<Job> queue, Generator generator){
		this.name = name;
		this.type = type;
		this.queue = queue;
		free = true;
		
		if(type == TipoCentro.DISK){
			jobOut = 0;
			unifGen = new UniformLongGenerator(0L, 1L, 103L);
			isFoundGen = new UniformDoubleGenerator(43L);
		}
		
		this.generator = generator;
	}
	
	public Job getJobFromQueue(){
		
		tempJob = null;
		
		if(queue != null){
			if(!queue.isEmpty()){
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
			}
		}else{
			throw new RuntimeException("Centro senza coda");
		}
		
		return tempJob;
	}
	
	public void addJobToQueue(Job j){
		queue.add(j);
		
		if(type == TipoCentro.DISK)
			this.updateRanges();
	}

	private void updateRanges(){
		newRangeEnd = queue.size();
		unifGen.setRangeEnd(newRangeEnd.longValue());
	}
	
	public Number prevediDurata(Integer jobClass){
		if(type == TipoCentro.CPU){
			if(jobClass == 1){
				tempMean = 0.058;
			}else if(jobClass == 2){
				tempMean = 0.074;
			}else if(jobClass == 3){
				tempMean = 0.0285;
			}else{
				throw new RuntimeException("Classe non prevista");
			}
		}else{
			tempMean = 1.0;
		}
		
		
		return (tempMean) * generator.generateNextValue().doubleValue();
	}
	
	public Boolean isFree() {
		return free;
	}

	private Boolean isFound() {
		if (isFoundGen.generateNextValue() <= 0.1) {
			return true;
		} else {
			return false;
		}
	}
	
	public Job getCurrentJob() {
		free = true;
		
		if(type == TipoCentro.DISK){
			jobOut++;
			
			if(this.isFound()){
				currentJob.setFoundData(true);
			}
		}
		
		tempJob = currentJob;
		currentJob = null;
		
		return tempJob;
	}

	public void setCurrentJob(Job currentJob) {
		free = false;
		this.currentJob = currentJob;
	}

	public void setJobOut(Integer jobOut) {
		this.jobOut = jobOut;
	}

	public Integer getJobOut() {
		return jobOut;
	}

	public Boolean isQueueEmpty() {
		return queue.isEmpty();
	}

	public String getName() {
		return name;
	}
}
