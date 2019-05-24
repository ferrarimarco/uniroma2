package it.uniroma2.disp.mpsr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import info.ferrarimarco.java.helper.io.IoHelper;
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
		
		StatusMatrixGenerator statusMatrixGenerator = new StatusMatrixGenerator(users, nodes);
		SimpleMatrix<Integer> statusMatrix = statusMatrixGenerator.generateStatusMatrix();
		
		SimpleMatrix<Double> routingMatrix = RoutingMatrixGenerator.generateRoutingMatrix(nodes);
		
		List<Double> mu = MuGenerator.computeMus(nodes);		
		List<Double> x = XYCalculator.computeX(nodes, routingMatrix, mu);
		List<Node> nodesList = NodesInitializer.generateNodes(nodes);
		
		try {
			IoHelper.writeStringToFile(statusMatrix.toString(), "results\\statusMatrix.txt");
			IoHelper.writeStringToFile(routingMatrix.toString(), "results\\routingMatrix.txt");
			IoHelper.writeCollectionToFile(mu, "results\\mu.txt");
			IoHelper.writeCollectionToFile(x, "results\\x.txt");
			IoHelper.writeCollectionToFile(nodesList, "results\\nodes.txt");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		double GN = GNCalculator.computeGN(statusMatrix, x, nodesList);
		double GN_1 = 1.0/GN;
		
		try {
			IoHelper.writeStringToFile("G(N) = " + Double.toString(GN), "results\\GN.txt");
			IoHelper.writeStringToFile("1/G(N) = " + Double.toString(GN_1), "results\\GN.txt", true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
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
			
			List<List<Integer>> saturatedStatuses = new ArrayList<List<Integer>>();
			
			for(int i = 0; i < saturatedStatusesIndexes.size(); i++){
				saturatedStatuses.add(statusMatrix.getRow(saturatedStatusesIndexes.get(i)));
			}
			
			try {
				IoHelper.writeStringToFile("-------------", "results\\thresholdProbability.txt", true);
				IoHelper.writeStringToFile("THRESHOLD = " + threshold, "results\\thresholdProbability.txt", true);
				IoHelper.writeStringToFile("P(Nbe + Nfe > threshold) (STATUS PROB SUM) = " + sum, "results\\thresholdProbability.txt", true);
				IoHelper.writeStringToFile("SATURATED STATUSES:", "results\\thresholdProbability.txt", true);
				IoHelper.writeCollectionToFile(saturatedStatuses, "results\\thresholdProbability.txt", true);
				IoHelper.writeStringToFile("-------------", "results\\thresholdProbability.txt", true);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			threshold++;
		}
		
		// MVA Begins HERE
		
		SimpleMatrix<Double> routingMatrixMVA = RoutingMatrixGenerator.generateRoutingMatrixMVA(nodesMVA);
		List<Double> muMVA = MuGenerator.computeMusMVA(nodesMVA);
		List<Node> nodesListMVA = NodesInitializer.generatesNodesForMVA(nodesMVA);
		
		try {
			IoHelper.writeCollectionToFile(muMVA, "results\\muMVA.txt");
			IoHelper.writeCollectionToFile(nodesListMVA, "results\\nodesMVA.txt");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		double minTr = 0.0;
		int min_Threshold_1 = 0;
		int min_Threshold_2 = 0;
		double p_c1_c1_rj_minTr = 0.0;
		double p_c2_c2_rj_minTr = 0.0;
		
		try {
			IoHelper.writeStringToFile("MVA", "results\\MVA.txt");
			IoHelper.writeStringToFile("", "results\\MVA_thresholds_trc1.txt");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			IoHelper.writeStringToFile("-------------", "results\\MVADetailsEni.txt");
			IoHelper.writeStringToFile("-------------", "results\\MVADetailsEti.txt");
			IoHelper.writeStringToFile("-------------", "results\\MVADetailsRhoi.txt");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		for(int i = 1; i <= saturationMap.size(); i++){
			for(int j = 1; j <= saturationMap.size(); j++){
				
				int threshold_1 = i;
				int threshold_2 = j;
				
				double p_c1_c1_rj = saturationMap.get(threshold_1);
				routingMatrixMVA.setElement(0, 4, p_c1_c1_rj);
				routingMatrixMVA.setElement(0, 1, 1 - p_c1_c1_rj);
				
				double p_c2_c2_rj = saturationMap.get(threshold_2);
				routingMatrixMVA.setElement(3, 5, p_c2_c2_rj);
				routingMatrixMVA.setElement(3, 1, 1- p_c2_c2_rj);
				
				List<Double> yMVA = XYCalculator.computeYMVA(nodesMVA, routingMatrixMVA, muMVA);
				
				SimpleMatrix<Double> visits = MpsrComputationHelper.computeRelativeVisits(yMVA);
				
				try {
					IoHelper.writeStringToFile("THR_1: " + threshold_1 + ", THR_2: " + threshold_2, "results\\MVADetailsEni.txt", true);
					IoHelper.writeStringToFile("THR_1: " + threshold_1 + ", THR_2: " + threshold_2, "results\\MVADetailsEti.txt", true);
					IoHelper.writeStringToFile("THR_1: " + threshold_1 + ", THR_2: " + threshold_2, "results\\MVADetailsRhoi.txt", true);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				double tr_i = MpsrComputationHelper.mva(muMVA, visits, nodesListMVA, users);
				
				// Check if the probabilities are in range				
				if(p_c1_c1_rj <= 0.20 && p_c2_c2_rj <= 0.20){
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
				
				try {
					IoHelper.writeStringToFile("-------------", "results\\MVA.txt", true);
					IoHelper.writeStringToFile("THR_1: " + threshold_1 + ", THR_2: " + threshold_2, "results\\MVA.txt", true);
					IoHelper.writeStringToFile("Trc1:", "results\\MVA.txt", true);
					IoHelper.writeStringToFile(Double.toString(tr_i), "results\\MVA.txt", true);
					IoHelper.writeStringToFile("ROUTING MATRIX:", "results\\MVA.txt", true);
					IoHelper.writeStringToFile(routingMatrixMVA.toString(), "results\\MVA.txt", true);
					IoHelper.writeStringToFile("Y[i]:", "results\\MVA.txt", true);
					IoHelper.writeCollectionToFile(yMVA, "results\\MVA.txt", true);
					IoHelper.writeStringToFile("Visits (V[i,j]):", "results\\MVA.txt", true);
					IoHelper.writeStringToFile(visits.toString(), "results\\MVA.txt", true);
					
					IoHelper.writeStringToFile(threshold_1 + ":" + threshold_2 + ";" + Double.toString(tr_i), "results\\MVA_thresholds_trc1.txt", true);					
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		try {
			IoHelper.writeStringToFile("-------------", "results\\MVAMinThr.txt");
			IoHelper.writeStringToFile("THR_1: " + min_Threshold_1 + ", THR_2: " + min_Threshold_2, "results\\MVAMinThr.txt", true);
			IoHelper.writeStringToFile("p_c1_c1_rj_minTr:" + p_c1_c1_rj_minTr, "results\\MVAMinThr.txt", true);
			IoHelper.writeStringToFile("p_c2_c2_rj_minTr:" + p_c2_c2_rj_minTr, "results\\MVAMinThr.txt", true);
			IoHelper.writeStringToFile("minTr:" + minTr, "results\\MVAMinThr.txt", true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
