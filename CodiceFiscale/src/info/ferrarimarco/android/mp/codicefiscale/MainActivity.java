package info.ferrarimarco.android.mp.codicefiscale;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		/****** AutoCompleteTextView field initialization ******/
		AutoCompleteTextView provinceAutoComplete = (AutoCompleteTextView) findViewById(R.id.provinceAutoCompleteTextView);
		AutoCompleteTextView townAutoComplete = (AutoCompleteTextView) findViewById(R.id.cityAutoCompleteTextView);
		ArrayAdapter<String> provinceAdapter = new ArrayAdapter<String>(this, 
				                                        android.R.layout.simple_dropdown_item_1line, 
				                                        new String[] {"Roma",
				                                                      "Milano",
				                                                      "Parma",
				                                                      "Torino",
				                                                      "Sanbuceto"});
		ArrayAdapter<String> townAdapter = new ArrayAdapter<String>(this, 
                android.R.layout.simple_dropdown_item_1line, 
                new String[] {"Civitavecchia",
                              "Trombopolis",
                              "Inculopolis",
                              "Tivoli",
                              "Sanbuceto"});
		provinceAutoComplete.setAdapter(provinceAdapter);
		townAutoComplete.setAdapter(townAdapter);
		/****** End AutoCompleteTextView field initialization ******/
		
		/****** Adding an OnItemClickListener to AutoCompleteTextView fields... ******/
		provinceAutoComplete.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> provinceAdapter, View view, int pos,
					long id) {
				String selection = (String) provinceAdapter.getItemAtPosition(pos);
				Toast.makeText(
        				getApplicationContext(), 
        				"Ah, abiti in provincia di " + selection + "?", 
        				Toast.LENGTH_LONG
        			).show();
			}
		});
		
		townAutoComplete.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> townAdapter, View view, int pos,
					long id) {
				String selection = (String) townAdapter.getItemAtPosition(pos);
				Toast.makeText(
        				getApplicationContext(), 
        				"Ah, abiti a " + selection + "?", 
        				Toast.LENGTH_LONG
        			).show();
				Toast.makeText(
        				getApplicationContext(), 
        				"Ma 'n te vergogni??", 
        				Toast.LENGTH_LONG
        			).show();
			}
		});
		/****** Finished adding an OnItemClickListener to AutoCompleteTextView fields... ******/
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
