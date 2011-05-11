package simulator;

import java.util.ArrayList;
import java.util.LinkedList;

public class Sequenziatore {

	private Centro cpu;
	private Centro disk;
	private ArrayList<Centro> stampanti;
	private ArrayList<Centro> host;
	private ArrayList<Centro> terminali;
	
	private CalendarioEventi calendar;
	private Clock clock;
	
	public Sequenziatore(){
		
		cpu = new Centro("Cpu", TipoCentro.CPU, new LinkedList<Job>());
		disk = new Centro("Disk", TipoCentro.DISK, new LinkedList<Job>());
		
		stampanti = new ArrayList<Centro>();
		host = new ArrayList<Centro>();
		terminali = new ArrayList<Centro>();
		
		calendar = new CalendarioEventi();
		clock = new Clock();
	}
	
	private void fineTerminale(){
		
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
