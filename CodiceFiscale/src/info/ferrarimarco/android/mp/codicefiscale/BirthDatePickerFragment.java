package info.ferrarimarco.android.mp.codicefiscale;

import java.util.Calendar;

import android.os.Bundle;
import android.app.DialogFragment;
import android.widget.DatePicker;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;

public class BirthDatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

	private OnDatePickedListener datePickedListenerCallback;
	
	public interface OnDatePickedListener {
        public void onDatePicked(int year, int month, int day);
    }
	
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
        	datePickedListenerCallback = (OnDatePickedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnDatePickedListener");
        }
    }
	
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }
	
	
	@Override
	public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
		datePickedListenerCallback.onDatePicked(arg1, arg2, arg3);
	}

}
