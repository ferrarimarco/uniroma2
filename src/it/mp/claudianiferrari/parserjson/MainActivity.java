package it.mp.claudianiferrari.parserjson;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {

	// url to make request
	private static String url = "http://api.androidhive.info/contacts/";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		new DownloadJsonObjectTask(this).execute(url);
	}
}