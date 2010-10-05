package generators;

import interfaces.Generator;

import java.util.ArrayList;

public class XnSequenceGenerator extends SequenceGenerator implements Generator{
	
	private ArrayList<Long> sequence;
	
	//Costruttore con seed di default
	public XnSequenceGenerator(){
		
		//Inizializzazione
		super();
		sequence = new ArrayList<Long>();

		//Aggiungo il seme alla sequenza (primo elemento)
		sequence.add(getX0());
		
	}
	
	//Costruttore con seed scelto
	public XnSequenceGenerator(Long seed){
		
		//Inizializzazione
		super();
		sequence = new ArrayList<Long>();
		
		if ((seed > 0) && (seed % 2 == 1) && (seed % 5 != 0)) {
			super.setX0(seed);
		}else{
			System.out.println("seed inserito non va bene, uso il seed default (x0 = 1).");
		}
		
		//Aggiungo il seme alla sequenza (primo elemento)
		sequence.add(getX0());
	}
	
	
	//Restituisco nuovo numero della sequenza
	public Long getNextValue(){
		
		//genero un nuovo numero della sequenza
		setXn(generateNextValue());
		
		//incremento contatore sequenza
		setN(getN() + 1);
		
		//Aggiungo il nuovo xn alla lista dei numeri della sequenza
		sequence.add(getXn());
		
		return getXn();
	}

	public ArrayList<Long> getSequence() {
			return sequence;
	}

	public Number getActualValue() {
		return getXn();
	}

}
