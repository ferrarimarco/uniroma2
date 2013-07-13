package it.uniroma2.mp.passwordmanager.encryption;


public enum EncryptionAlgorithm {
	AES("AES"),
	BLOWFISH("BLOWFISH");
	
	private String value;
	
	private EncryptionAlgorithm(String value){
		this.value = value;
	}
	
	@Override
	public String toString(){
		return value;
	}
}
