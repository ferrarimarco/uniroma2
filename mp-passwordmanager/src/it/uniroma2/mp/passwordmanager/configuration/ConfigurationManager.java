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
	 * Controlla lo stato del flag della MasterPassowrd e restituisce "true" se  è stato settato, oppure  "false" in caso contrario
	 * @return stato della Flag della MasterPassword
	 * **/
	public boolean isMasterPasswordConfigured(){
		
		configurationDataSource.open();
		
		boolean result = configurationDataSource.getConfigurationValue(ConfigurationValueType.MASTER_PASSWORD_INITIALIZED).isEmpty() ? false : true;
		
		configurationDataSource.close();
		
		return result;
	}
	
	/***
	 * Imposta lo stato del flag del back button (se premuto o meno nel contesto della activity in focus)
	 * @param valore a cui impostare il flag
	 * @return stato della Flag del back button pressed
	 * **/
	public void setBackButtonPressed(boolean isPressed){
		configurationDataSource.open();
		
		String value = "";
		
		if(isPressed){
			value = ConfigurationValue.OK.toString();
		}else{
			value = ConfigurationValue.NOT_OK.toString();
		}
		
		configurationDataSource.updateBackButtonValue(value);
		
		configurationDataSource.close();
	}
	
	/***
	 * Controlla lo stato del flag del back button (se premuto o meno nel contesto della activity in focus)
	 * @return stato della Flag del back button pressed
	 * **/
	public boolean isBackButtonPressed(){
		configurationDataSource.open();
		
		String backButtonPressedString = configurationDataSource.getConfigurationValue(ConfigurationValueType.BACK_BUTTON_PRESSED);
		
		boolean result = false;
		
		if(backButtonPressedString.equals(ConfigurationValue.OK.toString())){
			result = true;
		}
		
		configurationDataSource.close();
		
		return result;
	}

}
