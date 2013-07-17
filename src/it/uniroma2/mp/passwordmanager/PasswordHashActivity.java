package it.uniroma2.mp.passwordmanager;

import it.uniroma2.mp.passwordmanager.authentication.AuthenticationTableGenerator;
import it.uniroma2.mp.passwordmanager.authentication.MasterPasswordManager;
import it.uniroma2.mp.passwordmanager.encryption.HashHelper;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;

public class PasswordHashActivity extends Activity {

	private int authenticationTableIndex;
	private StringBuilder insertedMasterPassword;
	private String[] serializedTables;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_authentication);		

		// Get the intent that started this activity
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		String input = extras.getString(android.content.Intent.EXTRA_TEXT);

		serializedTables = input.split(";");

		initializeAuthenticationSequence();
	}

	private void showAuthenticationTable(){

		MasterPasswordManager masterPasswordManager = new MasterPasswordManager(this);

		String[][] authenticationTable = masterPasswordManager.deserializeAuthenticationTable(serializedTables[authenticationTableIndex]);

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
			// return the SHA1 hash as result
			String hash = HashHelper.computeSHAHash(insertedMasterPassword.toString());
			
			// Create intent to deliver some kind of result data
			Intent result = new Intent("it.uniroma2.mp.passwordmanager.HASH_RESULT");
			result.putExtra(Intent.EXTRA_TEXT, hash);
			setResult(Activity.RESULT_OK, result);
			finish();
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.password_hash, menu);
		return true;
	}

}
