package test;

import generators.XnSequenceGenerator;

public class XnGeneratorTest {


	public static void main(String args[]){

		XnSequenceGenerator gen2 = new XnSequenceGenerator(101L);
		
		for(int i = 0; i<10000; i++){
			System.out.println(gen2.getNextValue());
		}
	}

}
