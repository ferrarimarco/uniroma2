package generators;

import constants.GeneratorConstants;
import interfaces.Generator;

public abstract class SequenceGenerator implements Generator{
	
	private Long x0;
	private Long xn;
	private Long n;
	private Long next;
	
	private Long multiplier;
	private Long module;

	public SequenceGenerator(){
		
		//Default seed
		x0 = 1L;
		
		//Inizializzo variabili per la sequenza
		xn = x0;
		n = 0L;
		multiplier = GeneratorConstants.MULTIPLIER;
		module = GeneratorConstants.MODULE;
	}
	
	//Genero un nuovo numero della sequenza
	public Long generateNextValue(){
		setNext((multiplier * xn) % module);
		
		return getNext();
	}
	
	public Long getX0() {
		return x0;
	}

	public void setX0(Long x0) {
		this.x0 = x0;
	}

	public Long getXn() {
		return xn;
	}

	public void setXn(Long xn) {
		this.xn = xn;
	}

	public Long getN() {
		return n;
	}

	public void setN(Long n) {
		this.n = n;
	}

	public Long getNext() {
		return next;
	}

	public void setNext(Long next) {
		this.next = next;
	}

	public Long getMultiplier() {
		return multiplier;
	}

	public Long getModule() {
		return module;
	}

}
