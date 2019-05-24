package it.mp.claudianiferrari.parserjson;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {

	// url to make request
	private static String jsonUrl = "http://api.androidhive.info/contacts/";
	private static String xmlUrl = "http://stackoverflow.com/feeds/";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Button jsonButton = (Button) findViewById(R.id.jsonButton);
		
		jsonButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new DownloadJsonObjectTask(MainActivity.this).execute(jsonUrl);
			}
		});
		
		Button xmlButton = (Button) findViewById(R.id.xmlButton);
		
		xmlButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new DownloadXmlObjectTask(MainActivity.this).execute(xmlUrl);
			}
		});
	}
}