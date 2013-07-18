package it.uniroma2.mp.passwordmanager;

import it.uniroma2.mp.passwordmanager.authentication.AuthenticationTableGenerator;
import it.uniroma2.mp.passwordmanager.authentication.MasterPasswordManager;
import it.uniroma2.mp.passwordmanager.configuration.ConfigurationManager;
import it.uniroma2.mp.passwordmanager.model.GridItem;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;

public class AuthenticationActivity extends Activity {

	private int authenticationTableIndex;
	private StringBuilder insertedMasterPassword;
	private MasterPasswordManager masterPasswordManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_authentication);
		// Show the Up button in the action bar.
		setupActionBar();
	}
	
	private void showAuthenticationTable(){
		masterPasswordManager = new MasterPasswordManager(this);
		
		String[][] authenticationTable = masterPasswordManager.loadAuthenticationTable(authenticationTableIndex);
		
		AuthenticationTableGenerator authenticationTableGenerator = new AuthenticationTableGenerator();
		String[][] scrambledTable = authenticationTableGenerator.scrambleTable(authenticationTable);		
		String[] scrambledTableOneRow = new String[scrambledTable.length * scrambledTable[0].length];
		
		for(int i = 0; i < scrambledTable.length; i++){
			for(int j = 0; j < scrambledTable[i].length; j++){
				scrambledTableOneRow[i * scrambledTable[i].length + j] = scrambledTable[i][j];
			}
		}
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, scrambledTableOneRow);
		GridView gridView = (GridView) findViewById(R.id.authenticationTable);
		gridView.setAdapter(adapter);
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				tableLetterClick(v);
			}});
		
		this.authenticationTableIndex++;
	}
	
	// Set the values for the first authentication table
	private void initializeAuthenticationSequence(){
		authenticationTableIndex = 0;
		insertedMasterPassword = new StringBuilder(AuthenticationTableGenerator.passwordLength);
		
		TextView masterPasswordTextView = (TextView) findViewById(R.id.masterPasswordTextView);
		masterPasswordTextView.setText("");
		
		showAuthenticationTable();
	}
	
	public void tableLetterClick(View v){
		TextView view = (TextView) v;
		
		insertedMasterPassword.append(view.getText());
		
		TextView masterPasswordTextView = (TextView) findViewById(R.id.masterPasswordTextView);
		
		StringBuilder sb = new StringBuilder(masterPasswordTextView.getText());
		sb.append("0");
		
		masterPasswordTextView.setText(sb.toString());
		
		if(authenticationTableIndex < AuthenticationTableGenerator.passwordLength){
			showAuthenticationTable();
		}else{
			boolean authenticationResult = masterPasswordManager.checkMasterPassword(insertedMasterPassword.toString());
			
			if(authenticationResult){
				Intent intent = new Intent(this, CategoriesActivity.class);
				intent.putExtra(GridItem.PARENT_PARAMETER_NAME, GridItem.NULL_PARENT_VALUE);
				startActivity(intent);
			}else{
				initializeAuthenticationSequence();
			}
		}
		
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onResume() {
		//datasource.open();
		super.onResume();
		
		ConfigurationManager configurationManager = new ConfigurationManager(this);
		
		if(configurationManager.isMasterPasswordConfigured()){// normal application flow
			initializeAuthenticationSequence();
		}else{
			// The master password is already initialized
			// Get back to MainActivity that knows what to do next
			Intent intent = new Intent(this, MainActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			finish();
		}
	}

}
