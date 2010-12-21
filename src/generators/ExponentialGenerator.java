package generators;

public class ExponentialGenerator {

	private UniformDoubleGenerator rng;
	private Double mean;
	
	public ExponentialGenerator(Long seed, Double mean){
		
		this.rng = new UniformDoubleGenerator(seed);
		this.mean = mean;	
		
	}
	
	public Double generateNextValue(){
		return (-mean) * Math.log(rng.generateNextValue());
	}
	
	public Double getDensity(Double t){
		return (1.0/mean) * Math.exp((-(1.0/mean))* t);
	}
}
