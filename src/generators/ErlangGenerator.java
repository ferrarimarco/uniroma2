package generators;

import interfaces.Generator;

public class ErlangGenerator implements Generator {

	private ExponentialGenerator expGen;
	private Long k;
	private Double tSum;
	
	public ErlangGenerator(Long seed, Double mean, Long k){
		this.k = k;
		expGen = new ExponentialGenerator(seed, mean / k);
		tSum = 0.0;
	}
	
	public Double generateNextValue() {
		
		for(int i = 0; i < k; i++)
			tSum = tSum + expGen.generateNextValue();
		
		return tSum;
	}

}
