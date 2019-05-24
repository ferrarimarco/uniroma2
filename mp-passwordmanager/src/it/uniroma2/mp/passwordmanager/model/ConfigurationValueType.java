package it.uniroma2.mp.passwordmanager.model;

/***
 * Classe per gestire gli eventuali valori di configurazione: 
 * **/

public enum ConfigurationValueType {
	MASTER_PASSWORD_INITIALIZED("master_password_init"),
	BACK_BUTTON_PRESSED("back_button_pressed"),
	;
	
	private String value;
	
	private ConfigurationValueType(String value){
		this.value = value;
	}
	
	public String toString(){
		return value;
	}
}
