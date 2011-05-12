package simulator;

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
	
	//Per prevedere la durata del servizio
	private Generator generator;
	private Double tempMean;
	
	public Centro(String name, TipoCentro type, LinkedList<Job> queue, Generator generator){
		this.name = name;
		this.type = type;
		this.queue = queue;
		free = true;
		
		if(type == TipoCentro.DISK){
			unifGen = new UniformLongGenerator(0L, 0L, 103L);
		}
		
		this.generator = generator;
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
			throw new RuntimeException("Centro senza coda");
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
	
	public Number prevediDurata(Integer jobClass){
		if(jobClass == 1){
			tempMean = 0.058;
		}else if(jobClass == 2){
			tempMean = 0.074;
		}else if(jobClass == 3){
			tempMean = 0.0285;
		}else{
			throw new RuntimeException("Classe non prevista");
		}
		
		return (tempMean) * generator.generateNextValue().doubleValue();
	}
	
	public Boolean isFree() {
		return free;
	}

	public Job getCurrentJob() {
		free = true;
		
		return currentJob;
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
}
