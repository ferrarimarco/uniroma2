package generators;

import interfaces.Generator;

public class ErlangGenerator implements Generator {

	private ExponentialGenerator expGen;
	private Integer k;
	private Double tSum;
	private Double mean;
	private Double mu;
	
	public ErlangGenerator(Long seed, Double mean, Integer k){
		if(k <= 0)
			throw new ArithmeticException();
		
		this.k = k;
		expGen = new ExponentialGenerator(seed, mean / k);
		tSum = 0.0;
		this.mean = mean;
		mu = 1.0/this.mean;
	}
	
	public Double generateNextValue() {
		
		tSum = 0.0;
		
		for(int i = 0; i < k; i++)
			tSum = tSum + expGen.generateNextValue();

		return tSum;
	}

	public Double getMu() {
		return mu;
	}
}
