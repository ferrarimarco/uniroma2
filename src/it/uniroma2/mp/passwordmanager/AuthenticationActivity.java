package it.uniroma2.mp.passwordmanager;

import it.uniroma2.mp.passwordmanager.authentication.AuthenticationTableGenerator;
import it.uniroma2.mp.passwordmanager.authentication.MasterPasswordManager;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.NavUtils;
import android.annotation.TargetApi;
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
		
		initializeAuthenticationSequence();
	}
	
	private void showAuthenticationTable(){
		masterPasswordManager = new MasterPasswordManager(this);
		
		String[][] authenticationTable = masterPasswordManager.loadAuthenticationTable(authenticationTableIndex);
		
		AuthenticationTableGenerator authenticationTableGenerator = new AuthenticationTableGenerator();
		String[][] scrambledTable = authenticationTableGenerator.scrambleTable(authenticationTable);
		
		TableLayout table = (TableLayout) findViewById(R.id.authenticationTableLayout);
		
		for(int i = 0; i < table.getChildCount(); i++){
			
			TableRow row = (TableRow) table.getChildAt(i);
			
			for(int j = 0; j < row.getChildCount(); j++){
				TextView letterTextView = (TextView) row.getChildAt(j);
				
				letterTextView.setText(scrambledTable[i][j]);
			}
		}
		
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
				Toast.makeText(this, "Password: " + insertedMasterPassword.toString(), Toast.LENGTH_LONG).show();
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
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.authentication, menu);
		return true;
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
		
		initializeAuthenticationSequence();
	}

}
