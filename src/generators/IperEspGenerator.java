package generators;

import interfaces.Generator;

public class IperEspGenerator implements Generator {

	//private ExponentialGenerator expGen1;
	//private ExponentialGenerator expGen2;
	private ExponentialGenerator expGen;
	private UniformDoubleGenerator uniGen;
	private Double tempo1;
	private Double tempo2;
	private Double prob;
	private Double mean;

	public IperEspGenerator(Long seed1, Long seed2, Double mean, Double prob) {
		tempo1 = mean / (2 * prob);
		tempo2 = mean / (2 * (1 - prob));
		expGen = new ExponentialGenerator(seed1, 1.0);
		//expGen1 = new ExponentialGenerator(seed1, tempo1);
		//expGen2 = new ExponentialGenerator(seed2, tempo2);
		uniGen = new UniformDoubleGenerator(seed1);
		this.prob = prob;
		this.mean = mean;
	}

	@Override
	public Double generateNextValue() {
		if (uniGen.generateNextValue() >= prob)
			return expGen.generateNextValue()*tempo1;
		else
			return expGen.generateNextValue()*tempo2;
	}

	public Double getDensity(Double t) {
		return 2 * Math.pow(prob, 2) * (1.0 / mean)
				* Math.exp(-2 * prob * (1.0 / mean) * t) + 2
				* Math.pow(1 - prob, 2) * (1.0 / mean)
				* Math.exp(-2 * (1 - prob) * (1.0 / mean) * t);
	}
}
