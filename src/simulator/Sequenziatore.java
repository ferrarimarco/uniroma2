package simulator;

import generators.ErlangGenerator;
import generators.ExponentialGenerator;
import generators.IperEspGenerator;
import generators.SeedCalculator;
import generators.UniformDoubleGenerator;


import java.util.LinkedList;

public class Sequenziatore {

	private Centro cpu;
	private Centro disk;
	private Centro[] stampanti;
	private Centro[] host;
	private Centro[] terminali;

	private CalendarioEventi calendar;
	private Clock clock;
	private Integer nextEventIndex;
	private Double nextEventTime;
	private UniformDoubleGenerator jobClassGen;
	
	//Per statistiche
	private Double tempoMedioRispJob;
	private Double thrDisk;
	private Integer jobCompletati;
	private Double tempoRispTuttiJob;
	private Integer jobInHost;
	
	//Debug
	private String eventoPrec;
	
	public Sequenziatore(Integer numeroJob){
		
		cpu = new Centro("Cpu", TipoCentro.CPU, new LinkedList<Job>(), new ExponentialGenerator(SeedCalculator.getSeme(), 1.0));
		disk = new Centro("Disk", TipoCentro.DISK, new LinkedList<Job>(), new ErlangGenerator(SeedCalculator.getSeme(), 0.033, 3));

		stampanti = new Centro[numeroJob];
		host = new Centro[numeroJob];
		terminali = new Centro[numeroJob];
		
		calendar = new CalendarioEventi(numeroJob);
		clock = new Clock();
		
		jobCompletati = 0;
		tempoMedioRispJob = 0.0;
		tempoRispTuttiJob = 0.0;
		jobInHost = 0;
		
		jobClassGen = new UniformDoubleGenerator(SeedCalculator.getSeme());
		
		for(int i = 0; i < numeroJob; i++){
			stampanti[i] = new Centro("ST" + i, TipoCentro.STAMPANTE, null, new UniformDoubleGenerator(2L, 78L, SeedCalculator.getSeme()));
		}
		
		for(int i = 0; i < numeroJob; i++){
			host[i] = new Centro("HOST" + i, TipoCentro.HOST, null, new IperEspGenerator(SeedCalculator.getSeme(), SeedCalculator.getSeme(), 0.085, 0.6));
		}
		
		for(int i = 0; i < numeroJob; i++){
			Centro tempCentro = new Centro("TERM" + i, TipoCentro.TERMINALE, null, new ErlangGenerator(SeedCalculator.getSeme(), 10.0, 2));
			
			//Occupiamo il centro con job (per inizializzazione)
			Job tempJob = new Job(1,i);
			tempCentro.setCurrentJob(tempJob);
			terminali[i] = tempCentro;
			
			calendar.updateEvent(calendar.firstTerminalIndex + i, tempCentro.prevediDurata(1).doubleValue());
		}
	}
	
	public void simula(Integer lunghezzaRun){
		
		while(jobCompletati < lunghezzaRun){
			nextEventIndex = calendar.getNextEventIndex();
			nextEventTime = calendar.getEventTime(nextEventIndex);
			
			if(nextEventTime == Double.MAX_VALUE){
				System.out.println("Indice Evento precedente a errore: " + eventoPrec);
				
				throw new RuntimeException();
			}
			
			clock.setSimTime(nextEventTime);
			
			if(nextEventIndex == 0){//Evento fineCPU
				this.fineCpu();
			}else if(nextEventIndex == 1){//Evento fineDisk
				this.fineDisk();
			}else if(nextEventIndex >= calendar.firstTerminalIndex & nextEventIndex < calendar.firstHostIndex){//Evento fineTerminale
				this.fineTerminale(nextEventIndex - 2);
			}else if(nextEventIndex >= calendar.firstHostIndex & nextEventIndex < calendar.firstStIndex){//Evento fineHost
				this.fineHost(nextEventIndex - 2 - (calendar.firstHostIndex - calendar.firstTerminalIndex));
			}else{//Evento fineStampante
				this.fineStampante(nextEventIndex - 2 - (calendar.firstStIndex - calendar.firstTerminalIndex));
			}
			
			//Debug
			eventoPrec = nextEventIndex.toString();
		}
		
		//Per calcolo statistiche
		if(jobInHost != 0){
			tempoMedioRispJob = tempoRispTuttiJob / jobInHost;
		}
		
		thrDisk = disk.getJobOut() / (clock.getSimTime());
	}
	
	private void fineTerminale(Integer idCentro){
		
		//Gestione job uscente da terminale
		
		//Prendo il job corrente e libero il terminale
		Job j = terminali[idCentro].getCurrentJob();
		j.setTermExitTime(clock.getSimTime());

		//Invio job uscente a cpu
		if(cpu.isFree() && cpu.isQueueEmpty()){
			cpu.setCurrentJob(j);
			
			//Prevedo durata del servizio CPU
			Double durata = cpu.prevediDurata(j.getJobClass()).doubleValue();
			calendar.updateEvent(calendar.cpuIndex, clock.getSimTime() + durata);
		}else{
			cpu.addJobToQueue(j);
		}
	}
	
	private void fineCpu(){
		
		Job j = cpu.getCurrentJob();
		Integer idCentro = j.getIdentifier();

		System.out.println("CPU: prendo job " + j.getIdentifier() + " class " + j.getJobClass());
		
		if(j.getJobClass() == 1){//Gestione Job uscente classe 1
			
			//Calcolo prob per decidere nuova classe job
			if(jobClassGen.generateNextValue() <= 0.3){
				j.setJobClass(2);
			}else{
				j.setJobClass(3);
			}

			System.out.println("CPU: job " + j.getIdentifier() + " classe 1 diventa classe " + j.getJobClass());
			
			//Invio job uscente a cpu
			if(cpu.isFree() && cpu.isQueueEmpty()){
				cpu.setCurrentJob(j);
				
				System.out.println("CPU: Metto in cpu job " + j.getIdentifier() + " di classe " + j.getJobClass());
				
				//Prevedo durata del servizio CPU
				Double durata = cpu.prevediDurata(j.getJobClass()).doubleValue();
				
				//Aggiorno evento CPU
				calendar.updateEvent(calendar.cpuIndex, clock.getSimTime() + durata);
			}else{
				cpu.addJobToQueue(j);
				
				System.out.println("CPU: Metto in coda cpu job " + j.getIdentifier() + "  di classe " + j.getJobClass());
			}
			
		}else{//Gestione Job uscente classe 2 o 3
			if(j.getJobClass() == 2){
				//Invio job uscente a host
				host[idCentro].setCurrentJob(j);

				//Prevedo durata del servizio host
				Double durata = host[idCentro].prevediDurata(j.getIdentifier()).doubleValue();
				
				//Aggiorno evento host
				calendar.updateEvent(calendar.firstHostIndex + j.getIdentifier(), clock.getSimTime() + durata);
				
				System.out.println("CPU: Metto job " + j.getIdentifier() + "  di classe " + j.getJobClass() + " in host " + idCentro);
				
			}else if(j.getJobClass() == 3){
				if(j.getFoundData()){
					//Invio job uscente a stampante
					stampanti[idCentro].setCurrentJob(j);

					//Prevedo durata del servizio stampante
					Double durata = stampanti[idCentro].prevediDurata(j.getIdentifier()).doubleValue();
					
					//Aggiorno evento stampante
					calendar.updateEvent(calendar.firstStIndex + j.getIdentifier(), clock.getSimTime() + durata);
					
					
					System.out.println("CPU: Metto job " + j.getIdentifier() + "  di classe " + j.getJobClass() + " in st " + idCentro);
					
				}else{
					
					//Invio job uscente a disk
					if(disk.isFree() && disk.isQueueEmpty()){
						disk.setCurrentJob(j);
						
						//Prevedo durata del servizio CPU
						Double durata = cpu.prevediDurata(j.getJobClass()).doubleValue();
						
						//Aggiorno evento disk
						calendar.updateEvent(calendar.diskIndex, clock.getSimTime() + durata);
						
						
						System.out.println("CPU: Metto job " + j.getIdentifier() + "  di classe " + j.getJobClass() + " in disk");
						
					}else{
						disk.addJobToQueue(j);
						System.out.println("CPU: Metto job " + j.getIdentifier() + "  di classe " + j.getJobClass() + " in coda disk");
					}
				}
			}else{
				throw new RuntimeException("Errore classe");
			}
		}
		
		//Gestione job entranti
		j = cpu.getJobFromQueue();
		if(j != null){
			cpu.setCurrentJob(j);
			
			//Prevedo durata del servizio CPU
			Double durata = cpu.prevediDurata(j.getJobClass()).doubleValue();
			
			//Aggiorno evento CPU
			calendar.updateEvent(calendar.cpuIndex, clock.getSimTime() + durata);
			
			System.out.println("CPU (job da coda): Metto job " + j.getIdentifier() + "  di classe " + j.getJobClass() + " in cpu");
			
		}
	}
	
	private void fineDisk(){
		
		//Gestione job uscente da disk
		
		//Prendo il job corrente e libero disk
		Job j = disk.getCurrentJob();
		
		//Invio job uscente a cpu
		if(cpu.isFree() && cpu.isQueueEmpty()){
			cpu.setCurrentJob(j);
			
			//Prevedo durata del servizio CPU
			Double durata = cpu.prevediDurata(j.getJobClass()).doubleValue();
			calendar.updateEvent(calendar.cpuIndex, clock.getSimTime() + durata);
			
			System.out.println("DISK: Metto job " + j.getIdentifier() + "  di classe " + j.getJobClass() + " in cpu");
			
		}else{
			cpu.addJobToQueue(j);
			
			System.out.println("DISK: Metto job " + j.getIdentifier() + "  di classe " + j.getJobClass() + " in coda cpu");
			
		}

		//Gestione job entrante
		
		//Prendo job da coda (se presente)
		j = disk.getJobFromQueue();
		
		if(j != null){
			disk.setCurrentJob(j);
			
			//Prevedo durata del servizio CPU
			Double durata = cpu.prevediDurata(j.getJobClass()).doubleValue();
			calendar.updateEvent(calendar.cpuIndex, clock.getSimTime() + durata);
			
			System.out.println("DISK(job da coda): Metto job " + j.getIdentifier() + "  di classe " + j.getJobClass() + " in cpu");
		}
	}
	
	private void fineHost(Integer idCentro){
		
		//Gestione job uscente da Host
		
		//Prendo il job corrente e libero host
		Job j = host[idCentro].getCurrentJob();
		
		//Calcolo tempo di risp
		tempoRispTuttiJob += (clock.getSimTime() - j.getTermExitTime());
		
		//Invio job uscente a stampante
		stampanti[idCentro].setCurrentJob(j);

		//Prevedo durata del servizio stampante
		Double durata = stampanti[idCentro].prevediDurata(j.getIdentifier()).doubleValue();
		
		//Aggiorno evento stampante
		calendar.updateEvent(calendar.firstStIndex + j.getIdentifier(), clock.getSimTime() + durata);

		System.out.println("HOST " + idCentro + ": Metto job " + j.getIdentifier() + "  di classe " + j.getJobClass() + " in st " + idCentro);
		
		jobInHost++;
	}
	
	private void fineStampante(Integer idCentro){
		//Gestione job uscente da Stampante
		
		//Prendo il job corrente e libero stampante
		Job j = stampanti[idCentro].getCurrentJob();
		
		//Invio job uscente a terminale
		terminali[idCentro].setCurrentJob(j);

		//Prevedo durata del servizio terminale
		Double durata = terminali[idCentro].prevediDurata(j.getIdentifier()).doubleValue();
		
		//Aggiorno evento terminale
		calendar.updateEvent(calendar.firstTerminalIndex + j.getIdentifier(), clock.getSimTime() + durata);
		
		System.out.println("ST " + idCentro + ": Metto job " + j.getIdentifier() + "  di classe " + j.getJobClass() + " in st " + idCentro);
				
		jobCompletati++;
	}

	public Double getTempoMedioRispJob() {
		return tempoMedioRispJob;
	}

	public Double getThrDisk() {
		return thrDisk;
	}

	
}
