package info.ferrarimarco.android.mp.codicefiscale;

import android.os.Bundle;
import android.app.DialogFragment;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;


public class BirthDateFragment extends Fragment {
	
	private View view;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.birth_date_fragment_layout, container, false);
		
		Button fillDateButton = (Button) view.findViewById(R.id.fillDateButton);
		fillDateButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showDatePickerDialog(v);				
			}
		});
		
		return view;
	}
	
	public void updateDateFieldContent(int year, int month, int day){
		EditText dateEditText = (EditText) view.findViewById(R.id.dateEditText);
		
		//TODO: rewrite this to be locale sensitive
		dateEditText.setText(day + "/" + month + "/" + year);		
	}
	
	public void showDatePickerDialog(View v) {
	    DialogFragment newFragment = new BirthDatePickerFragment();
	    newFragment.show(MainActivity.getCurrentSupportFragmentManager(), "datePicker");
	}
}
