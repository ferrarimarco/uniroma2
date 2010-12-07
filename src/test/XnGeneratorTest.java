package test;

import generators.XnGenerator;

public class XnGeneratorTest {


	public static void main(String args[]){

		XnGenerator gen2 = new XnGenerator(101L);
		
		for(int i = 0; i<10000; i++){
			System.out.println(gen2.generateNextValue());
		}
	}

}
