package info.ferrarimarco.uniroma2.sii.heartmonitor;


public enum CryptographyConstant {
	
	CRYPTO_PROVIDER("BC"),
	CRYPTO_ALGORITHM("AES"),
	CIPHER_INSTANCE_TRANSFORMATION("AES/CBC/ZeroBytePadding"),
	SECURE_HASH_ALGORITHM("SHA-256");
	
	private String value;
	
	private CryptographyConstant(String value){
		this.value = value;
	}
	
	public String getStringValue(){
		return value;
	}
}
