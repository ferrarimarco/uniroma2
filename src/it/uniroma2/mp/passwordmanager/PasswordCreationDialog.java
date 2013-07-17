package it.uniroma2.mp.passwordmanager;

import it.uniroma2.mp.passwordmanager.encryption.EncryptionAlgorithm;
import it.uniroma2.mp.passwordmanager.model.Password;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;

public class PasswordCreationDialog extends DialogFragment {	

	private String userId;
	private String passwordValue;
	private EncryptionAlgorithm algorithm;
	
	private long passwordId;
	
	public PasswordCreationDialog(){
		
		userId = "";
		passwordValue = "";
		
		algorithm = Password.DEFAULT_ENCRYPTION_ALGORITHM;
		
		passwordId = Password.DUMMY_PASSWORD_ID;
	}

	/* The activity that creates an instance of this dialog fragment must
	 * implement this interface in order to receive event callbacks.
	 * Each method passes the DialogFragment in case the host needs to query it. */
	public interface PasswordCreationDialogListener {
		public void onDialogPositiveClick(DialogFragment dialog);
		public void onDialogNegativeClick(DialogFragment dialog);
	}

	// Use this instance of the interface to deliver action events
	PasswordCreationDialogListener mListener;

	public Dialog onCreateDialog(Bundle savedInstanceState) {
		LayoutInflater inflater = getActivity().getLayoutInflater();

		// Build the dialog and set up the button click handlers
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		// Inflate and set the layout for the dialog
		// Pass null as the parent view because its going in the dialog layout
		final View view = inflater.inflate(R.layout.password_creation_dialog, null);

		int dialogTitleId = 0;
		
		if(userId.isEmpty() && passwordValue.isEmpty() && passwordId == Password.DUMMY_PASSWORD_ID){
			dialogTitleId = R.string.create_new_password_dialog_title;
		}else{
			dialogTitleId = R.string.edit_password_dialog_title;
		}
		
		builder.setView(view).setMessage(dialogTitleId)
		.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				
				EditText userIdEditText = (EditText) view.findViewById(R.id.user_id_edittext);
				EditText passwordEditText = (EditText) view.findViewById(R.id.password_value_edittext);
				
				userId = userIdEditText.getText().toString();
				passwordValue = passwordEditText.getText().toString();
				
				RadioGroup algorithmRadioGroup = (RadioGroup) view.findViewById(R.id.encryption_algorithm_radio_group);
				
				// get selected radio button from radioGroup
				int selectedId = algorithmRadioGroup.getCheckedRadioButtonId();
				
				if(selectedId == R.id.aes_radio){
					algorithm = EncryptionAlgorithm.AES;
				}else if(selectedId == R.id.blowfish_radio){
					algorithm = EncryptionAlgorithm.BLOWFISH;
				}
				
				// Send the positive button event back to the host activity
				mListener.onDialogPositiveClick(PasswordCreationDialog.this);
			}
		})
		.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				// Send the negative button event back to the host activity
				mListener.onDialogNegativeClick(PasswordCreationDialog.this);
			}
		});

		if(!userId.isEmpty()){
			EditText userIdEditText = (EditText) view.findViewById(R.id.user_id_edittext);
			userIdEditText.setText(userId);
		}
		
		if(!passwordValue.isEmpty()){
			EditText passwordEditText = (EditText) view.findViewById(R.id.password_value_edittext);
			passwordEditText.setText(passwordValue);
		}
		
		return builder.create();
	}

	// Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		// Verify that the host activity implements the callback interface
		try {
			// Instantiate the NoticeDialogListener so we can send events to the host
			mListener = (PasswordCreationDialogListener) activity;
		} catch (ClassCastException e) {
			// The activity doesn't implement the interface, throw exception
			throw new ClassCastException(activity.toString()
					+ " must implement PasswordCreationDialogListener");
		}
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getPasswordValue() {
		return passwordValue;
	}

	public void setPasswordValue(String passwordValue) {
		this.passwordValue = passwordValue;
	}

	public long getPasswordId() {
		return passwordId;
	}

	public void setPasswordId(long passwordId) {
		this.passwordId = passwordId;
	}

	public EncryptionAlgorithm getAlgorithm() {
		return algorithm;
	}

	public void setAlgorithm(EncryptionAlgorithm algorithm) {
		this.algorithm = algorithm;
	}
}
