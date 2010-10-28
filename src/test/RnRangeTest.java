package test;

import generators.RnRangeGenerator;

public class RnRangeTest {

	public static void main(String[] args) {
		RnRangeGenerator rngen1 = new RnRangeGenerator(2L, 78L);
		
		System.out.print("rngen1: ");
		
		for(int i = 0; i<100; i++){
			System.out.print(rngen1.getNextValue() + ", ");
		}
	}

}
