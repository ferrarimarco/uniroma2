package info.ferrarimarco.android.mp.codicefiscale;

import android.content.Context;
import android.os.Bundle;

import android.app.Activity;
import android.app.FragmentManager;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

public class MainActivity extends Activity implements BirthDatePickerFragment.OnDatePickedListener {
	
	private static Context context;
	private static FragmentManager fragmentManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		MainActivity.context = getApplicationContext();
		MainActivity.fragmentManager = getFragmentManager();
		
		setContentView(R.layout.activity_main);
		
		// Here because if not I get this bug: https://code.google.com/p/android/issues/detail?id=5237
		// when I'll find a fix I will move this init code in BirthPlaceDataFragment
		ArrayAdapter<String> provinceAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, new String[] {"Roma", "Milano", "Parma", "Torino"});
		AutoCompleteTextView provinceAutoComplete = (AutoCompleteTextView) findViewById(R.id.provinceAutoCompleteTextView);
		provinceAutoComplete.setAdapter(provinceAdapter);
		
		ArrayAdapter<String> townAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, new String[] {"Roma", "Latina", "Frosinone", "Viterbo", "Rieti"});
		AutoCompleteTextView townAutoComplete = (AutoCompleteTextView) findViewById(R.id.cityAutoCompleteTextView);
		townAutoComplete.setAdapter(townAdapter);
		// end code to fix		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onDatePicked(int year, int month, int day) {
		BirthDateFragment birthDateFragment = (BirthDateFragment) fragmentManager.findFragmentById(R.id.birthDateFragment);

        if (birthDateFragment != null) {
        	birthDateFragment.updateDateFieldContent(year, month, day);
        }
	}

	public static Context getContext() {
		return context;
	}

	public static FragmentManager getCurrentSupportFragmentManager() {
		return fragmentManager;
	}
}
