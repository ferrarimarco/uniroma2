package generators;

import interfaces.Generator;

public class XnSequenceGenerator extends SequenceGenerator implements Generator{
	
	//Costruttore con seed scelto
	public XnSequenceGenerator(Long seed){
		
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
