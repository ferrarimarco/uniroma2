package test;

import generators.RnLongRangeGenerator;
import generators.RnDoubleRangeGenerator;

public class RnRangeTest {

	public static void main(String[] args) {
		
		RnDoubleRangeGenerator rngen2 = new RnDoubleRangeGenerator(2L, 78L, 101L);
		
		for(int i = 0; i<100; i++){
			System.out.println(rngen2.getNextValue());
		}
		
		System.out.println("Generatore altro tipo");
		
		RnLongRangeGenerator rngen1 = new RnLongRangeGenerator(2L, 78L, 101L);
		
		for(int i = 0; i<100; i++){
			System.out.println(rngen1.getNextValue());
		}
	}

}
