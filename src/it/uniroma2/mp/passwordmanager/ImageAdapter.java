package it.uniroma2.mp.passwordmanager;

import it.uniroma2.mp.passwordmanager.model.Category;
import it.uniroma2.mp.passwordmanager.persistance.CategoriesDataSource;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class ImageAdapter extends BaseAdapter {

	private List<Category> categories;
	private LayoutInflater inflater;

	public ImageAdapter(Context context, String parent) {

		inflater = LayoutInflater.from(context);

		CategoriesDataSource categoriesDataSource = new CategoriesDataSource(context);
		categoriesDataSource.open();

		categories = categoriesDataSource.getAllCategories(parent);
		
		if(categories.size() == 0){
			categories.add(categoriesDataSource.getEmptyCategory());
		}
		
		categoriesDataSource.close();
	}

	@Override
	public int getCount() {
		return categories.size();
	}

	@Override
	public Object getItem(int i) {
		return categories.get(i);
	}

	@Override
	public long getItemId(int i) {
		return categories.get(i).getDrawableId();
	}

	@Override
	public View getView(int i, View view, ViewGroup viewGroup) {
		View v = view;
		ImageView picture;
		TextView name;

		if(v == null) {
			v = inflater.inflate(R.layout.category_grid_item, viewGroup, false);
			v.setTag(R.id.category_picture, v.findViewById(R.id.category_picture));
			v.setTag(R.id.category_text, v.findViewById(R.id.category_text));
		}

		picture = (ImageView) v.getTag(R.id.category_picture);
		name = (TextView) v.getTag(R.id.category_text);

		Category category = (Category) getItem(i);

		picture.setImageResource(category.getDrawableId());
		name.setText(category.getName());

		return v;
	}


}
