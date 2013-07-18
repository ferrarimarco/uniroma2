package it.uniroma2.mp.passwordmanager.configuration;

import android.content.Context;
import it.uniroma2.mp.passwordmanager.model.ConfigurationValueType;
import it.uniroma2.mp.passwordmanager.persistance.ConfigurationDataSource;

/***
 * Classe che si occupa della gestione dei flag di configurazione
 * **/

public class ConfigurationManager {

	private ConfigurationDataSource configurationDataSource;
	
	public ConfigurationManager(Context context){
		configurationDataSource = new ConfigurationDataSource(context);
	}
	
	/***
	 * Controllo lo stato del flag della MasterPassowrd e restituisce "true" se  è stato settato, oppure  "false" in caso contrario
	 * @return stato della Flag della MasterPassword
	 * **/
	public boolean isMasterPasswordConfigured(){
		
		configurationDataSource.open();
		
		boolean result = configurationDataSource.getConfigurationValue(ConfigurationValueType.MASTER_PASSWORD_INITIALIZED).isEmpty() ? false : true;
		
		configurationDataSource.close();
		
		return result;
	}

}
