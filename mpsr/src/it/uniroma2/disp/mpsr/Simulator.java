package it.uniroma2.disp.mpsr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import info.ferrarimarco.java.helper.math.SimpleMatrix;
import it.uniroma2.disp.mpsr.helper.GNCalculator;
import it.uniroma2.disp.mpsr.helper.MpsrComputationHelper;
import it.uniroma2.disp.mpsr.helper.ProbabilityHelper;
import it.uniroma2.disp.mpsr.helper.SaturationHelper;
import it.uniroma2.disp.mpsr.init.MuGenerator;
import it.uniroma2.disp.mpsr.init.NodesInitializer;
import it.uniroma2.disp.mpsr.init.RoutingMatrixGenerator;
import it.uniroma2.disp.mpsr.init.StatusMatrixGenerator;
import it.uniroma2.disp.mpsr.init.XYCalculator;
import it.uniroma2.disp.mpsr.model.Node;

public class Simulator {

	private static final int nodes = 4;
	private static final int nodesMVA = nodes + 2;
	private static final int users = 35;
	
	public static void main(String[] args) {
		
		double[] mu = MuGenerator.computeMus(nodes);

		StatusMatrixGenerator statusMatrixGenerator = new StatusMatrixGenerator(users, nodes);
		SimpleMatrix<Integer> statusMatrix = statusMatrixGenerator.generateStatusMatrix();
		
		SimpleMatrix<Double> routingMatrix = RoutingMatrixGenerator.generateRoutingMatrix(nodes);
		
		double[] x = XYCalculator.computeX(nodes, routingMatrix, mu);
		
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
		
		// MVA Begins HERE
		
		
		List<Double> muMVA = new ArrayList<Double>(nodesMVA);
		
		// Can't use new ArrayList<>(Arrays.asList(mu)) because mu is a list of doubles and not of Doubles
		for(int i = 0; i < mu.length; i++){
			muMVA.add(mu[i]);
		}

		double mu4 = 1.0/0.05;
		double mu5 = mu4;
		muMVA.add(mu4);
		muMVA.add(mu5);		
		
		SimpleMatrix<Double> routingMatrixMVA = RoutingMatrixGenerator.generateRoutingMatrixMVA(nodesMVA);
		
		List<Node> nodesListMVA = NodesInitializer.generatesNodesForMVA(nodesMVA);
		
		double minTr = 0.0;
		int min_Threshold_1 = 0;
		int min_Threshold_2 = 0;
		double p_c1_c1_rj_minTr = 0.0;
		double p_c2_c2_rj_minTr = 0.0;
		
		for(int i = 1; i <= saturationMap.size(); i++){
			for(int j = 1; j <= saturationMap.size(); j++){
				
				int threshold_1 = i;
				int threshold_2 = j;
				
				System.out.println("S1: " + threshold_1 + ", S2: " + threshold_2);
				
				double p_c1_c1_rj = saturationMap.get(threshold_1);
				routingMatrixMVA.setElement(0, 4, p_c1_c1_rj);
				routingMatrixMVA.setElement(0, 1, 1 - p_c1_c1_rj);
				
				double p_c2_c2_rj = saturationMap.get(threshold_2);
				routingMatrixMVA.setElement(3, 5, p_c2_c2_rj);
				routingMatrixMVA.setElement(3, 1, 1- p_c2_c2_rj);
				
				double[] yMVA = XYCalculator.computeYMVA(nodesMVA, routingMatrixMVA, muMVA);
				
				for(int k = 0; k < yMVA.length; k++){
					System.out.println("y[" + k + "]: " + yMVA[k]);
				}
				
				SimpleMatrix<Double> visits = MpsrComputationHelper.computeRelativeVisits(yMVA);
				
				double tr_i = MpsrComputationHelper.mva(muMVA, visits, nodesListMVA, users);
				
				// Check if the probabilities are in range
				if(p_c1_c1_rj <= 0.25 && p_c2_c2_rj <= 0.25){
					if(minTr == 0.0){
						minTr = tr_i;
					}else{
						if(tr_i < minTr){
							min_Threshold_1 = threshold_1;
							min_Threshold_2 = threshold_2;
							
							minTr = tr_i;
							
							p_c1_c1_rj_minTr = p_c1_c1_rj;
							p_c2_c2_rj_minTr = p_c2_c2_rj;
						}
					}
				}
			}
		}
		
		System.out.println("minTr = " + minTr);
		System.out.println("min_Threshold_1 = " + min_Threshold_1);
		System.out.println("min_Threshold_2 = " + min_Threshold_2);
		System.out.println("p_c1_c1_rj_minTr = " + p_c1_c1_rj_minTr);
		System.out.println("p_c2_c2_rj_minTr = " + p_c2_c2_rj_minTr);
	}
}
