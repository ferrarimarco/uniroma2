package it.uniroma2.disp.mpsr;

import java.util.ArrayList;
import java.util.List;

import info.ferrarimarco.java.helper.math.CombinatoricsHelper;
import info.ferrarimarco.java.helper.math.SimpleMatrix;

public class StatusMatrixGenerator {

	private SimpleMatrix<Integer> statusMatrix;
	private int users;
	private int rows;
	private int nodes;

	public StatusMatrixGenerator(int users, int nodes){
		rows = CombinatoricsHelper.binomialCoefficient(users + nodes - 1, nodes - 1);
		this.users = users;
		this.nodes = nodes;

		statusMatrix = new SimpleMatrix<Integer>(rows, nodes, 0);
	}

	private int computeCardinality(int users, int nodes){
		return CombinatoricsHelper.binomialCoefficient(users + nodes -1, nodes - 1);
	}

	public SimpleMatrix<Integer> generateStatusMatrix(){
		List<List<Integer>> rawStatusMatrix = generateStatus(rows, nodes, users);

		for(int i = 0; i < rawStatusMatrix.size(); i++){
			for(int j = 0; j < rawStatusMatrix.get(i).size(); j++){
				statusMatrix.setElement(j, i, rawStatusMatrix.get(i).get(j));
			}
		}

		return statusMatrix;
	}

	private List<List<Integer>> generateStatus(int cardinality, int nodes, int users){

		int numSubStati;

		List<List<Integer>> stati_generati = new ArrayList<List<Integer>>(nodes);

		// columns initialization
		for(int q = 0; q < nodes; q++){
			stati_generati.add(new ArrayList<Integer>(cardinality));
		}

		if(nodes == 1){
			stati_generati.get(0).add(users);
		}else if(nodes==2){
			for(int i = 0;i < cardinality; i++){
				stati_generati.get(0).add(users-i);
				stati_generati.get(1).add(i);
			}
		}else if(nodes>=3){
			for(int j=0; j<nodes; j++){
				if(j==0) stati_generati.get(j).add(users);
				else stati_generati.get(j).add(0);
			}

			for(int i=1; i<=users; i++){
				numSubStati = computeCardinality(i, nodes - 1);
				List<List<Integer>> tmp_stati_generati = new ArrayList<List<Integer>>(nodes-1);
				tmp_stati_generati = this.generateStatus(numSubStati, nodes-1, i);

				for(int j=1; j<numSubStati+1; j++){
					stati_generati.get(0).add(users-i);
					for(int k=1; k<nodes; k++){
						stati_generati.get(k).add(tmp_stati_generati.get(k-1).get(j-1));
					}
				}
			}
		}

		return stati_generati;
	}       
}