package it.uniroma2.disp.mpsr;

import info.ferrarimarco.java.helper.math.DigitsHelper;
import info.ferrarimarco.java.helper.math.SimpleMatrix;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class StatusMatrixGenerator {

	public static SimpleMatrix<Integer> generateStatus(int nodes, int users){
		
		List<ArrayList<Integer>> matrix = new ArrayList<ArrayList<Integer>>();
		
		int powArgument = nodes - 1;
		
		int maximum = (int) (users * Math.pow(10, powArgument));
		
		List<Integer> numbersToWrite = new ArrayList<Integer>(maximum);
		
		for(int i = 1; i < maximum + 1; i++){
			
			int sum = DigitsHelper.computeDigitsSum(i);
			
			if(sum == users){
				numbersToWrite.add(i);
			}		
		}
		
		Collections.sort(numbersToWrite, Collections.reverseOrder());
		
		for(int i = 0; i < numbersToWrite.size(); i++){
			
			ArrayList<Integer> row = new ArrayList<Integer>(nodes);
			
			for(int j = 0; j < nodes; j++){
				row.add(0);
			}
			
			int num = numbersToWrite.get(i);
			int zeroes = row.size() - DigitsHelper.getNumberLenght(num);
			
			for(int j = zeroes; j < row.size(); j++){
				row.set(j, DigitsHelper.getIthDigit(j + 1 - zeroes, num));
			}
			
			matrix.add(row);
		}
		
		SimpleMatrix<Integer> simpleMatrix = new SimpleMatrix<Integer>(matrix.size(), matrix.get(0).size(), 0);
		
		for(int i = 0; i < matrix.size(); i++){
			for(int j = 0; j < matrix.get(i).size(); j++){
				simpleMatrix.setElement(i, j, matrix.get(i).get(j));
			}
		}
		
		return simpleMatrix;
	}
	

}
