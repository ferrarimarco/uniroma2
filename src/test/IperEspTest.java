package test;

import generators.IperEspGenerator;

public class IperEspTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		IperEspGenerator iperGen = new IperEspGenerator(1L, 3L, 2.0, 0.6);
		GeneratorTest genTest = new GeneratorTest();
		
		Double time;

		for (int i = 0; i < 100000; i++) {
			time = iperGen.generateNextValue();
			genTest.elaborateNewValue(time, iperGen.getDensity(time));
		}

		genTest.finalize("C:\\");
	}

}
