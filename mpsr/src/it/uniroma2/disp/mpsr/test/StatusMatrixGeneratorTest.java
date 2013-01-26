package it.uniroma2.disp.mpsr.test;

import info.ferrarimarco.java.helper.math.SimpleMatrix;
import it.uniroma2.disp.mpsr.StatusMatrixGenerator;

public class StatusMatrixGeneratorTest {

	public static void main(String[] args){
		SimpleMatrix<Integer> statusMatrix = StatusMatrixGenerator.generateStatus(4, 3);
		
		System.out.println(statusMatrix.toString());
		
	}
}
