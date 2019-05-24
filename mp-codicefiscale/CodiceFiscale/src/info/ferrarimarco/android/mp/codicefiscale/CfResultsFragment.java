package info.ferrarimarco.android.mp.codicefiscale;

import info.ferrarimarco.android.mp.codicefiscale.helper.CodiceFiscaleHelper;
import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;


public class CfResultsFragment extends Fragment {
	
	private View view;
	
	private boolean updateEnabled;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.cf_results_fragment_layout, container, false);
		
		EditText resultsEditText = (EditText) view.findViewById(R.id.resultTextView);
		
		resultsEditText.addTextChangedListener(new TextWatcher(){

			@Override
			public void afterTextChanged(Editable s) {
				if (updateEnabled) {
					
					updateEnabled = false;
					
					String cf = s.toString().substring(0, 15);
					String controlCode = CodiceFiscaleHelper.computeControlCode(cf);
					CfResultsFragment cfResultsFragment = (CfResultsFragment) MainActivity.getCurrentSupportFragmentManager().findFragmentById(R.id.CfResultsFragment);
					String currentCfResultsContent = cfResultsFragment.getCurrentCfResultsFieldContents();
					
					((EditText) view.findViewById(R.id.resultTextView)).setText(currentCfResultsContent.substring(0, 15) + controlCode);					
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {}
			
		});
		
		return view;
	}
	
	public String getCurrentCfResultsFieldContents(){
		return ((EditText) view.findViewById(R.id.resultTextView)).getText().toString();
	}
	
	public void updateCfResultsField(String update){
		
		updateEnabled = true;
		
		((EditText) view.findViewById(R.id.resultTextView)).setText(update);
	}

}
