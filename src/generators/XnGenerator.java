package generators;

import interfaces.Generator;

public class XnGenerator extends AbstractGenerator implements Generator{
	
	//Costruttore con seed scelto
	public XnGenerator(Long seed){
		
		//Inizializzazione
		super(seed);
	}
	
	
	//Restituisco nuovo numero della sequenza
	public Long getNextValue(){
		
		//genero un nuovo numero della sequenza
		setXn(generateNextValue());
		
		//incremento contatore sequenza
		setN(getN() + 1);

		return getXn();
	}

	public Number getActualValue() {
		return getXn();
	}

}
