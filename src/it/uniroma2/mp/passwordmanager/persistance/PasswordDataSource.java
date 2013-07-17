package it.uniroma2.mp.passwordmanager.persistance;

import java.util.ArrayList;
import java.util.List;

import it.uniroma2.mp.passwordmanager.encryption.EncryptionAlgorithm;
import it.uniroma2.mp.passwordmanager.encryption.EncryptionHelper;
import it.uniroma2.mp.passwordmanager.model.Password;
import it.uniroma2.mp.passwordmanager.model.PasswordType;
import it.uniroma2.mp.passwordmanager.authentication.MasterPasswordManager;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;


public class PasswordDataSource {

	// Database fields
	private SQLiteDatabase database;
	private SQLiteHelper dbHelper;
	
	private Context context;

	private String[] allColumns = {
			SQLiteHelper.COLUMN_ID,
			SQLiteHelper.COLUMN_DESCRIPTION,
			SQLiteHelper.COLUMN_VALUE,
			SQLiteHelper.COLUMN_CATEGORY,
			SQLiteHelper.COLUMN_ALGORITHM };
	
	private String[] allColumnsAuth = {
			SQLiteHelper.COLUMN_ID,
			SQLiteHelper.COLUMN_DESCRIPTION,
			SQLiteHelper.COLUMN_VALUE };

	public PasswordDataSource(Context context){
		
		this.context = context;
		
		dbHelper = new SQLiteHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}
	
	private String getDBTable(PasswordType passwordType){
		
		String table = "";
		
		if(passwordType.equals(PasswordType.MASTER) || passwordType.equals(PasswordType.AUTH_TABLE)){
			table = SQLiteHelper.TABLE_AUTH;
		}else if(passwordType.equals(PasswordType.STORED)){
			table = SQLiteHelper.TABLE_PASSWORDS;
		}
		
		return table;
	}
	
	private String getDecryptedPassword(String encryptedPassword, EncryptionAlgorithm encryptionAlgorithm){
		EncryptionHelper encryptionHelper = new EncryptionHelper();
		
		MasterPasswordManager masterPasswordManager = new MasterPasswordManager(context);
		
		String masterPassword = masterPasswordManager.loadMasterPassword();
		
		String result = "";
		
		try {
			result = encryptionHelper.decrypt(masterPassword, encryptedPassword, encryptionAlgorithm);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	}
	
	private String getEncryptedPassword(String decryptedPassword, EncryptionAlgorithm encryptionAlgorithm){
		EncryptionHelper encryptionHelper = new EncryptionHelper();
		
		MasterPasswordManager masterPasswordManager = new MasterPasswordManager(context);
		
		String masterPassword = masterPasswordManager.loadMasterPassword();
		
		String result = "";
		
		try {
			result = encryptionHelper.encrypt(masterPassword, decryptedPassword, encryptionAlgorithm);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return result;
	}
	
	public void createPassword(Password password, PasswordType passwordType) {
		
		ContentValues values = new ContentValues();
		
		values.put(SQLiteHelper.COLUMN_DESCRIPTION, password.getDescription());
		
		String value = "";
		
		if(!passwordType.equals(PasswordType.AUTH_TABLE) && !passwordType.equals(PasswordType.MASTER)){
			values.put(SQLiteHelper.COLUMN_CATEGORY, password.getParent());
			values.put(SQLiteHelper.COLUMN_ALGORITHM, password.getEncryptionAlgorithm().toString());
			
			value = getEncryptedPassword(password.getValue(), password.getEncryptionAlgorithm());
		}else{
			value = password.getValue();
		}
		
		values.put(SQLiteHelper.COLUMN_VALUE, value);
		
		String table = getDBTable(passwordType);
		
		database.insert(table, null, values);
	}
	
	public void updatePassword(Password password, PasswordType passwordType) {
		
		String where = SQLiteHelper.COLUMN_ID + "=?";
		String[] whereArgs = new String[] {Long.toString(password.getId())};

		ContentValues dataToInsert = new ContentValues();                          
		dataToInsert.put(SQLiteHelper.COLUMN_DESCRIPTION, password.getDescription());
		
		String value = "";
		
		if(!passwordType.equals(PasswordType.AUTH_TABLE) && !passwordType.equals(PasswordType.MASTER)){
			value = getEncryptedPassword(password.getValue(), password.getEncryptionAlgorithm());
		}else{
			value = password.getValue();
		}
		
		dataToInsert.put(SQLiteHelper.COLUMN_VALUE, value);
		dataToInsert.put(SQLiteHelper.COLUMN_CATEGORY, password.getParent());
		dataToInsert.put(SQLiteHelper.COLUMN_ALGORITHM, password.getEncryptionAlgorithm().toString());
		
		database.update(getDBTable(passwordType), dataToInsert, where, whereArgs);
	}

	public void deletePassword(Password password, PasswordType passwordType) {
		
		long id = password.getId();
		
		String table = getDBTable(passwordType);
		
		database.delete(table, SQLiteHelper.COLUMN_ID + " = " + id, null);
	}

	private String[] getAllColumns(PasswordType passwordType){
		
		String[] allCols = null;
		
		if(!passwordType.equals(PasswordType.AUTH_TABLE) && !passwordType.equals(PasswordType.MASTER)){
			allCols = this.allColumns;
		}else{
			allCols = this.allColumnsAuth;
		}
		
		return allCols;
	}
	
	public Password getPassword(PasswordType passwordType, String description, String parent){
		
		String table = getDBTable(passwordType);
		
		String condition = "";
		
		String[] allColumns = getAllColumns(passwordType);
		
		if(parent != null && !parent.isEmpty()){
			condition = SQLiteHelper.COLUMN_DESCRIPTION + " = '" + description + "' AND " + SQLiteHelper.COLUMN_CATEGORY + " = '" + parent + "'";
		}else{
			condition = SQLiteHelper.COLUMN_DESCRIPTION + " = '" + description + "'";
		}
		
		Cursor cursor = database.query(table, allColumns, condition, null, null, null, null);
		
		Password result = null;
		
		if(cursor.moveToFirst()){
			result = cursorToPassword(cursor, passwordType);
		}
		
		cursor.close();
		
		return result;
	}
	
	public Password getPassword(long id){
		Cursor cursor = database.query(SQLiteHelper.TABLE_PASSWORDS, allColumns, SQLiteHelper.COLUMN_ID + " = '" + id + "'", null, null, null, null);

		Password gridItem = null;
		
		if(cursor.moveToFirst()){
			gridItem = cursorToPassword(cursor, PasswordType.STORED);
		}

		cursor.close();

		return gridItem;
	}
	
	public List<Password> getAllPasswords(PasswordType passwordType, String parent) {
		
		List<Password> comments = new ArrayList<Password>();
		
		String table = getDBTable(passwordType);

		String[] allColumns = getAllColumns(passwordType);
		
		String selection = null;
		
		if(parent != null && !parent.isEmpty()){
			selection = SQLiteHelper.COLUMN_CATEGORY + " = '" + parent + "'";
		}
		
		Cursor cursor = database.query(table, allColumns, selection, null, null, null, null);

		cursor.moveToFirst();
		
		while (!cursor.isAfterLast()) {
			Password comment = cursorToPassword(cursor, passwordType);
			comments.add(comment);
			cursor.moveToNext();
		}
		
		// Make sure to close the cursor
		cursor.close();
		return comments;
	}

	private Password cursorToPassword(Cursor cursor, PasswordType passwordType) {
		Password password = new Password();
		password.setId(cursor.getLong(cursor.getColumnIndex(SQLiteHelper.COLUMN_ID)));
		
		password.setDescription(cursor.getString(cursor.getColumnIndex(SQLiteHelper.COLUMN_DESCRIPTION)));
		
		if(cursor.getColumnIndex(SQLiteHelper.COLUMN_CATEGORY) != -1){
			password.setParent(cursor.getString(cursor.getColumnIndex(SQLiteHelper.COLUMN_CATEGORY)));
		}else{
			password.setParent(Password.DUMMY_PARENT_VALUE);
		}
		
		if(cursor.getColumnIndex(SQLiteHelper.COLUMN_ALGORITHM) != -1){
			EncryptionAlgorithm encryptionAlgorithm = EncryptionAlgorithm.getEncryptionAlgorithmFromValue(cursor.getString(cursor.getColumnIndex(SQLiteHelper.COLUMN_ALGORITHM)));
			password.setEncryptionAlgorithm(encryptionAlgorithm);
		}else{
			password.setEncryptionAlgorithm(Password.DEFAULT_ENCRYPTION_ALGORITHM);
		}
		
		String value = "";
		
		if(!passwordType.equals(PasswordType.AUTH_TABLE) && !passwordType.equals(PasswordType.MASTER)){
			value = getDecryptedPassword(cursor.getString(cursor.getColumnIndex(SQLiteHelper.COLUMN_VALUE)), password.getEncryptionAlgorithm());
		}else{
			value = cursor.getString(cursor.getColumnIndex(SQLiteHelper.COLUMN_VALUE));
		}
		
		password.setValue(value);
		
		return password;
	}
}
