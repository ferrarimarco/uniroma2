package generators;

import interfaces.Generator;

import java.util.ArrayList;

import constants.GeneratorConstants;


public class RnSequenceGenerator implements Generator {

	private Long x0;
	private Long xn;
	private Long n;
	private Long next;
	
	private Double result;
	
	private Long multiplier;
	private Long module;
	
	private ArrayList<Double> sequenceRn;
	
	
	//Costruttore con seed di default
	public RnSequenceGenerator(){
		
		//Default seed
		x0 = 1L;
		
		//Inizializzazione
		this.commonInit();
		
	}
	
	//Costruttore con seed custom
	public RnSequenceGenerator(Long seed){
		
		
		if ((seed > 0) && (seed % 2 == 1) && (seed % 5 != 0)) {
			x0 = seed;
		}else{
			System.out.println("seed inserito non va bene, uso il seed default (x0 = 1). Puoi cambiarlo con il metodo setX0");
			x0 = 1L;
		}
		
		this.commonInit();
		
	}
	
	//Inizializzazione comune ai due costruttori
	private void commonInit(){
		
		sequenceRn = new ArrayList<Double>();
		
		//Inizializzo variabili per la sequenza
		xn = x0;
		n = 0L;
		multiplier = GeneratorConstants.MULTIPLIER;
		module = GeneratorConstants.MODULE;
		
		//Aggiungo il seme alla sequenza (primo elemento)
		sequenceRn.add(perModuleDivision(x0));
	}
	
	
	//Genero un nuovo numero della sequenza
	private Long generateNext(){
		next = (multiplier * xn) % module;
		
		return next;
	}
	
	//Restituisco prossimo numero della sequenza
	public Double getActualValue(){
		return perModuleDivision(xn);
	}
	
	//Restituisco nuovo numero della sequenza
	public Double getNextValue(){
		
		//genero un nuovo numero della sequenza
		xn = generateNext();
		
		//incremento contatore sequenza
		n++;
		
		//Aggiungo il nuovo xn alla lista dei numeri della sequenza
		sequenceRn.add(perModuleDivision(xn));
		
		return perModuleDivision(xn);
		
	}
	
	//Restituisco la sequenza finora generata
	public ArrayList<Double> getActualSequence(){
		return sequenceRn;
	}
	
	private Double perModuleDivision(Long l){
		
		result = (l.doubleValue() / module.doubleValue());
		return result;
	}

	
	public void setX0(Long x0) {
		this.x0 = x0;
	}
	
	
}
