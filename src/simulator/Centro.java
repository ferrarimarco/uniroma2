package simulator;

import java.util.LinkedList;

public class Centro {

	private String name;
	private TipoCentro tipo;
	private LinkedList queue;
	
	public Centro(String name, TipoCentro tipo, LinkedList queue){
		this.name = name;
		this.tipo = tipo;
		this.queue = queue;
	}
}
