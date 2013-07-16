package it.uniroma2.mp.passwordmanager.persistance;

import it.uniroma2.mp.passwordmanager.R;
import it.uniroma2.mp.passwordmanager.model.GridItem;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class SQLiteHelper extends SQLiteOpenHelper {

	public static final String TABLE_PASSWORDS = "passwords";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_DESCRIPTION = "description";
	public static final String COLUMN_VALUE = "value";
	public static final String COLUMN_CATEGORY = "category";

	public static final String TABLE_AUTH = "authentication";

	public static final String TABLE_CONFIG = "configuration";
	
	public static final String TABLE_CATEGORIES = "categories";
	public static final String COLUMN_PARENT = "parent";
	public static final String COLUMN_DRAWABLE_ID = "drawable_id";

	private static final String DATABASE_NAME = "passwords.db";
	private static final int DATABASE_VERSION = 1;

	public SQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		
		String createTable = "create table "	+ TABLE_PASSWORDS + "("
				+ COLUMN_ID + " integer primary key autoincrement, "
				+ COLUMN_VALUE + " text not null, "
				+ COLUMN_DESCRIPTION + " text not null, "
				+ COLUMN_CATEGORY + " text not null);";
		
		database.execSQL(createTable);
		
		createTable = "create table " + TABLE_AUTH + "("
				+ COLUMN_ID + " integer primary key autoincrement, "
				+ COLUMN_DESCRIPTION + " text not null,"
				+ COLUMN_VALUE + " text not null);";
		
		database.execSQL(createTable);
		
		createTable = "create table " + TABLE_CONFIG + "("
				+ COLUMN_ID + " integer primary key autoincrement, "
				+ COLUMN_DESCRIPTION + " text not null, "
				+ COLUMN_VALUE + " text not null);";
		
		database.execSQL(createTable);
		
		createTable = "create table " + TABLE_CATEGORIES + "("
				+ COLUMN_ID + " integer primary key autoincrement, "
				+ COLUMN_VALUE + " text not null,"
				+ COLUMN_PARENT + " text not null,"
				+ COLUMN_DRAWABLE_ID + " text not null);";
		
		database.execSQL(createTable);
		
		String categoriesInitialization = "insert into " + TABLE_CATEGORIES + " values(null, '" + GridItem.EMAIL_CATEGORY_VALUE + "','"+ "-1" +"','"+ R.drawable.email +"')";
		database.execSQL(categoriesInitialization);
		
		categoriesInitialization = "insert into " + TABLE_CATEGORIES + " values(null, '" + GridItem.DEVICES_CATEGORY_VALUE + "','"+ "-1" +"','"+ R.drawable.devices +"')";
		database.execSQL(categoriesInitialization);
		
		categoriesInitialization = "insert into " + TABLE_CATEGORIES + " values(null, '"+ GridItem.BANK_CATEGORY_VALUE + "','"+ "-1" +"','"+ R.drawable.bank +"')";
		database.execSQL(categoriesInitialization);
		
		categoriesInitialization = "insert into " + TABLE_CATEGORIES + " values(null, '" + GridItem.WEB_CATEGORY_VALUE + "','"+ "-1" +"','"+ R.drawable.web +"')";
		database.execSQL(categoriesInitialization);
		
		categoriesInitialization = "insert into " + TABLE_CATEGORIES + " values(null, '" + GridItem.OTHER_CATEGORY_VALUE + "','"+ "-1" +"','"+ R.drawable.other +"')";
		database.execSQL(categoriesInitialization);
		
		categoriesInitialization = "insert into " + TABLE_CATEGORIES + " values(null, '" + GridItem.EMPTY_CATEGORY_VALUE + "','"+ "-1" +"','"+ R.drawable.empty +"')";
		database.execSQL(categoriesInitialization);
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}
}
