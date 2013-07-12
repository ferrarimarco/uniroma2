package it.uniroma2.mp.passwordmanager.encryption;


public enum EncryptionAlgorithm {
	AES_128("AES_128");
	
	private String value;
	
	private EncryptionAlgorithm(String value){
		this.value = value;
	}
	
	@Override
	public String toString(){
		return value;
	}
}
