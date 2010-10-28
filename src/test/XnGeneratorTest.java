package test;

import generators.XnSequenceGenerator;

public class XnGeneratorTest {


	public static void main(String args[]){

		XnSequenceGenerator gen1 = new XnSequenceGenerator();
		
		System.out.print("gen1: ");
		
		for(int i = 0; i<100; i++){
			System.out.print(gen1.getNextValue() + ", ");
		}
		
		System.out.println();

		System.out.print("gen2: ");

		XnSequenceGenerator gen2 = new XnSequenceGenerator(101L);
		
		for(int i = 0; i<100; i++){
			System.out.print(gen2.getNextValue() + ", ");
		}
		
		System.out.println();
	}

}
