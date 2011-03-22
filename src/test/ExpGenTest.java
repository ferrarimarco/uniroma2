package test;

import generators.ExponentialGenerator;

public class ExpGenTest {

	public static void main(String[] args) {
	
		ExponentialGenerator exp = new ExponentialGenerator(1L, 5.0);
		GeneratorTest genTest = new GeneratorTest("C:\\ExpGen", false);
		Double time;
		
		for(int i=0; i<1000; i++){
			time = exp.generateNextValue();
			genTest.elaborateNewValue(time, exp.getDensity(time));
		}

		genTest.finalize();
	}

}
