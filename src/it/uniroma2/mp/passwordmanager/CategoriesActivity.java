package it.uniroma2.mp.passwordmanager;

import it.uniroma2.mp.passwordmanager.model.Category;
import it.uniroma2.mp.passwordmanager.persistance.CategoriesDataSource;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

public class CategoriesActivity extends Activity {

	private String parentCategoryId;
	private CategoriesDataSource categoriesDataSource;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_categories);
		
		categoriesDataSource = new CategoriesDataSource(this);
		
		GridView gridview = (GridView) findViewById(R.id.categoriesGridView);

		parentCategoryId = getIntent().getStringExtra(Category.PARENT_PARAMETER_NAME);

		gridview.setAdapter(new ImageAdapter(this, parentCategoryId));

		gridview.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				
				FrameLayout layout = (FrameLayout) v;
				
				String parentCategoryValue = ((TextView) layout.getChildAt(1)).getText().toString();
				
				Toast.makeText(CategoriesActivity.this, parentCategoryValue, Toast.LENGTH_SHORT).show();
				
				categoriesDataSource.open();
				String parentCategoryIdForChild = categoriesDataSource.getCategoryId(parentCategoryValue);
				categoriesDataSource.close();
				
				Intent intent = new Intent(CategoriesActivity.this, CategoriesActivity.class);
				intent.putExtra(Category.PARENT_PARAMETER_NAME, parentCategoryIdForChild);
				startActivity(intent);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.categories, menu);
		return true;
	}

}
