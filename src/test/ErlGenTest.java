package test;

import generators.ErlangGenerator;
import io.FileOutput;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class ErlGenTest {

	public static void main(String[] args) {
		
		ErlangGenerator erl = new ErlangGenerator(1L, 2.0, 1);
		ArrayList<String> distrib = new ArrayList<String>();
		ArrayList<String> tempi = new ArrayList<String>();
		
		Double time = 0.0;
		Double result = 0.0;
		
		String timeString = "";
		String resultString = "";
		DecimalFormat df = new DecimalFormat("##.#############");

		for(int i=0; i<1000; i++){
			time = erl.generateNextValue();
			result = erl.getDensity(time);
			
			timeString = df.format(time);
			resultString = df.format(result);
			
			distrib.add(resultString);
			tempi.add(timeString);
		}
		
		FileOutput.textFileWriter(distrib, "C:\\Users\\Marco\\Desktop\\distribErl.txt", true, false, true);
		FileOutput.textFileWriter(tempi, "C:\\Users\\Marco\\Desktop\\tempiErl.txt", true, false, true);

	}

}
