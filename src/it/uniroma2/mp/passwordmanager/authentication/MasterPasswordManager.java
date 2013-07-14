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
	
	public MasterPasswordManager(Context context){
		passwordDataSource = new PasswordDataSource(context);
		configurationDataSource = new ConfigurationDataSource(context);
	}
	
	public void storeMasterPassword(String masterPassword){
		
		String masterPasswordHash = "";
		
		passwordDataSource.open();
		passwordDataSource.createPassword(PasswordType.MASTER.toString(), masterPasswordHash, PasswordType.MASTER);
		passwordDataSource.close();
		
		configurationDataSource.open();
		configurationDataSource.storeConfigurationValue(ConfigurationValueType.MASTER_PASSWORD_INITIALIZED, ConfigurationValue.OK.toString());
		configurationDataSource.close();
	}
	
}
