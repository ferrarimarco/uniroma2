package it.uniroma2.mp.passwordmanager;

import java.util.List;
import java.util.Random;

import it.uniroma2.mp.passwordmanager.persistance.PasswordDataSource;
import it.uniroma2.mp.passwordmanager.persistance.model.Password;
import android.os.Bundle;
import android.app.Activity;
import android.app.ListActivity;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;

public class MainActivity extends ListActivity {
	
	private PasswordDataSource datasource;

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.main, menu);
//		return true;
//	}	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		datasource = new PasswordDataSource(this);
		datasource.open();

		List<Password> values = datasource.getAllPasswords(PasswordType.STORED);

		// Use the SimpleCursorAdapter to show the
		// elements in a ListView
		ArrayAdapter<Password> adapter = new ArrayAdapter<Password>(this,
				android.R.layout.simple_list_item_1, values);
		setListAdapter(adapter);
	}

	// Will be called via the onClick attribute
	// of the buttons in main.xml
	public void onClick(View view) {
		@SuppressWarnings("unchecked")
		ArrayAdapter<Password> adapter = (ArrayAdapter<Password>) getListAdapter();
		Password password = null;
		switch (view.getId()) {
		case R.id.add:
			String[] comments = new String[] { "Cool", "Very nice", "Hate it" };
			int nextInt = new Random().nextInt(3);
			// Save the new comment to the database
			password = datasource.createPassword(comments[nextInt], Integer.toString(nextInt), PasswordType.STORED);
			adapter.add(password);
			break;
		case R.id.delete:
			if (getListAdapter().getCount() > 0) {
				password = (Password) getListAdapter().getItem(0);
				datasource.deletePassword(password, PasswordType.STORED);
				adapter.remove(password);
			}
			break;
		}
		adapter.notifyDataSetChanged();
	}

	@Override
	protected void onResume() {
		datasource.open();
		super.onResume();
	}

	@Override
	protected void onPause() {
		datasource.close();
		super.onPause();
	}

}
