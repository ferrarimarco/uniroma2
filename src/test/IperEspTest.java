package test;

import generators.IperEspGenerator;

public class IperEspTest {

	public static void main(String[] args) {

		IperEspGenerator iperGen = new IperEspGenerator(1L, 1L, 10.0, 0.6);
		GeneratorTest genTest = new GeneratorTest("C:\\IperEspGen.txt", false);
		
		Double time;

		for (int i = 0; i < 100000000; i++) {
			time = iperGen.generateNextValue();
			genTest.elaborateNewValue(time);
		}

		genTest.finalize();
	}
}
