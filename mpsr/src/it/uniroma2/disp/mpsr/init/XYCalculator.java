package it.uniroma2.disp.mpsr.init;

import java.util.List;

import info.ferrarimarco.java.helper.math.SimpleMatrix;

public class XYCalculator {

	public static double[] computeX(int nodes, SimpleMatrix<Double> routingMatrix, double[] mu){
		
		double[] x = new double[nodes];
		
		x[0] = 1.0;
		x[1] = mu[0] / (mu[2] * routingMatrix.getElement(2, 0));
		x[2] = mu[0] / (mu[1] * routingMatrix.getElement(2, 0));
		x[3] = (mu[0] * routingMatrix.getElement(2, 3)) / (mu[3] * routingMatrix.getElement(2, 0));
		
		return x;
	}
	
	public static double[] computeYMVA(int nodes, SimpleMatrix<Double> routingMatrix, List<Double> mu){
		
		double[] y = new double[nodes];
		
		y[2] = 1.0;
		
		y[0] = (y[2] * routingMatrix.getElement(2, 0)) / (1.0 - routingMatrix.getElement(0, 4));
		y[1] = y[2];
		y[3] = (y[2] * routingMatrix.getElement(2, 3)) / (1 - routingMatrix.getElement(3, 5));
		y[4] = y[0] * routingMatrix.getElement(0, 4);
		y[5] = y[3] * routingMatrix.getElement(3, 5);
		
		return y;
	}
	
}
