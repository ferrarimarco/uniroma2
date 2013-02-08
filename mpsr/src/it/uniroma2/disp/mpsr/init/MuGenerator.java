package it.uniroma2.disp.mpsr.init;

public class MuGenerator {
	
	public static double[] computeMus(int nodes){
		double[] mu = new double[nodes];
		
		mu[0] = 1.0/7.0; // client1
		mu[1] = 1/0.3; // FE server
		mu[2] = 1/0.08; // BE server
		mu[3] = 1.0/7.0; // client2
		
		return mu;
	}
	
}
