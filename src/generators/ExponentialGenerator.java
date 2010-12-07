package generators;

public class ExponentialGenerator {

	private UniformDoubleGenerator rng;
	private Double mean;
	
	public ExponentialGenerator(Long seed, Double mean){
		
		this.rng = new UniformDoubleGenerator(0L, 1L, seed);
		this.mean = mean;		
		
	}
	
	public Double generateNextValue(){
		return (-1.0 * mean) * Math.log(rng.generateNextValue());
	}

	public UniformDoubleGenerator getRng() {
		return rng;
	}

	public Double getMean() {
		return mean;
	}
}
