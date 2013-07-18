package it.uniroma2.mp.passwordmanager;

import it.uniroma2.mp.passwordmanager.model.Password;
import it.uniroma2.mp.passwordmanager.model.PasswordType;
import it.uniroma2.mp.passwordmanager.persistance.PasswordDataSource;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


public class PasswordAdapter extends BaseAdapter {

	private List<Password> gridItems;
	private LayoutInflater inflater;

	public PasswordAdapter(Context context, String parent) {

		inflater = LayoutInflater.from(context);

		PasswordDataSource passwordDataSource = new PasswordDataSource(context);
		passwordDataSource.open();
		gridItems = passwordDataSource.getAllPasswords(PasswordType.STORED, parent);
		
		if(gridItems.size() == 0){
			Password emptyPlaceholder = new Password();
			emptyPlaceholder.setDescription(context.getString(R.string.no_passwords_in_category));
			gridItems.add(emptyPlaceholder);
		}
		
		passwordDataSource.close();
	}

	@Override
	public int getCount() {
		return gridItems.size();
	}

	@Override
	public Object getItem(int i) {
		return gridItems.get(i);
	}

	@Override
	public View getView(int i, View view, ViewGroup viewGroup) {
		View v = view;
		TextView description;
		TextView value;

		Password gridItem = (Password) getItem(i);
		
		if(v == null) {
			v = inflater.inflate(R.layout.password_grid_item, viewGroup, false);
			v.setTag(R.id.description_password_textView, v.findViewById(R.id.description_password_textView));
			v.setTag(R.id.value_password_textView, v.findViewById(R.id.value_password_textView));
			v.setEnabled(false);
		}
		
		description = (TextView) v.getTag(R.id.description_password_textView);
		description.setText(gridItem.getDescription());
		
		value = (TextView) v.getTag(R.id.value_password_textView);
		value.setText(gridItem.getValue());
		value.setTag(gridItem.getId());

		return v;
	}

	public int getGridItemCount(){
		return gridItems.size();
	}

	@Override
	public long getItemId(int position) {
		return gridItems.get(position).getId();
	}

}
