package test;

import io.FileOutput;
import generators.UniformLongGenerator;

public class UniformLongGeneratorTest {

	public static void main(String[] args) {
		
		Integer[] testArray = new Integer[78];
		
		UniformLongGenerator rngen1 = new UniformLongGenerator(2L, 78L, 101L);
		Long nextValue;
		
		for(int i = 0; i<1000000; i++){
			nextValue = rngen1.generateNextValue();
			testArray[nextValue.intValue()]++;
		}
		
		FileOutput.textFileWriterIntArray(testArray, "C:\\distribUnifLongGen.txt", true, false, true);
	}

}
