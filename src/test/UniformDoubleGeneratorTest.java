package test;

import generators.UniformDoubleGenerator;

public class UniformDoubleGeneratorTest {

	public static void main(String[] args) {
		UniformDoubleGenerator unifGen = new UniformDoubleGenerator(2L, 78L, 101L);
		GeneratorTest genTest = new GeneratorTest("C:\\UnifDoubleGen.txt", false);
		Double time;
		
		for (int i = 0; i < 100000000; i++) {
			time = unifGen.generateNextValue();
			genTest.elaborateNewValue(time.doubleValue());
		}

		genTest.finalizeElaboration();
	}

}
