package generators;

import interfaces.Generator;


public class RnGenerator extends AbstractGenerator  implements Generator {

	private Double result;
	
	//Costruttore con seed custom
	public RnGenerator(Long seed){
		
		//Inizializzazione
		super(seed);
		
	}
	
	
	//Restituisco prossimo numero della sequenza
	public Double getActualValue(){
		return perModuleDivision(getXn());
	}
	
	//Restituisco nuovo numero della sequenza
	public Double getNextValue(){
		
		//genero un nuovo numero della sequenza
		setXn(generateNextValue());
		
		//incremento contatore sequenza
		setN(getN() + 1);

		return perModuleDivision(getXn());
		
	}
	
	
	private Double perModuleDivision(Long l){
		
		//Uso doubleValue per evitare problemi con divisione
		result = (l.doubleValue() / getModule().doubleValue());
		return result;
	}
	
	
}
