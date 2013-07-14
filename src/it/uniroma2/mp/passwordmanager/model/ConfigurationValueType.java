package it.uniroma2.mp.passwordmanager.model;


public enum ConfigurationValueType {
	MASTER_PASSWORD_INITIALIZED("master_password_init");
	
	private String value;
	
	private ConfigurationValueType(String value){
		this.value = value;
	}
	
	public String toString(){
		return value;
	}
}
