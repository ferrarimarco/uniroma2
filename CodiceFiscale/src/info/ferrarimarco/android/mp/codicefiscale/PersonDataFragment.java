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
				
				EditText resultEditText = (EditText) view.findViewById(R.id.resultTextView);				
				String actualCodiceFiscale = resultEditText.getText().toString();
				
				resultEditText.setText(surnameCodiceFiscale + actualCodiceFiscale.substring(3));
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
				
				EditText resultEditText = (EditText) view.findViewById(R.id.resultTextView);				
				String actualCodiceFiscale = resultEditText.getText().toString();
				
				resultEditText.setText(actualCodiceFiscale.substring(0, 3) + nameCodiceFiscale + actualCodiceFiscale.substring(6));
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {}
			
		});
		
		return view;
	}
}
