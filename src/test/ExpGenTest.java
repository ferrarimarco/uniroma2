package test;

import generators.ExponentialGenerator;

public class ExpGenTest {

	public static void main(String[] args) {
	
		ExponentialGenerator exp = new ExponentialGenerator(1L, 0.5);
		GeneratorTest genTest = new GeneratorTest();
		
		Double time;
		
		for(int i=0; i<1000000000; i++){
			time = exp.generateNextValue();
			genTest.elaborateNewValue(time, exp.getDensity(time));
		}
		
		genTest.finalize("C:\\ExpGen");
	}

}
