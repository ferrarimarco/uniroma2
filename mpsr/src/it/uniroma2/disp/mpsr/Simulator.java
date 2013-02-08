package it.uniroma2.disp.mpsr;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import info.ferrarimarco.java.helper.math.SimpleMatrix;
import it.uniroma2.disp.mpsr.helper.GNCalculator;
import it.uniroma2.disp.mpsr.helper.ProbabilityHelper;
import it.uniroma2.disp.mpsr.helper.SaturationHelper;
import it.uniroma2.disp.mpsr.init.MuGenerator;
import it.uniroma2.disp.mpsr.init.NodesInitializer;
import it.uniroma2.disp.mpsr.init.RoutingMatrixGenerator;
import it.uniroma2.disp.mpsr.init.StatusMatrixGenerator;
import it.uniroma2.disp.mpsr.init.XCalculator;
import it.uniroma2.disp.mpsr.model.Node;

public class Simulator {

	private static final int nodes = 4;
	private static final int users = 35;
	
	public static void main(String[] args) {
		
		double[] mu = MuGenerator.computeMus(nodes);

		StatusMatrixGenerator statusMatrixGenerator = new StatusMatrixGenerator(users, nodes);
		SimpleMatrix<Integer> statusMatrix = statusMatrixGenerator.generateStatusMatrix();
		
		SimpleMatrix<Double> routingMatrix = RoutingMatrixGenerator.generateRoutingMatrix(nodes);
		
		double[] x = XCalculator.computeX(nodes, routingMatrix, mu);
		
		for(int i = 0; i < mu.length; i++){
			System.out.println("mu_" + i + " = " + mu[i]);
		}
		
		for(int i = 0; i < x.length; i++){
			System.out.println("x_" + i + " = " + x[i]);
		}
		
		List<Node> nodesList = NodesInitializer.generateNodes(nodes);
		
		double GN = GNCalculator.computeGN(statusMatrix, x, nodesList);
		System.out.println("G(N): " + GN);
		
		double GN_1 = 1.0/GN;
		System.out.println("1/G(N): " + GN_1);
		
		int threshold = 1;
		
		Map<Integer,Double> saturationMap = new HashMap<Integer,Double>();
		
		// prob che nfe + nbe > soglia
		while(threshold <= users){
			
			double sum = 0;
			
			List<Integer> saturatedStatusesIndexes = SaturationHelper.saturatedStatusesFinder(threshold, statusMatrix, nodesList);
			
			for(int i = 0; i < saturatedStatusesIndexes.size(); i++){
				
				List<Integer> status = statusMatrix.getRow(saturatedStatusesIndexes.get(i));
				
				sum += ProbabilityHelper.computeStatusProbability(status, nodesList, x, GN);
			}
			
			saturationMap.put(threshold, sum);
			
			System.out.println(threshold + " " + sum);
			
			threshold++;
		}
	}
}
