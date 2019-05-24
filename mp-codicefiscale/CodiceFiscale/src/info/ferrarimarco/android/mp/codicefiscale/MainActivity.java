package info.ferrarimarco.android.mp.codicefiscale;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import info.ferrarimarco.android.mp.codicefiscale.helper.CodiceFiscaleHelper;
import info.ferrarimarco.android.mp.codicefiscale.persistance.BelfioreDataSource;
import android.content.Context;
import android.os.Bundle;

import android.app.Activity;
import android.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
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
		
		final BelfioreDataSource belfiore = new BelfioreDataSource(context);
		belfiore.open();
		
		List<String> provincie = belfiore.getProvincie();
		String[] provincieArray = provincie.toArray(new String[provincie.size()]);
		
		ArrayAdapter<String> provinceAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, provincieArray);	
		AutoCompleteTextView provinceAutoComplete = (AutoCompleteTextView) findViewById(R.id.provinceAutoCompleteTextView);
		provinceAutoComplete.setAdapter(provinceAdapter);
		
		provinceAutoComplete.addTextChangedListener(new TextWatcher(){

			@Override
			public void afterTextChanged(Editable s) {
				if(s.length() > 1){
					List<String> comuni = belfiore.getComuni(s.toString());
					String[] comuniArray = comuni.toArray(new String[comuni.size()]);
					
					ArrayAdapter<String> comuniAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_dropdown_item_1line, comuni);
					AutoCompleteTextView comuniAutoComplete = (AutoCompleteTextView) findViewById(R.id.cityAutoCompleteTextView);
					comuniAutoComplete.setAdapter(comuniAdapter);
				}				
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				
			}});
		
		AutoCompleteTextView comuniAutoComplete = (AutoCompleteTextView) findViewById(R.id.cityAutoCompleteTextView);
		comuniAutoComplete.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				
				if(s.length() > 1){
					String idNazionale = belfiore.getIdNazionale(s.toString());
					
					CfResultsFragment cfResultsFragment = (CfResultsFragment) MainActivity.getCurrentSupportFragmentManager().findFragmentById(R.id.CfResultsFragment);
					
					String currentCfResultsContent = cfResultsFragment.getCurrentCfResultsFieldContents();
					
					String firstPart = currentCfResultsContent.substring(0, 11);
					String lastPart = currentCfResultsContent.substring(15);
					
					cfResultsFragment.updateCfResultsField(firstPart + idNazionale + lastPart); 
				}
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}
		});
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

		month++;
		
        if (birthDateFragment != null) {
        	birthDateFragment.updateDateFieldContent(year, month, day);
        	
        	PersonDataFragment personDataFragment = (PersonDataFragment) MainActivity.getCurrentSupportFragmentManager().findFragmentById(R.id.personDataFragment);
        	boolean male = personDataFragment.isMaleRadioChecked();
        	
        	Calendar birthDate = new GregorianCalendar(year, month, day);
        	
        	String cfBirthdateDay = CodiceFiscaleHelper.computeBirthDateDay(birthDate, male);
        	String cfBirthdateMonth = CodiceFiscaleHelper.computeBirthDateMonth(birthDate);
        	String cfBirthdateYear = CodiceFiscaleHelper.computeBirthDateYear(birthDate);
        	
        	String cfBirthdateComplete = cfBirthdateYear + cfBirthdateMonth + cfBirthdateDay;
        	
			CfResultsFragment cfResultsFragment = (CfResultsFragment) MainActivity.getCurrentSupportFragmentManager().findFragmentById(R.id.CfResultsFragment);
			
			String currentCfResultsContent = cfResultsFragment.getCurrentCfResultsFieldContents();
			
			cfResultsFragment.updateCfResultsField(currentCfResultsContent.substring(0, 6) + cfBirthdateComplete + currentCfResultsContent.substring(11));
        }
	}

	public static Context getContext() {
		return context;
	}

	public static FragmentManager getCurrentSupportFragmentManager() {
		return fragmentManager;
	}
}
