package it.uniroma2.disp.mpsr.init;

import java.util.ArrayList;
import java.util.List;

public class MuGenerator {
	
	public static List<Double> computeMus(int nodesNumber){
		List<Double> mu = new ArrayList<Double>(nodesNumber);
		
		mu.add(1.0/7.0); // client1
		mu.add(1/0.3); // FE server
		mu.add(1/0.08); // BE server
		mu.add(1.0/7.0); // client2
		
		return mu;
	}
	
	public static List<Double> computeMusMVA(int nodesNumber){
		List<Double> muMVA = computeMus(nodesNumber);

		muMVA.add(1.0/0.05);
		muMVA.add(1.0/0.05);
		
		return muMVA;
	}	
}
