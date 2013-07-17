package it.uniroma2.mp.passwordmanager.model;

import it.uniroma2.mp.passwordmanager.encryption.EncryptionAlgorithm;


public class Password {
	
	private long id;
	private String value;
	private String description;
	private String parent;
	private EncryptionAlgorithm encryptionAlgorithm;
	
	public static final long DUMMY_PASSWORD_ID = -1;
	public static final EncryptionAlgorithm DEFAULT_ENCRYPTION_ALGORITHM = EncryptionAlgorithm.AES;
	public static final String DUMMY_PARENT_VALUE = "-1";
	
	public Password(){
		
	}
	
	public Password(long id, String value, String description, String parent, EncryptionAlgorithm encryptionAlgorithm){
		this.id = id;
		this.value = value;
		this.description = description;
		this.setParent(parent);
		this.setEncryptionAlgorithm(encryptionAlgorithm);
	}
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	public EncryptionAlgorithm getEncryptionAlgorithm() {
		return encryptionAlgorithm;
	}

	public void setEncryptionAlgorithm(EncryptionAlgorithm encryptionAlgorithm) {
		this.encryptionAlgorithm = encryptionAlgorithm;
	}

	@Override
	public String toString(){
		return value;
	}
}
