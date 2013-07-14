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
		
		if(passwordType.equals(PasswordType.AUTHENTICATION)){
			table = SQLiteHelper.TABLE_AUTH;
		}else if(passwordType.equals(PasswordType.STORED)){
			table = SQLiteHelper.TABLE_PASSWORDS;
		}
		
		return table;
	}

	public Password createPassword(String description, String value, PasswordType passwordType) {
		
		ContentValues values = new ContentValues();
		
		values.put(SQLiteHelper.COLUMN_DESCRIPTION, description);
		values.put(SQLiteHelper.COLUMN_VALUE, value);
		
		String table = getDBTable(passwordType);
		
		long insertId = database.insert(table, null, values);
		
		Cursor cursor = database.query(table, allColumns, SQLiteHelper.COLUMN_ID + " = " + insertId, null, null, null, null);
		cursor.moveToFirst();
		
		Password newPassword = cursorToPassword(cursor);
		cursor.close();
		
		return newPassword;
	}

	public void deletePassword(Password password, PasswordType passwordType) {
		
		long id = password.getId();
		
		String table = getDBTable(passwordType);
		
		database.delete(table, SQLiteHelper.COLUMN_ID + " = " + id, null);
	}

	public List<Password> getAllPasswords(PasswordType passwordType) {
		
		List<Password> comments = new ArrayList<Password>();
		
		String table = getDBTable(passwordType);

		Cursor cursor = database.query(table, allColumns, null, null, null, null, null);

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
		password.setId(cursor.getLong(0));
		password.setValue(cursor.getString(1));
		password.setDescription(cursor.getString(2));
		
		return password;
	}
}
