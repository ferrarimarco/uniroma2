package it.uniroma2.mp.passwordmanager;

import it.uniroma2.mp.passwordmanager.model.GridItem;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;

public class PasswordsActivity extends FragmentActivity {

	private String parentCategoryId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_categories);

		GridView gridview = (GridView) findViewById(R.id.categoriesGridView);

		parentCategoryId = getIntent().getStringExtra(GridItem.PARENT_PARAMETER_NAME);

		gridview.setAdapter(new PasswordAdapter(this, parentCategoryId));

		gridview.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

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
