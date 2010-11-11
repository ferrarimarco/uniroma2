package test;

import generators.RnLongRangeGenerator;

public class RnRangeTest {

	
	private static Long nextValue;
	
	public static void main(String[] args) {
		
		int[] testArray = new int[78];
		
		RnLongRangeGenerator rngen1 = new RnLongRangeGenerator(2L, 78L, 101L);
		
		for(int i = 0; i<1000000; i++){
			nextValue = rngen1.getNextValue();
			testArray[nextValue.intValue()]++;
			//System.out.println(nextValue);
		}
		
		for(int i = 0; i< testArray.length; i++){
			System.out.println(testArray[i]);	
		}
	}

}
