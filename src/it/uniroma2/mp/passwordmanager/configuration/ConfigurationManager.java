package it.uniroma2.mp.passwordmanager.configuration;

import android.content.Context;
import it.uniroma2.mp.passwordmanager.model.ConfigurationValueType;
import it.uniroma2.mp.passwordmanager.persistance.ConfigurationDataSource;


public class ConfigurationManager {

	private ConfigurationDataSource configurationDataSource;
	
	public ConfigurationManager(Context context){
		configurationDataSource = new ConfigurationDataSource(context);
	}
	
	public boolean isMasterPasswordConfigured(){
		
		configurationDataSource.open();
		
		boolean result = configurationDataSource.getConfigurationValue(ConfigurationValueType.MASTER_PASSWORD_INITIALIZED).isEmpty() ? false : true;
		
		configurationDataSource.close();
		
		return result;
	}

}
