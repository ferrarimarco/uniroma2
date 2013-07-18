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
	
	/***
	 * Prende in ingresso un stringa e restituisce un oggetto di tipo EncryptionAlgorithm
	 * @param value
	 * @return result
	 * **/
	public static EncryptionAlgorithm getEncryptionAlgorithmFromValue(String value){
		
		EncryptionAlgorithm result = null;
		
		if(value.equals(AES.toString())){
			result = EncryptionAlgorithm.AES;
		}else if(value.equals(BLOWFISH.toString())){
			result = EncryptionAlgorithm.BLOWFISH;
		}
		
		return result;
	}
}
