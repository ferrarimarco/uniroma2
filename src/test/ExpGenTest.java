package test;

import java.text.DecimalFormat;
import java.util.ArrayList;
import io.FileOutput;
import generators.ExponentialGenerator;

public class ExpGenTest {

	public static void main(String[] args) {
	
		Double mean = 0.5;
		
		ExponentialGenerator exp = new ExponentialGenerator(1L, mean);
		ArrayList<String> distrib = new ArrayList<String>();
		ArrayList<String> tempi = new ArrayList<String>();
		
		Double time = 0.0;
		Double result = 0.0;
		
		String timeString = "";
		String resultString = "";
		
		for(int i=0; i<1000000; i++){
			time = exp.generateNextValue();
			result = exp.getDensity(time);
			
			DecimalFormat df = new DecimalFormat("##.##########");
			timeString = df.format(time);
			resultString = df.format(result);
			
			distrib.add(resultString);
			tempi.add(timeString);
		}
		
		FileOutput.textFileWriter(distrib, "C:\\Users\\Marco\\Desktop\\distrib.txt", true, false, true);
		FileOutput.textFileWriter(tempi, "C:\\Users\\Marco\\Desktop\\tempi.txt", true, false, true);

	}

}
