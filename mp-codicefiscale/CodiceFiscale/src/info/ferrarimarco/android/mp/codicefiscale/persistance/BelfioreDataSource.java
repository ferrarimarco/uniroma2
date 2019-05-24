package info.ferrarimarco.android.mp.codicefiscale.persistance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;


public class BelfioreDataSource {

	private SQLiteDatabase database;
	private BelfioreSQLiteOpenHelper dbHelper;
	
	private String[] allColumns = { BelfioreSQLiteOpenHelper.COLUMN_ID,
			BelfioreSQLiteOpenHelper.COLUMN_ID_NAZIONALE,
			BelfioreSQLiteOpenHelper.COLUMN_PROVINCIA,
			BelfioreSQLiteOpenHelper.COLUMN_COMUNE};

	public BelfioreDataSource(Context context) {
		dbHelper = new BelfioreSQLiteOpenHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getReadableDatabase();
	}

	public void close() {
		dbHelper.close();
	}
	
	public List<BelfioreEntry> getAllBelfioreEntries() {
		List<BelfioreEntry> entries = new ArrayList<BelfioreEntry>();

		Cursor cursor = database.query(BelfioreSQLiteOpenHelper.TABLE, allColumns, null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			BelfioreEntry entry = cursorToBelfioreEntry(cursor);
			entries.add(entry);
			cursor.moveToNext();
		}
		
		// Make sure to close the cursor
		cursor.close();
		
		return entries;
	}

	private BelfioreEntry cursorToBelfioreEntry(Cursor cursor) {
		
		BelfioreEntry entry = new BelfioreEntry();
		
		entry.setId(cursor.getInt(0));
		entry.setIdNazionale(cursor.getString(1));
		entry.setProvincia(cursor.getString(2));
		entry.setComune(cursor.getString(3));
		
		return entry;
	}
	
	public String getIdNazionale(String comune){

		Cursor cursor = database.query(BelfioreSQLiteOpenHelper.TABLE, allColumns, BelfioreSQLiteOpenHelper.COLUMN_COMUNE + "=?",
				new String[] { comune }, null, null, null, null);
		
		String result = "";
		
		// TODO: check if multiple results (filter with provincia)
		
		if (cursor != null && cursor.getCount() > 0){
			cursor.moveToFirst();
			
			result = cursor.getString(cursor.getColumnIndex(BelfioreSQLiteOpenHelper.COLUMN_ID_NAZIONALE));
		}
		
		if(result == null || result.isEmpty() || cursor == null){
			result = "XXXX";
		}
		
		return result;
	}

	public List<String> getProvincie(){
		
		List<String> entries = new ArrayList<String>();

		Cursor cursor = database.query(true, BelfioreSQLiteOpenHelper.TABLE, new String[]{BelfioreSQLiteOpenHelper.COLUMN_PROVINCIA}, null, null, null, null, null, null);

		cursor.moveToFirst();
		
		while (!cursor.isAfterLast()) {
			
			if(cursor.getString(0) == null){
				String[] columns = cursor.getColumnNames();
				System.out.println();
			}
			
			if(cursor.getString(0) != null){
				entries.add(cursor.getString(0));
			}
			
			
			cursor.moveToNext();
		}
		
		// Make sure to close the cursor
		cursor.close();
		
		Collections.sort(entries);
		
		return entries;
	}
	
	public List<String> getComuni(String provincia){
		
		List<String> entries = new ArrayList<String>();

		Cursor cursor = database.query(BelfioreSQLiteOpenHelper.TABLE, new String[]{BelfioreSQLiteOpenHelper.COLUMN_COMUNE}, BelfioreSQLiteOpenHelper.COLUMN_PROVINCIA + "=?", new String[] { provincia }, null, null, null, null);
		cursor.moveToFirst();
		
		while (!cursor.isAfterLast()) {
			
			if(cursor.getString(0) == null){
				String[] columns = cursor.getColumnNames();
				System.out.println();
			}
			
			if(cursor.getString(0) != null){
				entries.add(cursor.getString(0));
			}			
			
			cursor.moveToNext();
		}
		
		// Make sure to close the cursor
		cursor.close();
		
		Collections.sort(entries);
		
		return entries;
	}
	
}
