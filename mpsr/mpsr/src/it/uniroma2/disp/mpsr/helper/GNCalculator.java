package it.uniroma2.disp.mpsr.helper;

import java.util.List;

import info.ferrarimarco.java.helper.math.SimpleMatrix;
import it.uniroma2.disp.mpsr.model.Node;

public class GNCalculator {

	public static double computeGN(SimpleMatrix<Integer> statusMatrix, List<Double> x, List<Node> nodes){
		
		double GN = 0.0;
		
		for(int i = 0; i < statusMatrix.getRows(); i++){
			GN += MpsrComputationHelper.multiplier(statusMatrix.getRow(i), nodes, x);
		}
		
		return GN;
	}
}
