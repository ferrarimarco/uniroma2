package it.uniroma2.mp.passwordmanager.persistance;

import java.util.ArrayList;
import java.util.List;

import it.uniroma2.mp.passwordmanager.model.Category;
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

	private Category cursorToCategory(Cursor cursor){
		
		String nameId = cursor.getString(cursor.getColumnIndex(SQLiteHelper.COLUMN_VALUE));
		String name = Category.getDescription(nameId, context);
		String drawableId = cursor.getString(cursor.getColumnIndex(SQLiteHelper.COLUMN_DRAWABLE_ID));
		String parent = cursor.getString(cursor.getColumnIndex(SQLiteHelper.COLUMN_PARENT));

		return new Category(name, Integer.parseInt(drawableId), Integer.parseInt(parent));
	}
	
	public List<Category> getAllCategories(String parent){

		Cursor cursor = database.query(SQLiteHelper.TABLE_CATEGORIES, allColumns, SQLiteHelper.COLUMN_PARENT + " = '" + parent + "' AND " + SQLiteHelper.COLUMN_VALUE + " != '" + Category.EMPTY_CATEGORY_VALUE + "'", null, null, null, null);

		List<Category> categories = new ArrayList<Category>();

		if(cursor.moveToFirst()){
			while (!cursor.isAfterLast()) {
				categories.add(cursorToCategory(cursor));
				cursor.moveToNext();
			}
		}

		cursor.close();

		return categories;
	}

	public Category getEmptyCategory(){
		Cursor cursor = database.query(SQLiteHelper.TABLE_CATEGORIES, allColumns, SQLiteHelper.COLUMN_VALUE + " = '" + Category.EMPTY_CATEGORY_VALUE + "'", null, null, null, null);

		Category category = null;

		if(cursor.moveToFirst()){
			String name = cursor.getString(cursor.getColumnIndex(SQLiteHelper.COLUMN_VALUE));
			String drawableId = cursor.getString(cursor.getColumnIndex(SQLiteHelper.COLUMN_DRAWABLE_ID));
			String parent = cursor.getString(cursor.getColumnIndex(SQLiteHelper.COLUMN_PARENT));

			category = new Category(name, Integer.parseInt(drawableId), Integer.parseInt(parent));
		}

		return category;
	}

	public void storeCategory(Category category){
		ContentValues values = new ContentValues();

		values.put(SQLiteHelper.COLUMN_VALUE, category.getName());
		values.put(SQLiteHelper.COLUMN_PARENT, category.getParent());

		database.insert(SQLiteHelper.TABLE_CATEGORIES, null, values);
	}

	public void deleteCategory(Category category) {

		database.delete(SQLiteHelper.TABLE_CATEGORIES, 
				SQLiteHelper.COLUMN_ID + " = '" + category.getName()
				+ "' AND " + SQLiteHelper.COLUMN_PARENT + " = '" + category.getParent() + "'", null);
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
