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
	private Integer[] occorrenze;
	
	public GeneratorTest(String resultsPath, Boolean append){
		timeString = "";
		resultString = "";
		//Solo per test con Interi
		occorrenze = new Integer[120];
		for(int i=0; i < occorrenze.length; i++)
			occorrenze[i]=0;
		
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
	
	public void elaborateNewValueInt(Double time){
		
		//Inseriamo i valori solo se compatibili con le dimensione dell'Array
		if (time.intValue() < occorrenze.length)
		occorrenze[time.intValue()]++;
	}
	
	public void finalizeInt()
	{
		try {
			for(int i=0; i<occorrenze.length; i++)
			{
				bufferedWriterTempi.write(occorrenze[i].toString());
				bufferedWriterTempi.newLine();
			}
			
			bufferedWriterTempi.close();
			bufferedWriterDens.close();
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
