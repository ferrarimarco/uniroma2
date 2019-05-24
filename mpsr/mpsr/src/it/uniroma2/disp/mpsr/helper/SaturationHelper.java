package it.uniroma2.disp.mpsr.helper;

import info.ferrarimarco.java.helper.math.SimpleMatrix;
import it.uniroma2.disp.mpsr.model.Node;

import java.util.ArrayList;
import java.util.List;

public class SaturationHelper {
	
	public static List<Integer> saturatedStatusesFinder(int threshold, SimpleMatrix<Integer> statusMatrix, List<Node> nodes){
		
		List<Integer> result = new ArrayList<Integer>();
		
		for(int i = 0; i < statusMatrix.getRows(); i++){
			
			int sumStatusJobs = 0;
			
			for(int j = 0; j < nodes.size(); j++){
				if(!nodes.get(j).isInstantService()){
					sumStatusJobs = sumStatusJobs + statusMatrix.getElement(i, j);
				}
			}
			if(sumStatusJobs >= threshold){
				result.add(i);
			}
		}
		
		return result;
	}	
}
