package it.mp.claudianiferrari.parserjson;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class MainActivity extends Activity {

	// url to make request
	private static String url = "http://api.androidhive.info/contacts/";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		new DownloadJsonObjectTask(this).execute(url);
	}
	
	public void setContatti(List<String> contactsList){
		
		ListView listView = (ListView) findViewById(R.id.ListViewContatti);
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, contactsList);
		listView.setAdapter(arrayAdapter);
	}
}