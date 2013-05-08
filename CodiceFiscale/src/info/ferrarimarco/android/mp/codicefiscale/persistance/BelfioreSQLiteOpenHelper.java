package info.ferrarimarco.android.mp.codicefiscale.persistance;

import info.ferrarimarco.android.mp.codicefiscale.MainActivity;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;


public class BelfioreSQLiteOpenHelper extends SQLiteOpenHelper {
	
	private static String DB_PATH = "/data/data/info.ferrarimarco.android.mp.codicefiscale/databases/";
	private static final String DB_NAME = "belfiore.db";
	private static final String DB_NAME_IMPORT = "belfiore.db";
	private static final int DB_VERSION = 1;
	
	private SQLiteDatabase myDataBase;
	
	public static final String TABLE = "comuni";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_ID_NAZIONALE = "ID_NAZIONALE";
	public static final String COLUMN_PROVINCIA = "PROVINCIA";
	public static final String COLUMN_COMUNE = "COMUNE";
	
	public BelfioreSQLiteOpenHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		
		try {
			importDatabase();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		/*
		boolean dbExist = checkDataBase();

		if(dbExist){
			//do ng - database already exist
		}else{
			//By calling this method and empty database will be created into the default system path
			//of your application so we are gonna be able to overwrite that database with our database.
			this.getWritableDatabase();
		}
		
		try {
			importDatabase();
		} catch (IOException e) {
			throw new Error("Error copying database");
		}*/
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		onCreate(db);
	}
	
	 /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     * @return true if it exists, false if it doesn't
     */
	private boolean checkDataBase(){

		SQLiteDatabase checkDB = null;

		try{
			String myPath = DB_PATH + DB_NAME;
			checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

		}catch(SQLiteException e){ 
			//database does't exist yet.
		}

		if(checkDB != null){ 
			checkDB.close(); 
		}

		// TODO: check if a table named comuni exists
		return checkDB != null ? true : false;
	}
	
	private void importDatabase() throws IOException{
		
		//Open your local db as the input stream
    	InputStream myInput = MainActivity.getContext().getAssets().open(DB_NAME_IMPORT);
 
    	// Path to the just created empty db
    	String outFileName = DB_PATH + DB_NAME;
 
    	//Open the empty db as the output stream
    	OutputStream myOutput = new FileOutputStream(outFileName);
 
    	//transfer bytes from the inputfile to the outputfile
    	byte[] buffer = new byte[1024];
    	int length;
    	while ((length = myInput.read(buffer)) != -1){
    		myOutput.write(buffer, 0, length);
    	}
 
    	//Close the streams
    	myOutput.flush();
    	myOutput.close();
    	myInput.close();
	}
	
	public void openDataBase() throws SQLException{

		//Open the database
		String myPath = DB_PATH + DB_NAME;
		myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
	}

	@Override
	public synchronized void close() {

		if(myDataBase != null)
			myDataBase.close();

		super.close();

	}
}
