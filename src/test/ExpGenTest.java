package test;

import generators.ExponentialGenerator;

public class ExpGenTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		
		ExponentialGenerator exp = new ExponentialGenerator(1L, 2.0);
		
		for(int i=0; i<100000; i++){
			System.out.println(exp.generateNextValue());
		}
	}

}
