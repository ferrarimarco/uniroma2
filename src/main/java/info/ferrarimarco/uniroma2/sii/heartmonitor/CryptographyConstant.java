package info.ferrarimarco.uniroma2.sii.heartmonitor;


public enum CryptographyConstant {
	
	CRYPTO_PROVIDER("BC"),
	CRYPTO_ALGORITHM("AES"),
	CRYPTO_ALGORITHM_STRENGTH("256"),
	CIPHER_INSTANCE_TRANSFORMATION("AES/CBC/ZeroBytePadding");
	
	private String value;
	
	private CryptographyConstant(String value){
		this.value = value;
	}
	
	public String getStringValue(){
		return value;
	}
}
