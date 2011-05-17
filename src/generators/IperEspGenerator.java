package generators;

import interfaces.Generator;

public class IperEspGenerator implements Generator {

	private ExponentialGenerator expGen;
	private UniformDoubleGenerator uniGen;
	private Double tempo1;
	private Double tempo2;
	private Double prob;

	public IperEspGenerator(Long seedExp, Long seedUni, Double mean, Double prob) {
		tempo1 = mean / (2 * prob);
		tempo2 = mean / (2 * (1 - prob));
		
		//seedExp != seedUni AND semi diversi da 1L
		
		expGen = new ExponentialGenerator(seedExp, 1.0);
		uniGen = new UniformDoubleGenerator(seedUni);
		this.prob = prob;
	}

	public Double generateNextValue() {
		if (uniGen.generateNextValue() <= prob)
			return expGen.generateNextValue() * tempo1;
		else
			return expGen.generateNextValue() * tempo2;
	}
}
