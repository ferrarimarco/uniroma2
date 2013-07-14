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

	public static final String TABLE_CONFIG = "configuration";
	
	public static final String TABLE_CATEGORIES = "categories";
	public static final String COLUMN_PARENT = "parent";

	private static final String DATABASE_NAME = "passwords.db";
	private static final int DATABASE_VERSION = 1;

	public SQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		
		String createTable = "create table "	+ TABLE_PASSWORDS + "("
				+ COLUMN_ID + " integer primary key autoincrement, "
				+ COLUMN_VALUE + " text not null,"
				+ COLUMN_DESCRIPTION + " text not null);";
		
		database.execSQL(createTable);
		
		createTable = "create table " + TABLE_AUTH + "("
				+ COLUMN_ID + " integer primary key autoincrement, "
				+ COLUMN_DESCRIPTION + " text not null,"
				+ COLUMN_VALUE + " text not null);";
		
		database.execSQL(createTable);
		
		createTable = "create table " + TABLE_CONFIG + "("
				+ COLUMN_ID + " integer primary key autoincrement, "
				+ COLUMN_DESCRIPTION + " text not null,"
				+ COLUMN_VALUE + " text not null);";
		
		database.execSQL(createTable);
		
		createTable = "create table " + TABLE_CATEGORIES + "("
				+ COLUMN_ID + " integer primary key autoincrement, "
				+ COLUMN_DESCRIPTION + " text not null,"
				+ COLUMN_VALUE + " text not null,"
				+ COLUMN_PARENT + " text not null);";
		
		database.execSQL(createTable);
		
		String categoriesInitialization = "insert into " + TABLE_CATEGORIES + " values(null, '" + "EMAIL" +"', '" + "EMAIL" + "','"+ "-1" +"')";
		database.execSQL(categoriesInitialization);
		
		categoriesInitialization = "insert into " + TABLE_CATEGORIES + " values(null, '" + "DEVICES" +"', '" + "DEVICES" + "','"+ "-1" +"')";
		database.execSQL(categoriesInitialization);
		
		categoriesInitialization = "insert into " + TABLE_CATEGORIES + " values(null, '" + "BANKING" +"', '" + "BANKING" + "','"+ "-1" +"')";
		database.execSQL(categoriesInitialization);
		
		categoriesInitialization = "insert into " + TABLE_CATEGORIES + " values(null, '" + "WEB" +"', '" + "WEB" + "','"+ "-1" +"')";
		database.execSQL(categoriesInitialization);
		
		categoriesInitialization = "insert into " + TABLE_CATEGORIES + " values(null, '" + "OTHER" +"', '" + "OTHER" + "','"+ "-1" +"')";
		database.execSQL(categoriesInitialization);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}
}
