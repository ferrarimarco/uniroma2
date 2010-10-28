package generators;

import interfaces.Generator;


public class RnSequenceGenerator extends SequenceGenerator  implements Generator {

	private Double result;

	//Costruttore con seed di default
	public RnSequenceGenerator(){
		
		//Inizializzazione
		super();
	}
	
	//Costruttore con seed custom
	public RnSequenceGenerator(Long seed){
		
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
