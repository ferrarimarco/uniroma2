package test;

import generators.UniformLongGenerator;

public class UniformLongGeneratorTest {

	public static void main(String[] args) {
		
		
		UniformLongGenerator unifGen = new UniformLongGenerator(2L, 78L, 101L);
		GeneratorTest genTest = new GeneratorTest("C:\\UnifLongGen.txt", false);
		Long time;
		
		for (int i = 0; i < 100000000; i++) {
			time = unifGen.generateNextValue();
			genTest.elaborateNewValue(time.doubleValue());
		}

		genTest.finalizeElaboration();
		
		
	}

}
