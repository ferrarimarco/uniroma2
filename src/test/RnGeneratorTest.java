package test;

import generators.RnSequenceGenerator;

public class RnGeneratorTest {

	
	public static void main(String args[]){
		
		RnSequenceGenerator rngen1 = new RnSequenceGenerator();
		
		System.out.print("rngen1: ");
		
		for(int i = 0; i<100; i++){
			System.out.print(rngen1.getNextValue() + ", ");
		}
		System.out.println();
	
		System.out.println();
		
		System.out.print("rngen2: ");
		
		RnSequenceGenerator rngen2 = new RnSequenceGenerator(101L);
		
		for(int i = 0; i<100; i++){
			System.out.print(rngen2.getNextValue() + ", ");
		}	
	}
}
