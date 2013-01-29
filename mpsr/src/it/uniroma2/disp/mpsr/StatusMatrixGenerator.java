package it.uniroma2.disp.mpsr;

import java.util.ArrayList;

import info.ferrarimarco.java.helper.math.CombinatoricsHelper;
import info.ferrarimarco.java.helper.math.SimpleMatrix;

public class StatusMatrixGenerator {

	private SimpleMatrix<Integer> statusMatrix;
	private int users;
	private int rows;
	
	public StatusMatrixGenerator(int users, int nodes){
		
		rows = CombinatoricsHelper.binomialCoefficient(users + nodes - 1, nodes - 1);
		
		statusMatrix = new SimpleMatrix<Integer>(rows, nodes, 0);
		
		this.users = users;
		
	}
	
	public void generateStatusMatrix(){
		System.out.println(generateStatus(0, users, statusMatrix.getColumns()).toString());
	}
	
	public ArrayList<ArrayList<Integer>> generateStatus(int numStati, int numCentri, int numJob){
		
		int i, j, k, numSubStati;
		
		ArrayList<ArrayList<Integer>> stati_generati = new ArrayList<ArrayList<Integer>>(numCentri);
		
		
		for(int h = 0;h < numCentri;h++){
			stati_generati.add(new ArrayList<Integer>());
		}
		
		
		if(numCentri==1){
			
			stati_generati.get(0).add(numJob);
		}
		else if(numCentri==2){
			
			for(i = 0;i < numStati; i++){
				stati_generati.get(0).add(numJob-i);
				stati_generati.get(1).add(i);
			}
		}
		else if(numCentri>=3){
			
			for(j=0; j<numCentri; j++){
				if(j==0) stati_generati.get(j).add(numJob);
				else stati_generati.get(j).add(0);
			}
			
			for(i=1; i<=numJob; i++){
				numSubStati = calcola_card_E(i, numCentri - 1);
				ArrayList<ArrayList<Integer>> tmp_stati_generati = new ArrayList<ArrayList<Integer>>(numCentri-1);
				tmp_stati_generati = this.generateStatus(numSubStati, numCentri-1, i);

				for(j=1; j<numSubStati+1; j++){
					stati_generati.get(0).add(numJob-i);
					for(k=1; k<numCentri; k++){
						stati_generati.get(k).add(tmp_stati_generati.get(k-1).get(j-1));
					}
				}
			}
		}
		
		return stati_generati;
	}
	
	public int calcola_card_E(int users, int nodes){
		return CombinatoricsHelper.binomialCoefficient(users + nodes -1, nodes - 1);
	}
	
	
}
