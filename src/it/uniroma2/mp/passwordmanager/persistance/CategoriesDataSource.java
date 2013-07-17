package it.uniroma2.mp.passwordmanager.persistance;

import java.util.ArrayList;
import java.util.List;

import it.uniroma2.mp.passwordmanager.model.GridItem;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;


public class CategoriesDataSource {

	// Database fields
	private SQLiteDatabase database;
	private SQLiteHelper dbHelper;
	private Context context;

	private String[] allColumns = {
			SQLiteHelper.COLUMN_ID,
			SQLiteHelper.COLUMN_VALUE,
			SQLiteHelper.COLUMN_PARENT,
			SQLiteHelper.COLUMN_DRAWABLE_ID };

	public CategoriesDataSource(Context context){
		dbHelper = new SQLiteHelper(context);
		this.context = context;
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	private GridItem cursorToCategory(Cursor cursor){
		
		String nameId = cursor.getString(cursor.getColumnIndex(SQLiteHelper.COLUMN_VALUE));
		String name = GridItem.getDescription(nameId, context);
		
		// Custom GridItem has no builtin description
		if(name.isEmpty()){
			name = nameId;
		}
		
		String id = cursor.getString(cursor.getColumnIndex(SQLiteHelper.COLUMN_ID));
		String drawableId = cursor.getString(cursor.getColumnIndex(SQLiteHelper.COLUMN_DRAWABLE_ID));
		String parent = cursor.getString(cursor.getColumnIndex(SQLiteHelper.COLUMN_PARENT));

		return new GridItem(name, Integer.parseInt(drawableId), Integer.parseInt(parent), id);
	}
	
	public List<GridItem> getAllCategories(String parent){

		Cursor cursor = database.query(SQLiteHelper.TABLE_CATEGORIES, allColumns, SQLiteHelper.COLUMN_PARENT + " = '" + parent + "' AND " + SQLiteHelper.COLUMN_VALUE + " != '" + GridItem.EMPTY_CATEGORY_VALUE + "'", null, null, null, null);

		List<GridItem> gridItems = new ArrayList<GridItem>();

		if(cursor.moveToFirst()){
			while (!cursor.isAfterLast()) {
				gridItems.add(cursorToCategory(cursor));
				cursor.moveToNext();
			}
		}

		cursor.close();

		return gridItems;
	}
	
	public GridItem getGridItem(String id){
		Cursor cursor = database.query(SQLiteHelper.TABLE_CATEGORIES, allColumns, SQLiteHelper.COLUMN_ID + " = '" + id + "'", null, null, null, null);

		GridItem gridItem = null;
		
		if(cursor.moveToFirst()){
			gridItem = cursorToCategory(cursor);
		}

		cursor.close();

		return gridItem;
	}

	public GridItem getEmptyCategory(){
		Cursor cursor = database.query(SQLiteHelper.TABLE_CATEGORIES, allColumns, SQLiteHelper.COLUMN_VALUE + " = '" + GridItem.EMPTY_CATEGORY_VALUE + "'", null, null, null, null);

		GridItem gridItem = null;

		if(cursor.moveToFirst()){
			gridItem = cursorToCategory(cursor);
		}

		return gridItem;
	}

	public void storeCategory(GridItem gridItem){
		ContentValues values = new ContentValues();

		values.put(SQLiteHelper.COLUMN_VALUE, gridItem.getName());
		values.put(SQLiteHelper.COLUMN_PARENT, gridItem.getParent());
		values.put(SQLiteHelper.COLUMN_DRAWABLE_ID, gridItem.getDrawableId());

		database.insert(SQLiteHelper.TABLE_CATEGORIES, null, values);
	}

	public void deleteCategory(GridItem gridItem) {

		database.delete(SQLiteHelper.TABLE_CATEGORIES, 
				SQLiteHelper.COLUMN_ID + " = '" + gridItem.getName()
				+ "' AND " + SQLiteHelper.COLUMN_PARENT + " = '" + gridItem.getParent() + "'", null);
	}
	
	public void updateCategory(GridItem gridItem){
		String where = SQLiteHelper.COLUMN_ID + "=?";
		String[] whereArgs = new String[] {gridItem.getId()};

		ContentValues dataToInsert = new ContentValues();                          
		dataToInsert.put(SQLiteHelper.COLUMN_VALUE, gridItem.getName());
		dataToInsert.put(SQLiteHelper.COLUMN_PARENT, gridItem.getParent());
		dataToInsert.put(SQLiteHelper.COLUMN_DRAWABLE_ID, gridItem.getDrawableId());
		
		database.update(SQLiteHelper.TABLE_CATEGORIES, dataToInsert, where, whereArgs);
	}

	public String getCategoryId(String value){
		Cursor cursor = database.query(SQLiteHelper.TABLE_CATEGORIES, allColumns, SQLiteHelper.COLUMN_VALUE + " = '" + value + "'", null, null, null, null);

		String result = "";

		if(cursor.moveToFirst()){
			result = cursor.getString(cursor.getColumnIndex(SQLiteHelper.COLUMN_ID));
		}

		return result;
	}
}
