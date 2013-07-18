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
	
	private static final String authenticationTableRowSeparator = "-";
	
	
	public MasterPasswordManager(Context context){
		passwordDataSource = new PasswordDataSource(context);
		configurationDataSource = new ConfigurationDataSource(context);
	}
	
	/***
	 * Controlla la correttezza della password.
	 * @param tentativePassword password da verificare
	 * @return boolean "true" password corretta, "false" password errata
	 * **/
	public boolean checkMasterPassword(String tentativePassword){
		String masterPassword = loadMasterPassword();
		
		return (tentativePassword == null || tentativePassword.isEmpty()) ? false : tentativePassword.equals(masterPassword);
	}
	
	
	/***
	 * Restituisce la MasterPassoword.
	 * @return masterPassword.
	 ***/
	public String loadMasterPassword(){
		passwordDataSource.open();
		String masterPassword = passwordDataSource.getPassword(PasswordType.MASTER, PasswordType.MASTER.toString(), "").getValue();
		passwordDataSource.close();
		
		return masterPassword;
	}
	
	
	/***
	 * Memorizza la MasterPassword e un flag di inizializzazione relativo alla MaseterPassoword sul Database.
	 * @param masterPasswordValue password da memorizzare
	 * @return void
	 * **/
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
	
	/***
	 * Genera e Memorizza una tabella di autenticazione sul database
	 * @param masterPassword utilizzata come chiave per criptare
	 * @return void
	 * **/
	private void storeAuthenticationTables(String masterPassword){
		
		AuthenticationTableGenerator authenticationTableGenerator = new AuthenticationTableGenerator();
		
		for(int i = 0; i < AuthenticationTableGenerator.passwordLength; i++){
			
			String[][] authenticationTable = authenticationTableGenerator.generateTable(masterPassword.substring(i, i + 1));
			
			Password authTable = new Password(Password.DUMMY_PASSWORD_ID, serializeAuthenticationTable(authenticationTable), PasswordType.AUTH_TABLE.toString() + i, "", Password.DEFAULT_ENCRYPTION_ALGORITHM);
			
			passwordDataSource.createPassword(authTable, PasswordType.AUTH_TABLE);
		}
	}
	
	/***
	 * Restituisce la authenticationTableIndex tabella di autenticazione, prendendola dal database
	 * @param authenticationTableIndex indica il numero della tabella di autenticazione da recuparare
	 * @return La tabella di autenticazione specificata
	 * **/
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
