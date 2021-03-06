package generators;

import java.io.Serializable;

import interfaces.Generator;

public class ExponentialGenerator implements Generator, Serializable{

	private UniformDoubleGenerator rng;
	private Double mean;
	
	public ExponentialGenerator(Long seed, Double mean){
		
		this.rng = new UniformDoubleGenerator(seed);
		this.mean = mean;	
		
	}
	
	public Double generateNextValue(){
		return (-mean) * Math.log(rng.generateNextValue());
	}
}
