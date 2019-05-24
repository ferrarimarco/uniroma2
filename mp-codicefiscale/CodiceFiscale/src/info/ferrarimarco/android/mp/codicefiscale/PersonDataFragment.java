package info.ferrarimarco.android.mp.codicefiscale;

import info.ferrarimarco.android.mp.codicefiscale.helper.CodiceFiscaleHelper;
import android.os.Bundle;
import android.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;


public class PersonDataFragment extends Fragment{
	
	private View view;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.person_data_fragment_layout, container, false);
		
		EditText surnameEditText = (EditText) view.findViewById(R.id.surnameEditText);
		
		surnameEditText.addTextChangedListener(new TextWatcher(){

			@Override
			public void afterTextChanged(Editable s) {
				String surname = s.toString();
				
				String surnameCodiceFiscale = CodiceFiscaleHelper.computeSurname(surname);
				
				CfResultsFragment cfResultsFragment = (CfResultsFragment) MainActivity.getCurrentSupportFragmentManager().findFragmentById(R.id.CfResultsFragment);
				
				String currentCfResultsContent = cfResultsFragment.getCurrentCfResultsFieldContents();
				cfResultsFragment.updateCfResultsField(surnameCodiceFiscale + currentCfResultsContent.substring(3));
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {}
			
		});
		
		EditText nameEditText = (EditText) view.findViewById(R.id.nameEditText);
		
		nameEditText.addTextChangedListener(new TextWatcher(){

			@Override
			public void afterTextChanged(Editable s) {
				String name = s.toString();
				
				String nameCodiceFiscale = CodiceFiscaleHelper.computeName(name);
				
				CfResultsFragment cfResultsFragment = (CfResultsFragment) MainActivity.getCurrentSupportFragmentManager().findFragmentById(R.id.CfResultsFragment);
				
				String currentCfResultsContent = cfResultsFragment.getCurrentCfResultsFieldContents();
				
				String first = currentCfResultsContent.substring(0, 3);
				String after = currentCfResultsContent.substring(6);
				
				cfResultsFragment.updateCfResultsField(first + nameCodiceFiscale + after);	
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {}
			
		});
		
		return view;
	}
	
	public boolean isMaleRadioChecked(){
		RadioButton maleRadio = (RadioButton) view.findViewById(R.id.maleRadio);
		
		return maleRadio.isChecked();
	}
}
