package it.uniroma2.mp.passwordmanager;

import it.uniroma2.mp.passwordmanager.authentication.AuthenticationTableGenerator;
import it.uniroma2.mp.passwordmanager.authentication.MasterPasswordManager;
import it.uniroma2.mp.passwordmanager.configuration.ConfigurationManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
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
					    
					    finish();
					}		
				}else{
					Toast.makeText(MasterPasswordInitActivity.this, getString(R.string.masterPasswordLengthError), Toast.LENGTH_SHORT).show();
				}
			}
		});
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		ConfigurationManager configurationManager = new ConfigurationManager(this);
		
		if(!configurationManager.isMasterPasswordConfigured()){// show master password init activity
			EditText masterPasswordEditText = (EditText) findViewById(R.id.masterPasswordEditText);
			masterPasswordEditText.requestFocus();
			masterPasswordEditText.setText("");
			
			EditText masterPasswordConfirmationEditText = (EditText) findViewById(R.id.masterPasswordConfirmationEditText);
			masterPasswordConfirmationEditText.setText("");
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
