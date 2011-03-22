package test;

import generators.ErlangGenerator;

public class ErlGenTest {

	public static void main(String[] args) {
		
		ErlangGenerator erl = new ErlangGenerator(1L, 2.0, 20);
		GeneratorTest genTest = new GeneratorTest("C:\\ErlTest", false);
		Double time;

		for(int i=0; i<1000; i++){
			time = erl.generateNextValue();
			genTest.elaborateNewValue(time, erl.getDensity(time));
		}
		
		genTest.finalize();
	}
}
