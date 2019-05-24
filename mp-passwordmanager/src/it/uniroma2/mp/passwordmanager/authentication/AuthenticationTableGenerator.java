package it.uniroma2.mp.passwordmanager.authentication;

import java.util.Random;

/***
 * Classe che si occupa della generazione e dello scramble delle Tabelle di Autinticazione
 * **/
public class AuthenticationTableGenerator {

	public static final int passwordLength = 8;
	private static final int tableRows = 4;
	private static final int tableColumns = 4;
	
	private static final String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
			"abcdefghijklmnopqrstuvwxyz";
	
	/***
	 * Genera una tabella per l'autenticazione a partire da una lettera.
	 * @param letterToAlwaysShow indica la lettera della MasterPassword da inserire tra quelle della tabella
	 * @return table tabella di lettere scelte casuali più letterToAlwaysShow
	 * **/
	public String[][] generateTable(String letterToAlwaysShow){
		
		String[][] table = new String[tableRows][tableColumns];
		
		int letterToAlwaysShowIndex = letters.indexOf(letterToAlwaysShow);
		
		StringBuilder availableLetters = new StringBuilder(letters.length());
		
		if(letterToAlwaysShowIndex == 0){
			availableLetters.append(letters.substring(1));
		}else if(letterToAlwaysShowIndex == letters.length() - 1){
			availableLetters.append(letters.substring(0, letters.length() - 1));
		}else{
			availableLetters.append(letters.substring(0, letterToAlwaysShowIndex) + letters.substring(letterToAlwaysShowIndex + 1, letters.length()));
		}
		
		Random rand = new Random();
		
		for(int i = 0; i < table.length; i++){
			for(int j = 0; j < table[i].length; j++){

				// to have all different letters in a table
				int randInt = rand.nextInt(availableLetters.length());
				
				table[i][j] = availableLetters.substring(randInt, randInt + 1);
				
				availableLetters.deleteCharAt(randInt);
			}
		}
		
		// Put the letter to always show in a random position
		int rowIndex = rand.nextInt(tableRows);
		int columnIndex = rand.nextInt(tableColumns);
		
		table[rowIndex][columnIndex] = letterToAlwaysShow;
		
		return table;
	}
	
	/***
	 * Mischia le lettere della tabella
	 * @param tableToScramble tabella di input
	 * @return scrambledTable tabella di output
	 * **/
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
