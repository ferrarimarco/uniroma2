package test;

import generators.UniformDoubleGenerator;

public class UniformDoubleGeneratorTest {

	
	public static void main(String args[]){
	
		UniformDoubleGenerator rngen2 = new UniformDoubleGenerator(101L);
		
		for(int i = 0; i<100; i++){
			System.out.println(rngen2.generateNextValue());
		}
		
		UniformDoubleGenerator rgen3 = new UniformDoubleGenerator(12L, 13L, 101L);
		
		for(int i = 0; i<100; i++){
			System.out.println(rgen3.generateNextValue());
		}
		
	}
}
