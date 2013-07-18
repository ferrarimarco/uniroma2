package it.uniroma2.mp.passwordmanager.model;

import it.uniroma2.mp.passwordmanager.authentication.AuthenticationTableGenerator;

/***
 * Questa classe modella l'entità "Tabella di Autenticazione" esponendo all'esterno
 * tutti i metodi get e set per i suoi attributi
 * **/

public class AuthenticationTable {

	//Variabile contenente la tabella di autenticazione
	private String[][] table;
	
	//Variabile contenente il numero identificativo della tabella
	private int tableIndex;
	
	//Variabile contenente la lettara apartenente alla MasterPassword
	private String letterToAlwaysShow;
	
	public static final int tableRows = 4;
	public static final int tableColumns = 5;
	
	//Metodo costruttore che genera la tabella di autenticazione
	public AuthenticationTable(int tableIndex, String letterToAlwaysShow){
		this.tableIndex = tableIndex;
		this.letterToAlwaysShow = letterToAlwaysShow;
		
		AuthenticationTableGenerator authTableGenerator = new AuthenticationTableGenerator();
		table = authTableGenerator.generateTable(letterToAlwaysShow);
	}
	
	public String[][] getTable() {
		return table;
	}

	
	public void setTable(String[][] table) {
		this.table = table;
	}

	
	public int getTableIndex() {
		return tableIndex;
	}

	
	public void setTableIndex(int tableIndex) {
		this.tableIndex = tableIndex;
	}

	
	public String getLetterToAlwaysShow() {
		return letterToAlwaysShow;
	}

	
	public void setLetterToAlwaysShow(String letterToAlwaysShow) {
		this.letterToAlwaysShow = letterToAlwaysShow;
	}
	
}
