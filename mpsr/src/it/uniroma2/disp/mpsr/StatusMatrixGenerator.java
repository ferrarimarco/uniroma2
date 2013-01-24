package it.uniroma2.disp.mpsr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class StatusMatrixGenerator {
	
	public static void main(String[] args){
		
		int number = 123;
		int length = getNumberLenght(number);

		System.out.println("Len: " + length);
		
		for(int i = 1; i <= length; i++){
			System.out.println(i + ":" + getIthDigit(i, number));
		}
		
		System.out.println("Digits sum: " + computeDigitsSum(number));
		
		generateStatus(4, 3);	
		
	}

	public static void generateStatus(int nodes, int users){
		
		List<ArrayList<Integer>> matrix = new ArrayList<ArrayList<Integer>>();
		
		int maximum = (int) (users * Math.pow(10, nodes - 1));
		
		List<Integer> numbersToWrite = new ArrayList<Integer>(maximum);
		
		for(int i = 1; i < maximum + 1; i++){
			
			int sum = computeDigitsSum(i);
			
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
			int zeroes = row.size() - getNumberLenght(num);
			
			for(int j = zeroes; j < row.size(); j++){
				row.set(j, getIthDigit(j + 1 - zeroes, num));
			}
			
			System.out.println(Arrays.toString(row.toArray(new Integer[row.size()])));
			matrix.add(row);
		}	
	}
	
	private static int computeDigitsSum(int number){
		
		int length = getNumberLenght(number);
		
		int sum = 0;
		
		for(int i = 1; i < length + 1; i++){
			int ithDigit = getIthDigit(i, number);
			sum += ithDigit;
		}
		
		return sum;
	}
	
	private static int getIthDigit(int i, int number){
		
		int len = getNumberLenght(number);
		int divisor = (int) Math.pow(10, len - i);
		return (number / divisor) % 10;
	}
	
	private static int getNumberLenght(int number){
		return (number == 0) ? 1 : (int) Math.log10(number) + 1;
	}
}
