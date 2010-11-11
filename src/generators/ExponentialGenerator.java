package generators;

public class ExponentialGenerator {

	private RnGenerator rng;
	private Double average;
	
	public ExponentialGenerator(Long seed, Double average){
		
		this.rng = new RnGenerator(seed);
		this.average = average;		
		
	}
	
	public Double generateNextValue(){
		return (-1.0 * average) * Math.log(rng.getNextValue());
	}

	public RnGenerator getRng() {
		return rng;
	}

	public Double getAverage() {
		return average;
	}
}
