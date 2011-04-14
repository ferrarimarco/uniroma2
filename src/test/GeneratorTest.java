package test;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;

public class GeneratorTest {

	private BufferedWriter bufferedWriter;
	private Integer[] results;
	private DecimalFormat df;
	
	public GeneratorTest(String resultsPath, Boolean append){

		results = new Integer[120];
		
		df = new DecimalFormat("#.########");
		
		//Array initialization
		for(int i=0; i < results.length; i++)
			results[i]=0;

		try {
			bufferedWriter = new BufferedWriter(new FileWriter(resultsPath, append));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void elaborateNewValue(Double time){

		try {
			bufferedWriter.write(df.format(time).toString());
			bufferedWriter.newLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(time >= results.length)
			results[results.length - 1]++;
		else{
			results[time.intValue()]++;
		}
	}
	
	public void finalizeElaboration()
	{
		try {
			bufferedWriter.newLine();
			
			for(int i=0; i<results.length; i++)
			{
				bufferedWriter.write(results[i].toString());
				bufferedWriter.newLine();
			}
			
			bufferedWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
