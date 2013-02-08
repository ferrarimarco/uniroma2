package it.uniroma2.disp.mpsr.init;

import info.ferrarimarco.java.helper.math.SimpleMatrix;

public class XCalculator {

	public static double[] computeX(int nodes, SimpleMatrix<Double> routingMatrix, double[] mu){
		
		double[] x = new double[nodes];
		
		x[0] = 1.0;
		x[1] = mu[0] / (mu[2] * routingMatrix.getElement(2, 0));
		x[2] = mu[0] / (mu[1] * routingMatrix.getElement(2, 0));
		x[3] = (mu[0] * routingMatrix.getElement(2, 3)) / (mu[3] * routingMatrix.getElement(2, 0));
		
		return x;
	}
	
}
