package it.uniroma2.mp.passwordmanager.authentication;

import java.util.Random;


public class AuthenticationTableGenerator {

	private static final String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
	
	public String[][] generateTable(String letterToAlwaysShow, int rows, int columns){
		
		String[][] table = new String[rows][columns];
		
		Random rand = new Random();
		
		int letterToAlwaysShowIndex = letters.indexOf(letterToAlwaysShow);
		
		int[] alreadyChosen = new int[letters.length()];
		
		for(int i = 0; i < alreadyChosen.length; i++){
			alreadyChosen[i] = 0;
		}
		
		// Initialization
		for(int i = 0; i < table.length; i++){
			for(int j = 0; j < table[i].length; j++){
				int randInt = rand.nextInt(letters.length());
				
				// to have all different letters in a table
				while(alreadyChosen[randInt] != 0 || randInt == letterToAlwaysShowIndex){
					randInt = rand.nextInt(letters.length());
				}
				
				alreadyChosen[randInt]++;
				
				table[i][j] = letters.substring(randInt, randInt + 1);
			}
		}
		
		// Put the letter to always show in a random position
		int rowIndex = rand.nextInt(rows);
		int columnIndex = rand.nextInt(columns);
		
		table[rowIndex][columnIndex] = letterToAlwaysShow;
		
		
		return table;
	}
	
	public String[][] scrambleTable(String[][] tableToScramble){
		
		String[][] scrambledTable = new String[tableToScramble.length][tableToScramble[0].length];
		
		for(int i = 0; i < scrambledTable.length; i++){
			for(int j = 0; j < tableToScramble[i].length; j++){
				scrambledTable[i][j] = "-";
			}
		}

		Random rand = new Random();
		
		// Initialization
		for(int i = 0; i < tableToScramble.length; i++){
			for(int j = 0; j < tableToScramble[i].length; j++){
				
				// compute new coordinates for this element
				int newRow = rand.nextInt(tableToScramble.length);
				int newColumn = rand.nextInt(tableToScramble[i].length);
				
				while(!scrambledTable[newRow][newColumn].equals("-")){
					newRow = rand.nextInt(tableToScramble.length);
					newColumn = rand.nextInt(tableToScramble[i].length);
				}
				
				scrambledTable[newRow][newColumn] = tableToScramble[i][j];
			}
		}		
		
		return scrambledTable;
	}
	
}
