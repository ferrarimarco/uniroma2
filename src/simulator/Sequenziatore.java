package simulator;

import generators.ErlangGenerator;
import generators.ExponentialGenerator;
import generators.IperEspGenerator;
import generators.UniformDoubleGenerator;

import java.util.ArrayList;
import java.util.LinkedList;

public class Sequenziatore {

	private Centro cpu;
	private Centro disk;
	private Centro[] stampanti;
	private Centro[] host;
	private Centro[] terminali;
	
	private CalendarioEventi calendar;
	private Clock clock;
	
	private Job tempJob;
	private Centro tempCentro;
	private Centro tempTerminale;
	
	public Sequenziatore(Integer numeroJob){
		
		cpu = new Centro("Cpu", TipoCentro.CPU, new LinkedList<Job>(), new ExponentialGenerator(105L, 1.0));
		disk = new Centro("Disk", TipoCentro.DISK, new LinkedList<Job>(), new ErlangGenerator(107L, 0.033, 3));
		
		stampanti = new Centro[numeroJob];
		host = new Centro[numeroJob];
		terminali = new Centro[numeroJob];
		
		for(int i = 0; i < numeroJob; i++){
			stampanti[i] = new Centro("ST" + i, TipoCentro.STAMPANTE, null, new UniformDoubleGenerator(2L, 78L, 23L));
		}
		
		for(int i = 0; i < numeroJob; i++){
			host[i] = new Centro("HOST" + i, TipoCentro.HOST, null, new IperEspGenerator(27L, 31L, 0.085, 0.6));
		}
		
		for(int i = 0; i < numeroJob; i++){
			tempCentro = new Centro("TERM" + i, TipoCentro.TERMINALE, null, new ErlangGenerator(37L, 10.0, 2));
			
			//Occupiamo il centro con job (per inizializzazione)
			tempJob = new Job(1,i);
			tempCentro.setCurrentJob(tempJob);
			
			terminali[i] = tempCentro;
		}
		
		calendar = new CalendarioEventi();
		clock = new Clock();
	}
	
	private void fineTerminale(TipoEvento evento){
		
		//Gestione job uscente da terminale
		
		//Prendo il job corrente e libero il terminale
		Job j = terminali[evento.getEventId()].getCurrentJob();
		
		//Invio job uscente a cpu
		if(cpu.isFree()){
			cpu.setCurrentJob(tempJob);
		}else{
			cpu.addJobToQueue(tempJob);
		}

		//Prevedo durata del servizio CPU
		Double durata = cpu.prevediDurata(j.getIdentifier()).doubleValue();
		calendar.updateEvent(new Event(TipoEvento.FINECPU, clock.getSimTime() + durata));
	}
	
	private void fineCpu(){
		
	}
	
	private void fineDisk(){
		
	}
	
	private void fineHost(){
		
	}
	
	private void fineStampante(){
		
	}
}
