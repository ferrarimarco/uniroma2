package io;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * This class contains static methods to write data to files.
 * 
 * @author Marco Ferrari
 */
public class FileOutput {
	
	private BufferedWriter bufferedWriter;
	
	public FileOutput(String filePath, Boolean append){
		
		try {
			bufferedWriter = new BufferedWriter(new FileWriter(filePath, append));
		
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void writeLineToTextFile(String data) {
		
		try {
			bufferedWriter.write(data);
			bufferedWriter.newLine();
			
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	public void closeWriter(){
		try {
			bufferedWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
