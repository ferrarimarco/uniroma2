package it.uniroma2.mp.passwordmanager;

import it.uniroma2.mp.passwordmanager.model.GridItem;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;

public class SubcategoryCreationDialog extends DialogFragment {	

	private String subcategoryName;
	private int categoryDrawableId;
	private String categoryId;
	
	private ImageAdapter imageAdapter;

	public SubcategoryCreationDialog(){
		subcategoryName = "";
		categoryDrawableId = -1;
	}

	/* The activity that creates an instance of this dialog fragment must
	 * implement this interface in order to receive event callbacks.
	 * Each method passes the DialogFragment in case the host needs to query it. */
	public interface SubcategoryDialogListener {
		public void onDialogPositiveClick(DialogFragment dialog);
		public void onDialogNegativeClick(DialogFragment dialog);
	}

	// Use this instance of the interface to deliver action events
	SubcategoryDialogListener mListener;

	public Dialog onCreateDialog(Bundle savedInstanceState) {
		LayoutInflater inflater = getActivity().getLayoutInflater();

		// Build the dialog and set up the button click handlers
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		// Inflate and set the layout for the dialog
		// Pass null as the parent view because its going in the dialog layout
		final View view = inflater.inflate(R.layout.subcategory_creation_dialog, null);
		
		int dialogTitleId = 0;
		
		if(subcategoryName.isEmpty() && categoryId.equals("-1")){
			dialogTitleId = R.string.create_new_category_dialog_title;
		}else{
			dialogTitleId = R.string.edit_category_dialog_title;
		}
		
		builder.setView(view).setMessage(dialogTitleId)
		.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {

				EditText categoryNameEditText = (EditText) view.findViewById(R.id.subcategory_name_edittext);
				subcategoryName = categoryNameEditText.getText().toString();

				// Send the positive button event back to the host activity
				mListener.onDialogPositiveClick(SubcategoryCreationDialog.this);
			}
		})
		.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				// Send the negative button event back to the host activity
				mListener.onDialogNegativeClick(SubcategoryCreationDialog.this);
			}
		});

		GridView imageChooserGridView = (GridView) view.findViewById(R.id.image_chooser_gridView);
		imageAdapter = new ImageAdapter(view.getContext(), GridItem.CUSTOM_CATEGORY_DRAWABLE_ID);

		imageChooserGridView.setAdapter(imageAdapter);
		imageChooserGridView.setNumColumns(imageAdapter.getGridItemCount());

		imageChooserGridView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				categoryDrawableId = imageAdapter.getDrawableId(position);
			}
		});

		if(!subcategoryName.isEmpty()){
			EditText subcategoryNameEditText = (EditText) view.findViewById(R.id.subcategory_name_edittext);
			subcategoryNameEditText.setText(subcategoryName);
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
			mListener = (SubcategoryDialogListener) activity;
		} catch (ClassCastException e) {
			// The activity doesn't implement the interface, throw exception
			throw new ClassCastException(activity.toString()
					+ " must implement PasswordCreationDialogListener");
		}
	}

	public String getSubcategoryName() {
		return subcategoryName;
	}

	public void setSubcategoryName(String subcategoryName) {
		this.subcategoryName = subcategoryName;
	}

	public int getCategoryDrawableId() {
		return categoryDrawableId;
	}

	public void setCategoryDrawableId(int categoryDrawableId) {
		this.categoryDrawableId = categoryDrawableId;
	}

	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}
}
