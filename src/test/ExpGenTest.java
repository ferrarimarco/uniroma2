package test;

import generators.ExponentialGenerator;

public class ExpGenTest {

	public static void main(String[] args) {
	
		ExponentialGenerator expGen = new ExponentialGenerator(1L, 10.0);
		
		GeneratorTest genTest = new GeneratorTest("C:\\ExpGen.txt", false);
		Double time;
		
		for (int i = 0; i < 100000000; i++) {
			time = expGen.generateNextValue();
			genTest.elaborateNewValue(time);
		}

		genTest.finalizeElaboration();
		

	}
}
