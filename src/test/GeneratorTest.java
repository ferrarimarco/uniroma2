package test;

import io.FileOutput;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class GeneratorTest {

	private ArrayList<String> distrib;
	private ArrayList<String> tempi;
	private String timeString;
	private String resultString;
	private DecimalFormat df;
	
	public GeneratorTest(){
		distrib = new ArrayList<String>();
		tempi = new ArrayList<String>();
		timeString = "";
		resultString = "";
		
		df = new DecimalFormat("##.#############");
	}
	
	public void elaborateNewValue(Double time, Double result){
		timeString = df.format(time);
		resultString = df.format(result);

		distrib.add(resultString);
		tempi.add(timeString);
	}
	
	public void finalize(String resultsPath){
		FileOutput.textFileWriter(distrib, resultsPath + "distrib.txt", true, false, true);
		FileOutput.textFileWriter(tempi, resultsPath + "tempi.txt", true, false, true);
	}
}
