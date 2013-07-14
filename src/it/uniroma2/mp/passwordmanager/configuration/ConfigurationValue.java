package it.uniroma2.mp.passwordmanager.configuration;


public enum ConfigurationValue {
	OK("1"),
	NOT_OK("0");
	
	private String value;
	
	private ConfigurationValue(String value){
		this.value = value;
	}
	
	@Override
	public String toString(){
		return value;
	}
}
