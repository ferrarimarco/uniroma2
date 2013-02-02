package it.uniroma2.disp.mpsr.test;

import info.ferrarimarco.java.helper.math.SimpleMatrix;
import it.uniroma2.disp.mpsr.StatusMatrixGenerator;

public class StatusMatrixGeneratorTest {

	public static void main(String[] args){		
		StatusMatrixGenerator gen = new StatusMatrixGenerator(3, 4);
		SimpleMatrix<Integer> statusMatrix = gen.generateStatusMatrix();
		
		System.out.println(statusMatrix.toString());
	}
}
