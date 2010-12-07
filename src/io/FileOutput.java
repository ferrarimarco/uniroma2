package io;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * This class contains static methods to write data to files.
 * 
 * @author Marco Ferrari
 */
public class FileOutput {
	
	private static BufferedWriter bufferedWriter;
	
	public synchronized static void textFileWriter(ArrayList<String> source, String filePath, Boolean append, Boolean displayOutput, Boolean newLine) {
		
		String data;
		
		for (int i = 0; i < source.size(); i++) {
			
			data = source.get(i);
			FileOutput.writeLineToTextFile(data, filePath, append, displayOutput, newLine);
			
		}
	}
	
	public synchronized static void writeLineToTextFile(String data, String filePath, Boolean append, Boolean displayOutput, Boolean newLine) {
		
		try {
			bufferedWriter = new BufferedWriter(new FileWriter(filePath, append));
			bufferedWriter.write(data);
			bufferedWriter.newLine();
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				bufferedWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}	
	}
	
}
