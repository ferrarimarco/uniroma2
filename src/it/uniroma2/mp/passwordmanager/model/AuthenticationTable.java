package it.uniroma2.mp.passwordmanager.model;

import it.uniroma2.mp.passwordmanager.authentication.AuthenticationTableGenerator;

public class AuthenticationTable {

	private String[][] table;
	
	private int tableIndex;
	private String letterToAlwaysShow;
	
	public static final int tableRows = 4;
	public static final int tableColumns = 5;
	
	public AuthenticationTable(int tableIndex, String letterToAlwaysShow){
		this.tableIndex = tableIndex;
		this.letterToAlwaysShow = letterToAlwaysShow;
		
		AuthenticationTableGenerator authTableGenerator = new AuthenticationTableGenerator();
		table = authTableGenerator.generateTable(letterToAlwaysShow, tableRows, tableColumns);
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
