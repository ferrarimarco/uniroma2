package it.uniroma2.disp.mpsr.helper;

import info.ferrarimarco.java.helper.math.ComputationHelper;
import it.uniroma2.disp.mpsr.model.Node;

import java.util.List;

public class MpsrComputationHelper {
	
	public static double multiplier(List<Integer> status, List<Node> nodes, double[] x){
		
		double result = 1.0;
		
		for(int i = 0; i < status.size(); i++){
			result = result * computeTerm(nodes.get(i), x[i], status.get(i));
		}
		
		return result;
	}
	
	private static double computeTerm(Node node, double x, int statusComponent){
		double result = 1.0;
		
		if(node.isInstantService()){
			// beta_i(n_i) = n_i! for nodes != single processor node
			result = result * (Math.pow(x, statusComponent)/ ComputationHelper.factorial(statusComponent));
		}
		else{
			result = result * Math.pow(x, statusComponent);
		}
		
		return result;
	}
}
