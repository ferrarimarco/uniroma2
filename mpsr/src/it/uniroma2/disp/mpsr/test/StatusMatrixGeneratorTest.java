package it.uniroma2.disp.mpsr.test;

import it.uniroma2.disp.mpsr.StatusMatrixGenerator;

public class StatusMatrixGeneratorTest {

	public static void main(String[] args){		
		StatusMatrixGenerator gen = new StatusMatrixGenerator(3, 4);
		gen.generateStatusMatrix();
		
		
	}
}
