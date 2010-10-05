package generators;

import interfaces.Generator;

import java.util.ArrayList;

public class XnSequenceGenerator extends SequenceGenerator implements Generator{
	
	private ArrayList<Long> sequence;
	
	//Costruttore con seed di default
	public XnSequenceGenerator(){
		
		//Inizializzazione
		super();
		
		//Sequenza di tutti i numeri generati
		sequence = new ArrayList<Long>();

		//Aggiungo il seme alla sequenza (primo elemento)
		sequence.add(getX0());
		
	}
	
	//Costruttore con seed scelto
	public XnSequenceGenerator(Long seed){
		
		//Inizializzazione
		super(seed);
		
		//Sequenza di tutti i numeri generati
		sequence = new ArrayList<Long>();
		
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
