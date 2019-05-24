package test;

import generators.IperEspGenerator;

public class IperEspTest {

	public static void main(String[] args) {

		IperEspGenerator iperGen = new IperEspGenerator(103L, 1L, 10.0, 0.6);
		GeneratorTest genTest = new GeneratorTest("C:\\IperEspGen.txt", false);
		
		Double time;

		for (int i = 0; i < 10000; i++) {
			time = iperGen.generateNextValue();
			genTest.elaborateNewValue(time);
		}

		genTest.finalizeElaboration();
	}
}
