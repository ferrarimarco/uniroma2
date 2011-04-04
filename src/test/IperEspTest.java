package test;

import generators.IperEspGenerator;

public class IperEspTest {

	public static void main(String[] args) {

		IperEspGenerator iperGen = new IperEspGenerator(1L, 3L, 10.0, 0.6);
		GeneratorTest genTest = new GeneratorTest("C:\\IperEspGen", false);
		
		Double time;

		for (int i = 0; i < 1000000; i++) {
			time = iperGen.generateNextValue();
			genTest.elaborateNewValueInt(time);
		}

		genTest.finalizeInt();
	}
}
