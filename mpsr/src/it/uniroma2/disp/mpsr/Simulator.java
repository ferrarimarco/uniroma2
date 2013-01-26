package it.uniroma2.disp.mpsr;

import info.ferrarimarco.java.helper.math.SimpleMatrix;

public class Simulator {

	private static final int nodes = 4;
	private static final int users = 35;
	
	public static void main(String[] args) {

		SimpleMatrix<Integer> statusMatrix = StatusMatrixGenerator.generateStatus(nodes, users);
		
		System.out.println(statusMatrix.toString());		
	}

}
