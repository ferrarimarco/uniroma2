package it.uniroma2.mp.passwordmanager;

import it.uniroma2.mp.passwordmanager.authentication.AuthenticationTableGenerator;
import it.uniroma2.mp.passwordmanager.authentication.MasterPasswordManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MasterPasswordInitActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_master_password_init);
		
		EditText masterPasswordEditText = (EditText) findViewById(R.id.masterPasswordEditText);
		masterPasswordEditText.requestFocus();
		
		Button masterPasswordOkButton = (Button) findViewById(R.id.masterPasswordOkButton);
		masterPasswordOkButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				EditText masterPasswordEditText = (EditText) findViewById(R.id.masterPasswordEditText);
				EditText masterPasswordConfirmationEditText = (EditText) findViewById(R.id.masterPasswordConfirmationEditText);
				
				String masterPassword = masterPasswordEditText.getText().toString();
				String masterPasswordConfirmation = masterPasswordConfirmationEditText.getText().toString();
				
				if(masterPassword.length() == AuthenticationTableGenerator.passwordLength){
					if(!masterPassword.equals(masterPasswordConfirmation)){
						// Wrong master password
						// TODO: real time check onTextChanged
						
						Toast.makeText(MasterPasswordInitActivity.this, getString(R.string.masterPasswordNotConfirmed), Toast.LENGTH_SHORT).show();
					}else if(masterPassword.contains("-") || masterPassword.contains("'")){
						Toast.makeText(MasterPasswordInitActivity.this, getString(R.string.masterPasswordInvalidChars), Toast.LENGTH_SHORT).show();
					}else{
						MasterPasswordManager masterPasswordManager = new MasterPasswordManager(MasterPasswordInitActivity.this);
						masterPasswordManager.storeMasterPassword(masterPassword);
						
						Toast.makeText(MasterPasswordInitActivity.this, getString(R.string.masterPasswordOkConfirmation), Toast.LENGTH_SHORT).show();
						
						// return to the main activity
						Intent intent = new Intent(MasterPasswordInitActivity.this, MainActivity.class);
					    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);   
					    startActivity(intent);
					}				
				}else{
					Toast.makeText(MasterPasswordInitActivity.this, getString(R.string.masterPasswordLengthError), Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.master_password_init, menu);
		return true;
	}

}
