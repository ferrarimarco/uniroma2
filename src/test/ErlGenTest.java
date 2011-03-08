package test;

import generators.ErlangGenerator;

public class ErlGenTest {

	public static void main(String[] args) {
		
		ErlangGenerator erl = new ErlangGenerator(1L, 2.0, 1);
		GeneratorTest genTest = new GeneratorTest();
		Double time;

		for(int i=0; i<1000; i++){
			time = erl.generateNextValue();
			genTest.elaborateNewValue(time, erl.getDensity(time));
		}
		
		genTest.finalize("C:\\ErlGen");
	}
}
