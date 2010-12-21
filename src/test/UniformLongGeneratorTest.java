package test;

import generators.UniformLongGenerator;

public class UniformLongGeneratorTest {

	
	private static Long nextValue;
	
	public static void main(String[] args) {
		
		int[] testArray = new int[78];
		
		UniformLongGenerator rngen1 = new UniformLongGenerator(2L, 78L, 101L);
		
		for(int i = 0; i<1000000; i++){
			nextValue = rngen1.generateNextValue();
			testArray[nextValue.intValue()]++;
			//System.out.println(nextValue);
		}
		
		for(int i = 0; i< testArray.length; i++){
			System.out.println(testArray[i]);	
		}
	}

}
