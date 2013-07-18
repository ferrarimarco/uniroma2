package it.uniroma2.mp.passwordmanager;

import it.uniroma2.mp.passwordmanager.configuration.ConfigurationManager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends Activity {

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
		
		startNextActivity();
	}
	
	private void startNextActivity(){
		ConfigurationManager configurationManager = new ConfigurationManager(this);

		if(configurationManager.isMasterPasswordConfigured()){// normal application flow
			Intent intent = new Intent(this, AuthenticationActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		}else{// show master password init activity
			Intent intent = new Intent(this, MasterPasswordInitActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		startNextActivity();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}

}
