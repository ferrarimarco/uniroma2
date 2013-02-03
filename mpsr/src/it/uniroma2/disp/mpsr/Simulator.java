package it.uniroma2.disp.mpsr;

import info.ferrarimarco.java.helper.math.SimpleMatrix;

public class Simulator {

	private static final int nodes = 4;
	private static final int users = 1;
	
	public static void main(String[] args) {
		
		double mu0 = 1.0/7.0; // client1
		double mu1 = 1/0.3; // FE server
		double mu2 = 1/0.08; // BE server
		double mu3 = 1.0/7.0; // client2
		
		StatusMatrixGenerator statusMatrixGenerator = new StatusMatrixGenerator(users, nodes);
		
		SimpleMatrix<Integer> statusMatrix = statusMatrixGenerator.generateStatusMatrix();
		SimpleMatrix<Double> routingMatrix = RoutingMatrixGenerator.generateRoutingMatrix(4);
		
		double x0 = 5.0;
		double x1 = ((5.0 * mu0) / mu1) * (1.0 + routingMatrix.getElement(2, 3)/routingMatrix.getElement(2, 0));
		double x2 = (5.0 * mu0) / (mu2 * routingMatrix.getElement(2, 0));
		double x3 = (5.0 * mu0 * routingMatrix.getElement(2, 3)) / (mu3 * routingMatrix.getElement(2, 0));
		
		double GN = 0.0;
		
		for(int i = 0; i < statusMatrix.getRows(); i++){
			double GNTerm = Math.pow(x0, statusMatrix.getElement(i, 0))
					* Math.pow(x1, statusMatrix.getElement(i, 1))
					* Math.pow(x2, statusMatrix.getElement(i, 2))
					* Math.pow(x3, statusMatrix.getElement(i, 3));
			GN += GNTerm;
		}
		
		System.out.println(GN);
	}

}
