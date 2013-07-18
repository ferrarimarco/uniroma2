package it.uniroma2.mp.passwordmanager;

import it.uniroma2.mp.passwordmanager.PasswordCreationDialog.PasswordCreationDialogListener;
import it.uniroma2.mp.passwordmanager.authentication.MasterPasswordManager;
import it.uniroma2.mp.passwordmanager.encryption.EncryptionAlgorithm;
import it.uniroma2.mp.passwordmanager.model.GridItem;
import it.uniroma2.mp.passwordmanager.model.Password;
import it.uniroma2.mp.passwordmanager.model.PasswordType;
import it.uniroma2.mp.passwordmanager.persistance.PasswordDataSource;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

public class PasswordsActivity extends FragmentActivity implements PasswordCreationDialogListener {

	private String parentCategoryId;
	private boolean notFirstStart;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_passwords);

		notFirstStart = false;
		
		GridView gridview = (GridView) findViewById(R.id.passwordsGridView);

		parentCategoryId = getIntent().getStringExtra(GridItem.PARENT_PARAMETER_NAME);

		gridview.setAdapter(new PasswordAdapter(this, parentCategoryId));

		gridview.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

			}
		});
		
		registerForContextMenu(gridview);
	}
	
	private void showPasswordCreationDialog(String userId, String passwordValue, long passwordId, EncryptionAlgorithm encryptionAlgorithm) {
		// Create an instance of the dialog fragment and show it
		PasswordCreationDialog dialog = new PasswordCreationDialog();
		
		dialog.setUserId(userId);
		dialog.setPasswordValue(passwordValue);
		dialog.setPasswordId(passwordId);
		dialog.setAlgorithm(encryptionAlgorithm);
		
		dialog.show(getSupportFragmentManager(), "PasswordDialogFragment");
	}
	
	@Override
	public void onDialogPositiveClick(DialogFragment dialog) {
		// User touched the dialog's positive button

		PasswordCreationDialog creationDialog = (PasswordCreationDialog) dialog;
		
		String userId = creationDialog.getUserId();
		String passwordValue = creationDialog.getPasswordValue();
		long passwordId = creationDialog.getPasswordId();
		EncryptionAlgorithm algorithm = creationDialog.getAlgorithm();
		
		if (userId == null || userId.isEmpty()) {
			Toast.makeText(this, R.string.invalid_password_user_id, Toast.LENGTH_SHORT).show();
			
			showPasswordCreationDialog(userId, passwordValue, passwordId, algorithm);
			
		}else if(passwordValue == null || passwordValue.isEmpty()){
			Toast.makeText(this, R.string.invalid_password_value, Toast.LENGTH_SHORT).show();
			
			showPasswordCreationDialog(userId, passwordValue, passwordId, algorithm);
		} else {// Store the new password and update GridView adapter to show changes

			Password password = new Password(passwordId, passwordValue, userId, parentCategoryId, algorithm);
			
			PasswordDataSource passwordDataSource = new PasswordDataSource(this); 
			passwordDataSource.open();
			
			if(password.getId() == Password.DUMMY_PASSWORD_ID){
				passwordDataSource.createPassword(password, PasswordType.STORED);
			}else{
				passwordDataSource.updatePassword(password, PasswordType.STORED);
			}
			
			passwordDataSource.close();
			
			GridView gridview = (GridView) findViewById(R.id.passwordsGridView);
			gridview.setAdapter(new PasswordAdapter(this, parentCategoryId));
		}
	}
	
	@Override
	public void onDialogNegativeClick(DialogFragment dialog) {
		// User touched the dialog's negative button
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.categories, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.new_category_menu_item:
			showPasswordCreationDialog("", "", Password.DUMMY_PASSWORD_ID, EncryptionAlgorithm.AES);
			return true;
		case R.id.reset_master_password_menu_item:
			new AlertDialog.Builder(this)
		    .setTitle(getString(R.string.master_password_dialog_title))
		    .setMessage(getString(R.string.master_password_dialog_text))
		    .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int which) { 
		            // continue with delete
		        	MasterPasswordManager masterPasswordManager = new MasterPasswordManager(PasswordsActivity.this);
		        	masterPasswordManager.resetMasterPassword();
		        	
		        	// return to the main activity
					Intent intent = new Intent(PasswordsActivity.this, MainActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
				    startActivity(intent);
				    
				    finish();
		        }
		     })
		    .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int which) { 
		            // do nothing
		        }
		     })
		     .show();
			return true;
		default:
			return super.onOptionsItemSelected(item);

		}
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		getMenuInflater().inflate(R.menu.item_contextual_menu , menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {

		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		FrameLayout frameLayout = (FrameLayout) info.targetView;
		
		TextView passwordValueTextView = (TextView) frameLayout.getChildAt(1);

		long passwordId = (Long) passwordValueTextView.getTag();

		PasswordDataSource passwordDataSource = new PasswordDataSource(this);
		
		switch(item.getItemId()){
		case R.id.edit_category_menu_item:
			passwordDataSource.open();
			Password gridItem = passwordDataSource.getPassword(passwordId);
			passwordDataSource.close();
			showPasswordCreationDialog(gridItem.getDescription(), gridItem.getValue(), gridItem.getId(), gridItem.getEncryptionAlgorithm());
			
			break;
		case R.id.delete_category_menu_item:
			passwordDataSource.open();
			passwordDataSource.deletePassword(Long.toString(passwordId), PasswordType.STORED);
			passwordDataSource.close();
			
			GridView gridview = (GridView) findViewById(R.id.passwordsGridView);
			gridview.setAdapter(new PasswordAdapter(this, parentCategoryId));
			
			Toast.makeText(this, getString(R.string.password_deleted_toast), Toast.LENGTH_SHORT).show();
			break;
		default:
			return super.onContextItemSelected(item);
		}

		return true;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		if(notFirstStart){
			Intent intent = new Intent(this, MainActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);			
			finish();
		}
		
		notFirstStart = true;
	}

}
