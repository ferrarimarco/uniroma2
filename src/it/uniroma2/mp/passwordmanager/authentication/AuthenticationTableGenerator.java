package it.uniroma2.mp.passwordmanager.authentication;

import java.util.Random;


public class AuthenticationTableGenerator {

	private static final String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
	
	public String[][] generateTable(String letterToAlwaysShow, int rows, int columns){
		
		String[][] table = new String[rows][columns];
		
		Random rand = new Random();
		
		// Initialization
		for(int i = 0; i < table.length; i++){
			for(int j = 0; j < table[i].length; j++){
				int randInt = rand.nextInt(letters.length());
				
				table[i][j] = letters.substring(randInt, randInt + 1);
			}
		}
		
		// Put the letter to always show in a random position
		int rowIndex = rand.nextInt(rows);
		int columnIndex = rand.nextInt(columns);
		
		table[rowIndex][columnIndex] = letterToAlwaysShow;
		
		
		return table;
	}
	
}
