package it.uniroma2.mp.passwordmanager;

import it.uniroma2.mp.passwordmanager.SubcategoryCreationDialog.SubcategoryDialogListener;
import it.uniroma2.mp.passwordmanager.model.GridItem;
import it.uniroma2.mp.passwordmanager.persistance.CategoriesDataSource;
import android.os.Bundle;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

public class CategoriesActivity extends FragmentActivity implements SubcategoryDialogListener {

	private String parentCategoryId;
	private CategoriesDataSource categoriesDataSource;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_categories);
		
		categoriesDataSource = new CategoriesDataSource(this);
		
		GridView gridview = (GridView) findViewById(R.id.categoriesGridView);

		parentCategoryId = getIntent().getStringExtra(GridItem.PARENT_PARAMETER_NAME);

		gridview.setAdapter(new ImageAdapter(this, parentCategoryId));

		gridview.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				
				FrameLayout layout = (FrameLayout) v;
				
				String parentCategoryDescription = ((TextView) layout.getChildAt(1)).getText().toString();
				String parentCategoryValue = GridItem.getValueFromDescription(parentCategoryDescription, CategoriesActivity.this);
				
				if(!parentCategoryValue.equals(GridItem.EMPTY_CATEGORY_VALUE)){// create a new category
					categoriesDataSource.open();
					String parentCategoryIdForChild = categoriesDataSource.getCategoryId(parentCategoryValue);
					categoriesDataSource.close();
					
					Intent intent = null;
					
					if(parentCategoryId.equals(GridItem.NULL_PARENT_VALUE)){
						intent = new Intent(CategoriesActivity.this, CategoriesActivity.class);
						intent.putExtra(GridItem.PARENT_PARAMETER_NAME, parentCategoryIdForChild);
						startActivity(intent);	
					}else{
						Toast.makeText(CategoriesActivity.this, "TestSubCatToast", Toast.LENGTH_LONG).show();
					}
					
									
				}else{
					showSubcategoryCreationDialog();
				}
			}
		});
	}
	
    private void showSubcategoryCreationDialog() {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = new SubcategoryCreationDialog();
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
    	
    	GridItem category = new GridItem(subcategoryName, R.drawable.bank, Integer.parseInt(parentCategoryId));
    	
    	categoriesDataSource.open();
    	categoriesDataSource.storeCategory(category);
    	categoriesDataSource.close();
    	
    	// TODO: invalidare la view per aggiungere nuova categoria
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        // User touched the dialog's negative button
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.categories, menu);
		return true;
	}

}
