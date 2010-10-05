package generators;

import interfaces.Generator;

import java.util.ArrayList;


public class RnSequenceGenerator extends SequenceGenerator  implements Generator {

	private Double result;
	private ArrayList<Double> sequence;
	
	
	//Costruttore con seed di default
	public RnSequenceGenerator(){
		
		//Inizializzazione
		super();
		sequence = new ArrayList<Double>();
		
		//Aggiungo il seme alla sequenza (primo elemento)
		sequence.add(perModuleDivision(getX0()));
	}
	
	//Costruttore con seed custom
	public RnSequenceGenerator(Long seed){
		
		//Inizializzazione
		super(seed);
		
		//Sequenza di tutti i numeri generati
		sequence = new ArrayList<Double>();
		
		//Aggiungo il seme alla sequenza (primo elemento)
		sequence.add(perModuleDivision(getX0()));
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
		
		//Aggiungo il nuovo xn alla lista dei numeri della sequenza
		sequence.add(perModuleDivision(getXn()));
		
		return perModuleDivision(getXn());
		
	}
	
	//Restituisco la sequenza finora generata
	public ArrayList<Double> getSequence(){
		return sequence;
	}
	
	private Double perModuleDivision(Long l){
		
		//Uso doubleValue per evitare problemi con divisione
		result = (l.doubleValue() / getModule().doubleValue());
		return result;
	}
	
	
}
