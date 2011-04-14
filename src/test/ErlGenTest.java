package test;

import generators.ErlangGenerator;

public class ErlGenTest {

	public static void main(String[] args) {
		
		ErlangGenerator erlGen = new ErlangGenerator(1L, 10.0, 2);
		GeneratorTest genTest = new GeneratorTest("C:\\ErlGen.txt", false);
		Double time;
		
		for (int i = 0; i < 100000000; i++) {
			time = erlGen.generateNextValue();
			genTest.elaborateNewValue(time);
		}

		genTest.finalizeElaboration();
	}
}
