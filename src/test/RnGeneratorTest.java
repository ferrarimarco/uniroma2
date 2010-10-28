package test;

import generators.RnSequenceGenerator;

public class RnGeneratorTest {

	
	public static void main(String args[]){
	
		RnSequenceGenerator rngen2 = new RnSequenceGenerator(101L);
		
		for(int i = 0; i<100; i++){
			System.out.println(rngen2.getNextValue());
		}	
	}
}
