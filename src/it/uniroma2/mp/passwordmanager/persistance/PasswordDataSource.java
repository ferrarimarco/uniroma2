package it.uniroma2.mp.passwordmanager.persistance;

import java.util.ArrayList;
import java.util.List;

import it.uniroma2.mp.passwordmanager.model.Password;
import it.uniroma2.mp.passwordmanager.model.PasswordType;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;


public class PasswordDataSource {

	// Database fields
	private SQLiteDatabase database;
	private SQLiteHelper dbHelper;

	private String[] allColumns = {
			SQLiteHelper.COLUMN_ID,
			SQLiteHelper.COLUMN_DESCRIPTION,
			SQLiteHelper.COLUMN_VALUE,
			SQLiteHelper.COLUMN_CATEGORY };
	
	private String[] allColumnsAuth = {
			SQLiteHelper.COLUMN_ID,
			SQLiteHelper.COLUMN_DESCRIPTION,
			SQLiteHelper.COLUMN_VALUE };

	public PasswordDataSource(Context context){
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
	
	public void createPassword(String description, String value, String parent, PasswordType passwordType) {
		
		ContentValues values = new ContentValues();
		
		values.put(SQLiteHelper.COLUMN_DESCRIPTION, description);
		values.put(SQLiteHelper.COLUMN_VALUE, value);
		
		if(!passwordType.equals(PasswordType.AUTH_TABLE) && !passwordType.equals(PasswordType.MASTER)){
			values.put(SQLiteHelper.COLUMN_CATEGORY, parent);
		}
		
		String table = getDBTable(passwordType);
		
		database.insert(table, null, values);
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
			result = cursorToPassword(cursor);
		}
		
		cursor.close();
		
		return result;
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
			Password comment = cursorToPassword(cursor);
			comments.add(comment);
			cursor.moveToNext();
		}
		
		// Make sure to close the cursor
		cursor.close();
		return comments;
	}

	private Password cursorToPassword(Cursor cursor) {
		Password password = new Password();
		password.setId(cursor.getLong(cursor.getColumnIndex(SQLiteHelper.COLUMN_ID)));
		password.setValue(cursor.getString(cursor.getColumnIndex(SQLiteHelper.COLUMN_VALUE)));
		password.setDescription(cursor.getString(cursor.getColumnIndex(SQLiteHelper.COLUMN_DESCRIPTION)));
		
		return password;
	}
}
