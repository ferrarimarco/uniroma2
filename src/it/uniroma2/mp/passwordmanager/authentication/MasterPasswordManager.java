package it.uniroma2.mp.passwordmanager.authentication;

import android.content.Context;
import it.uniroma2.mp.passwordmanager.configuration.ConfigurationValue;
import it.uniroma2.mp.passwordmanager.model.ConfigurationValueType;
import it.uniroma2.mp.passwordmanager.model.PasswordType;
import it.uniroma2.mp.passwordmanager.persistance.ConfigurationDataSource;
import it.uniroma2.mp.passwordmanager.persistance.PasswordDataSource;


public class MasterPasswordManager {

	private PasswordDataSource passwordDataSource;
	private ConfigurationDataSource configurationDataSource;
	
	private static final String authenticationTableRowSeparator = "-";
	
	public MasterPasswordManager(Context context){
		passwordDataSource = new PasswordDataSource(context);
		configurationDataSource = new ConfigurationDataSource(context);
	}
	
	public boolean checkMasterPassword(String tentativePassword){
		String masterPassword = loadMasterPassword();
		
		return (tentativePassword == null || tentativePassword.isEmpty()) ? false : tentativePassword.equals(masterPassword);
	}
	
	private String loadMasterPassword(){
		passwordDataSource.open();
		String masterPassword = passwordDataSource.getPassword(PasswordType.MASTER, PasswordType.MASTER.toString(), "").getValue();
		passwordDataSource.close();
		
		return masterPassword;
	}
	
	public void storeMasterPassword(String masterPassword){
		
		passwordDataSource.open();
		passwordDataSource.createPassword(PasswordType.MASTER.toString(), masterPassword, "", PasswordType.MASTER);
		storeAuthenticationTables(masterPassword);
		passwordDataSource.close();
		
		configurationDataSource.open();
		configurationDataSource.storeConfigurationValue(ConfigurationValueType.MASTER_PASSWORD_INITIALIZED, ConfigurationValue.OK.toString());
		configurationDataSource.close();
	}
	
	private void storeAuthenticationTables(String masterPassword){
		
		AuthenticationTableGenerator authenticationTableGenerator = new AuthenticationTableGenerator();
		
		for(int i = 0; i < AuthenticationTableGenerator.passwordLength; i++){
			
			String[][] authenticationTable = authenticationTableGenerator.generateTable(masterPassword.substring(i, i + 1));
			
			passwordDataSource.createPassword(PasswordType.AUTH_TABLE.toString() + i, serializeAuthenticationTable(authenticationTable), "", PasswordType.AUTH_TABLE);
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
	
	private String[][] deserializeAuthenticationTable(String serializedAuthenticationTable){
		
		String[] splittedString = serializedAuthenticationTable.split(authenticationTableRowSeparator);
		
		String[][] authenticationTable = new String[splittedString.length][splittedString[0].length()];
		
		for(int i = 0; i < splittedString.length; i++){
			for(int j = 0; j < splittedString[i].length(); j++){
				authenticationTable[i][j] = splittedString[i].substring(j, j + 1);
			}
		}
		
		return authenticationTable;
	}
	
}
