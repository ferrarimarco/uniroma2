package test;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class GeneratorTest {

	private BufferedWriter bufferedWriter;
	private Integer[] results;
	
	public GeneratorTest(String resultsPath, Boolean append){

		results = new Integer[120];
		
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

		if(time >= results.length)
			results[results.length - 1]++;
		else{
			results[time.intValue()]++;
		}
	}
	
	public void finalizeElaboration()
	{
		try {
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
