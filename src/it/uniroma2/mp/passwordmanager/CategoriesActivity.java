package it.uniroma2.mp.passwordmanager;

import it.uniroma2.mp.passwordmanager.SubcategoryCreationDialog.SubcategoryDialogListener;
import it.uniroma2.mp.passwordmanager.authentication.MasterPasswordManager;
import it.uniroma2.mp.passwordmanager.model.GridItem;
import it.uniroma2.mp.passwordmanager.model.PasswordType;
import it.uniroma2.mp.passwordmanager.persistance.CategoriesDataSource;
import it.uniroma2.mp.passwordmanager.persistance.PasswordDataSource;
import android.os.Bundle;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

/***
 * Activity si occupa di:
 * 	1) Visualizzare Categorie e Sottocategorie
 *  2) Creare, modificare ed eliminare Sottocategorie
 *  	2.1) SubcategoryCreationDialog: in caso si voglia creare o modificare una Categoria
 *  3) Resettare la Master Password
 * Direziona l'utente verso le activity:
 *  1) CategoriesActivity: se si entra in una Categoria
 *  2) PasswordsActivity: se si entra in una sottocategoria.
 *  3) MasterPasswordInitActivity: nel caso si voglia resettare la Master Password
 * **/

public class CategoriesActivity extends FragmentActivity implements SubcategoryDialogListener {

	private String parentCategoryId;
	private CategoriesDataSource categoriesDataSource;
	PasswordDataSource passwordDataSource;
	
	private boolean notFirstStart;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_categories);
		
		notFirstStart = false;
		
		categoriesDataSource = new CategoriesDataSource(this);
		
		// To delete passwords when deleting a category
		passwordDataSource = new PasswordDataSource(this);

		GridView gridview = (GridView) findViewById(R.id.categoriesGridView);

		registerForContextMenu(gridview);

		parentCategoryId = getIntent().getStringExtra(GridItem.PARENT_PARAMETER_NAME);

		gridview.setAdapter(new ImageAdapter(this, parentCategoryId));

		gridview.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

				FrameLayout layout = (FrameLayout) v;

				TextView descriptionTextView = (TextView) layout.getChildAt(1);
				String parentCategoryIdForChild = (String) descriptionTextView.getTag();

				Intent intent = null;

				if(parentCategoryId.equals(GridItem.NULL_PARENT_VALUE)){
					intent = new Intent(CategoriesActivity.this, CategoriesActivity.class);
				}else{
					intent = new Intent(CategoriesActivity.this, PasswordsActivity.class);
				}

				intent.putExtra(GridItem.PARENT_PARAMETER_NAME, parentCategoryIdForChild);
				startActivity(intent);
			}
		});
	}

	private void showSubcategoryCreationDialog(String categoryName, String categoryId) {
		// Create an instance of the dialog fragment and show it
		SubcategoryCreationDialog dialog = new SubcategoryCreationDialog();

		if(!categoryName.isEmpty()){
			dialog.setSubcategoryName(categoryName);
		}
		
		if(!categoryId.isEmpty()){
			dialog.setCategoryId(categoryId);
		}else{
			dialog.setCategoryId(GridItem.DUMMY_CATEGORY_ID);
		}

		dialog.show(getSupportFragmentManager(), "SubcategoryDialogFragment");
	}


	// The dialog fragment receives a reference to this Activity through the
	// Fragment.onAttach() callback, which it uses to call the following methods
	// defined by the NoticeDialogFragment.NoticeDialogListener interface
	@Override
	public void onDialogPositiveClick(DialogFragment dialog) {
		// User touched the dialog's positive button

		SubcategoryCreationDialog creationDialog = (SubcategoryCreationDialog) dialog;

		String subcategoryName = creationDialog.getSubcategoryName();
		int categoryDrawableId = creationDialog.getCategoryDrawableId();
		String categoryId = creationDialog.getCategoryId();
		
		if (subcategoryName == null || subcategoryName.isEmpty()) {
			Toast.makeText(this, R.string.invalid_category_name, Toast.LENGTH_SHORT).show();
			showSubcategoryCreationDialog("", "");
		}else if(categoryDrawableId == -1){
			Toast.makeText(this, R.string.invalid_category_icon, Toast.LENGTH_SHORT).show();
			showSubcategoryCreationDialog(subcategoryName, categoryId);
		} else {// Store the new category and update GridView adapter to show changes

			GridItem category = new GridItem(subcategoryName, categoryDrawableId, Integer.parseInt(parentCategoryId), categoryId);

			categoriesDataSource.open();
			
			if(categoryId.equals(GridItem.DUMMY_CATEGORY_ID)){
				categoriesDataSource.storeCategory(category);
			}else{
				categoriesDataSource.updateCategory(category);
			}
			
			categoriesDataSource.close();

			GridView gridview = (GridView) findViewById(R.id.categoriesGridView);

			gridview.setAdapter(new ImageAdapter(this, parentCategoryId));
		}
	}

	@Override
	public void onDialogNegativeClick(DialogFragment dialog) {
		// User touched the dialog's negative button
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.categories, menu);

		if(parentCategoryId.equals(GridItem.NULL_PARENT_VALUE)){
			MenuItem createCategoryMenuItem = menu.findItem(R.id.new_category_menu_item);
			createCategoryMenuItem.setEnabled(false);
		}

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.new_category_menu_item:
			showSubcategoryCreationDialog("", "");
			return true;
		case R.id.reset_master_password_menu_item:
			new AlertDialog.Builder(this)
		    .setTitle(getString(R.string.master_password_dialog_title))
		    .setMessage(getString(R.string.master_password_dialog_text))
		    .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int which) { 
		            // continue with delete
		        	MasterPasswordManager masterPasswordManager = new MasterPasswordManager(CategoriesActivity.this);
		        	masterPasswordManager.resetMasterPassword();
		        	
		        	// return to the main activity
					Intent intent = new Intent(CategoriesActivity.this, MainActivity.class);
				    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
				    startActivity(intent);
				    
				    finish();
		        }
		     })
		    .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int which) { 
		            // do nothing
		        }
		     })
		     .show();
			return true;
		default:
			return super.onOptionsItemSelected(item);

		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		getMenuInflater().inflate(R.menu.item_contextual_menu , menu);
		
		if(parentCategoryId.equals(GridItem.NULL_PARENT_VALUE)){
			MenuItem editCategoryMenuItem = menu.findItem(R.id.edit_category_menu_item);
			editCategoryMenuItem.setEnabled(false);
			
			MenuItem deleteCategoryMenuItem = menu.findItem(R.id.delete_category_menu_item);
			deleteCategoryMenuItem.setEnabled(false);
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {

		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		FrameLayout frameLayout = (FrameLayout) info.targetView;

		TextView textView = (TextView) frameLayout.getChildAt(1);

		String categoryId = (String) textView.getTag();

		switch(item.getItemId()){
		case R.id.edit_category_menu_item:
			categoriesDataSource.open();
			GridItem gridItem = categoriesDataSource.getGridItem(categoryId);
			categoriesDataSource.close();
			showSubcategoryCreationDialog(gridItem.getName(), gridItem.getId());
			break;
		case R.id.delete_category_menu_item:
			categoriesDataSource.open();
			categoriesDataSource.deleteCategory(categoryId);
			categoriesDataSource.close();
			
			passwordDataSource.open();
			passwordDataSource.deleteAllPasswordsFromCategory(categoryId, PasswordType.STORED);
			passwordDataSource.close();
			
			GridView gridview = (GridView) findViewById(R.id.categoriesGridView);
			gridview.setAdapter(new ImageAdapter(this, parentCategoryId));
			
			Toast.makeText(this, getString(R.string.category_deleted_toast), Toast.LENGTH_SHORT).show();
			
			break;
		default:
			return super.onContextItemSelected(item);
		}

		return true;
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		if(notFirstStart){
			Intent intent = new Intent(this, MainActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);			
			finish();
		}
		
		notFirstStart = true;
	}
}
