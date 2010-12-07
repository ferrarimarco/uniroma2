package generators;

public class ExponentialGenerator {

	private RnGenerator rng;
	private Double mean;
	
	public ExponentialGenerator(Long seed, Double mean){
		
		this.rng = new RnGenerator(seed);
		this.mean = mean;		
		
	}
	
	public Double generateNextValue(){
		return (-1.0 * mean) * Math.log(rng.getNextValue());
	}

	public RnGenerator getRng() {
		return rng;
	}

	public Double getAverage() {
		return mean;
	}
}
