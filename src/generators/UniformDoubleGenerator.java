package generators;

import interfaces.Generator;


public class UniformDoubleGenerator implements Generator {

	private XnGenerator xng;
	
	private Double result;
	private Double tempRn;
	private Long tempXn;
	
	private Long rangeA;
	private Long rangeB;
	
	//Costruttore con seed custom
	public UniformDoubleGenerator(Long rangeA, Long rangeB, Long seed){
		
		xng = new XnGenerator(seed);
		
		this.rangeA = rangeA;
		this.rangeB = rangeB;
	}
	
	//Costruttore con seed custom, ma range default (tra 0 e 1)
	public UniformDoubleGenerator(Long seed){
		
		xng = new XnGenerator(seed);
		
		this.rangeA = 0L;
		this.rangeB = 1L;
	}
	
	
	//Restituisco nuovo numero
	public Double generateNextValue(){
		
		//Genero nuovo numero della sequenza
		tempXn = xng.generateNextValue();

		tempRn = rangeA + (rangeB - rangeA) * perModuleDivision(tempXn);
		
		return tempRn;
		
	}
	
	
	private Double perModuleDivision(Long l){
		
		//Uso doubleValue per evitare problemi con divisione
		result = (l.doubleValue() / xng.getModule().doubleValue());
		return result;
	}
	
	public Long getRangeA() {
		return rangeA;
	}

	public Long getRangeB() {
		return rangeB;
	}
}
