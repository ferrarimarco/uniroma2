package it.uniroma2.mp.passwordmanager.persistance;

import it.uniroma2.mp.passwordmanager.model.ConfigurationValueType;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/***
 * Classe per gestire la persistenza dei valori di configurazione.
 * **/

public class ConfigurationDataSource {
	// Database fields
	private SQLiteDatabase database;
	private SQLiteHelper dbHelper;

	private String[] allColumns = {
			SQLiteHelper.COLUMN_ID,
			SQLiteHelper.COLUMN_DESCRIPTION,
			SQLiteHelper.COLUMN_VALUE };

	public ConfigurationDataSource(Context context){
		dbHelper = new SQLiteHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}
	
	/***
	 * Restituisce il valore del Flag
	 * **/
	public String getConfigurationValue(ConfigurationValueType configurationValueType){
		
		Cursor cursor = database.query(SQLiteHelper.TABLE_CONFIG, allColumns, SQLiteHelper.COLUMN_DESCRIPTION + " = '" + configurationValueType.toString() + "'", null, null, null, null);
		
		String result = "";
		
		if(cursor.moveToFirst()){
			result = cursor.getString(cursor.getColumnIndex(SQLiteHelper.COLUMN_VALUE));
		}
		
		return result;
	}
	
	/***
	 * Salva il valore del flag
	 * **/
	public void storeConfigurationValue(ConfigurationValueType configurationValueType, String value){
		ContentValues values = new ContentValues();
		
		values.put(SQLiteHelper.COLUMN_DESCRIPTION, configurationValueType.toString());
		values.put(SQLiteHelper.COLUMN_VALUE, value);
		
		String table = SQLiteHelper.TABLE_CONFIG;
		
		database.insert(table, null, values);
	}
	
	/***
	 * Cancella il valore del flag
	 * **/
	public void deleteConfigurationValue(ConfigurationValueType configurationValueType){
		database.delete(SQLiteHelper.TABLE_CONFIG, SQLiteHelper.COLUMN_DESCRIPTION + " = '" + configurationValueType.toString() + "'", null);
	}
}
