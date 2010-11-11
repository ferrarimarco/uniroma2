package test;

import generators.RnGenerator;

public class RnGeneratorTest {

	
	public static void main(String args[]){
	
		RnGenerator rngen2 = new RnGenerator(101L);
		
		for(int i = 0; i<100; i++){
			System.out.println(rngen2.getNextValue());
		}	
	}
}
