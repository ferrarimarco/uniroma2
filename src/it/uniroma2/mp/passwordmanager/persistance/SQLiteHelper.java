package it.uniroma2.mp.passwordmanager.persistance;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class SQLiteHelper extends SQLiteOpenHelper {

	public static final String TABLE_PASSWORDS = "passwords";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_DESCRIPTION = "description";
	public static final String COLUMN_VALUE = "value";
	
	public static final String TABLE_AUTH = "authentication";		

	private static final String DATABASE_NAME = "passwords.db";
	private static final int DATABASE_VERSION = 1;

	// Database creation sql statement
	private static final String DATABASE_CREATE = "create table "
			+ TABLE_PASSWORDS + "("
			+ COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_VALUE + " text not null,"
			+ COLUMN_DESCRIPTION + " text not null);"
			+ "create table "
			+ TABLE_AUTH + "("
			+ COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_VALUE + " text not null,"
			+ COLUMN_DESCRIPTION + " text not null);";

	public SQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}
}
