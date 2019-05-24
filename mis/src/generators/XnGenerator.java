package generators;

import java.io.Serializable;

import constants.GeneratorConstants;
import interfaces.Generator;

public class XnGenerator implements Generator, Serializable{
	
	private Long x0;
	private Long xn;
	
	private Long multiplier;
	private Long module;


	public XnGenerator(Long seed){
		//Inizializzo variabili per la sequenza
		x0 = seed;
		xn = x0;
		multiplier = GeneratorConstants.MULTIPLIER;
		module = GeneratorConstants.MODULE;
	}
	
	//Genero un nuovo numero della sequenza
	public Long generateNextValue(){
		
		xn = (multiplier * xn) % module;
		
		return xn;
	}

	protected void setXn(Long xn){
		this.xn = xn;
	}
	
	protected Long getXn() {
		return xn;
	}

	public Long getMultiplier() {
		return multiplier;
	}

	public Long getModule() {
		return module;
	}

}
