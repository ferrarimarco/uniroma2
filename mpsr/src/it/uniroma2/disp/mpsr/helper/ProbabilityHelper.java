package it.uniroma2.disp.mpsr.helper;

import it.uniroma2.disp.mpsr.model.Node;

import java.util.List;

public class ProbabilityHelper {
	
	public static double computeStatusProbability(List<Integer> status, List<Node> nodes, List<Double> x, double GN){
		return MpsrComputationHelper.multiplier(status, nodes, x) / GN;
	}	
}
