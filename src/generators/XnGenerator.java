package generators;

import constants.GeneratorConstants;
import interfaces.Generator;

public class XnGenerator implements Generator{
	
	private Long x0;
	private Long xn;
	
	private Long multiplier;
	private Long module;


	public XnGenerator(Long seed){
		
		//Controllo se il seme va bene
		if ((seed > 0) && (seed % 2 == 1) && (seed % 5 != 0)) {
			x0 = seed;
		}else{
			System.out.println("seed inserito non va bene, uso il seed default (x0 = 1).");
		}
		
		//Inizializzo variabili per la sequenza
		xn = x0;
		multiplier = GeneratorConstants.MULTIPLIER;
		module = GeneratorConstants.MODULE;
	}
	
	//Genero un nuovo numero della sequenza
	public Long generateNextValue(){
		
		xn = (multiplier * xn) % module;
		
		return xn;
	}
	
	public Long getX0() {
		return x0;
	}

	public void setX0(Long x0) {
		this.x0 = x0;
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
