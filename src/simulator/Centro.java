package simulator;

import java.util.LinkedList;

public class Centro {

	private String name;
	private TipoCentro type;
	private LinkedList<Job> queue;
	
	//Per contare i Job uscenti
	private Integer jobOut;
	
	public Centro(String name, TipoCentro type, LinkedList<Job> queue){
		this.name = name;
		this.type = type;
		this.queue = queue;
	}
}
