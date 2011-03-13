package test;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;

public class GeneratorTest {

	private String timeString;
	private String resultString;
	private DecimalFormat df;
	private BufferedWriter bufferedWriterTempi;
	private BufferedWriter bufferedWriterDens;
	
	public GeneratorTest(String resultsPath, Boolean append){
		timeString = "";
		resultString = "";
		
		df = new DecimalFormat("##.#############");
		try {
			bufferedWriterTempi = new BufferedWriter(new FileWriter(resultsPath + "Tempi.txt", append));
			bufferedWriterDens = new BufferedWriter(new FileWriter(resultsPath + "Dens.txt", append));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void elaborateNewValue(Double time, Double result){
		timeString = df.format(time);
		resultString = df.format(result);
		
		try {
			bufferedWriterTempi.write(timeString);
			bufferedWriterTempi.newLine();
			
			bufferedWriterDens.write(resultString);
			bufferedWriterDens.newLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void finalize(){
		try {
			bufferedWriterTempi.close();
			bufferedWriterDens.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
