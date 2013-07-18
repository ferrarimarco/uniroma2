package it.uniroma2.mp.passwordmanager.authentication;

import android.content.Context;
import it.uniroma2.mp.passwordmanager.configuration.ConfigurationValue;
import it.uniroma2.mp.passwordmanager.model.ConfigurationValueType;
import it.uniroma2.mp.passwordmanager.model.Password;
import it.uniroma2.mp.passwordmanager.model.PasswordType;
import it.uniroma2.mp.passwordmanager.persistance.ConfigurationDataSource;
import it.uniroma2.mp.passwordmanager.persistance.PasswordDataSource;


public class MasterPasswordManager {

	private PasswordDataSource passwordDataSource;
	private ConfigurationDataSource configurationDataSource;
	
	public static final String authenticationTableRowSeparator = "-";
	
	public MasterPasswordManager(Context context){
		passwordDataSource = new PasswordDataSource(context);
		configurationDataSource = new ConfigurationDataSource(context);
	}
	
	public boolean checkMasterPassword(String tentativePassword){
		String masterPassword = loadMasterPassword();
		
		return (tentativePassword == null || tentativePassword.isEmpty()) ? false : tentativePassword.equals(masterPassword);
	}
	
	public String loadMasterPassword(){
		passwordDataSource.open();
		String masterPassword = passwordDataSource.getPassword(PasswordType.MASTER, PasswordType.MASTER.toString(), "").getValue();
		passwordDataSource.close();
		
		return masterPassword;
	}
	
	public void storeMasterPassword(String masterPasswordValue){
		
		passwordDataSource.open();
		
		Password masterPassword = new Password(Password.DUMMY_PASSWORD_ID, masterPasswordValue, PasswordType.MASTER.toString(), "", Password.DEFAULT_ENCRYPTION_ALGORITHM);
		
		passwordDataSource.createPassword(masterPassword, PasswordType.MASTER);
		storeAuthenticationTables(masterPasswordValue);
		passwordDataSource.close();
		
		configurationDataSource.open();
		configurationDataSource.storeConfigurationValue(ConfigurationValueType.MASTER_PASSWORD_INITIALIZED, ConfigurationValue.OK.toString());
		configurationDataSource.close();
	}
	
	private void storeAuthenticationTables(String masterPassword){
		
		AuthenticationTableGenerator authenticationTableGenerator = new AuthenticationTableGenerator();
		
		for(int i = 0; i < AuthenticationTableGenerator.passwordLength; i++){
			
			String[][] authenticationTable = authenticationTableGenerator.generateTable(masterPassword.substring(i, i + 1));
			
			Password authTable = new Password(Password.DUMMY_PASSWORD_ID, serializeAuthenticationTable(authenticationTable), PasswordType.AUTH_TABLE.toString() + i, "", Password.DEFAULT_ENCRYPTION_ALGORITHM);
			
			passwordDataSource.createPassword(authTable, PasswordType.AUTH_TABLE);
		}
	}
	
	public String[][] loadAuthenticationTable(int authenticationTableIndex){
		
		passwordDataSource.open();
		String serializedAuthenticationTable = passwordDataSource.getPassword(PasswordType.AUTH_TABLE, PasswordType.AUTH_TABLE.toString() + authenticationTableIndex, "").getValue();
		passwordDataSource.close();
		
		return deserializeAuthenticationTable(serializedAuthenticationTable);
	}
	
	private String serializeAuthenticationTable(String[][] authenticationTable){
		
		StringBuilder result = new StringBuilder(authenticationTable.length * authenticationTable[0].length);
		
		for(int i = 0; i < authenticationTable.length; i++){
			for(int j = 0; j < authenticationTable[i].length; j++){
				result.append(authenticationTable[i][j]);
			}
			
			if(i != authenticationTable.length - 1){
				result.append(authenticationTableRowSeparator);
			}
		}
		
		return result.toString();
	}
	
	public String[][] deserializeAuthenticationTable(String serializedAuthenticationTable){
		
		String[] splittedString = serializedAuthenticationTable.split(authenticationTableRowSeparator);
		
		String[][] authenticationTable = new String[splittedString.length][splittedString[0].length()];
		
		for(int i = 0; i < splittedString.length; i++){
			for(int j = 0; j < splittedString[i].length(); j++){
				authenticationTable[i][j] = splittedString[i].substring(j, j + 1);
			}
		}
		
		return authenticationTable;
	}
	
	public void resetMasterPassword(){
		
		configurationDataSource.open();
		configurationDataSource.deleteConfigurationValue(ConfigurationValueType.MASTER_PASSWORD_INITIALIZED);
		configurationDataSource.close();
		
		deleteMasterPassword();
	}
	
	private void deleteMasterPassword(){
		Password masterPassword = passwordDataSource.getPassword(PasswordType.MASTER, PasswordType.MASTER.toString(), "");
		
		passwordDataSource.open();
		passwordDataSource.deletePassword(Long.toString(masterPassword.getId()), PasswordType.MASTER);
		passwordDataSource.close();
	}
	
}
