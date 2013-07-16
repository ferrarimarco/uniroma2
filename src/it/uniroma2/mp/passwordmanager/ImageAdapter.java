package it.uniroma2.mp.passwordmanager;

import it.uniroma2.mp.passwordmanager.model.GridItem;
import it.uniroma2.mp.passwordmanager.persistance.CategoriesDataSource;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class ImageAdapter extends BaseAdapter {

	private List<GridItem> gridItems;
	private LayoutInflater inflater;
	private String parent;

	public ImageAdapter(Context context, String parent) {

		this.parent = parent;
		
		inflater = LayoutInflater.from(context);

		CategoriesDataSource categoriesDataSource;
		if (!parent.equals(GridItem.CUSTOM_CATEGORY_DRAWABLE_ID)) {
			categoriesDataSource = new CategoriesDataSource(context);
			categoriesDataSource.open();
			gridItems = categoriesDataSource.getAllCategories(parent);
			if (!parent.equals(GridItem.NULL_PARENT_VALUE)) {
				gridItems.add(categoriesDataSource.getEmptyCategory());
			}
			
			categoriesDataSource.close();
		}else{
			gridItems = new ArrayList<GridItem>(10);
			
			GridItem gridItem = new GridItem("Drive", R.drawable.drive, Integer.parseInt(GridItem.CUSTOM_CATEGORY_DRAWABLE_ID));
			gridItems.add(gridItem);
			
			gridItem = new GridItem("ebay", R.drawable.ebay, Integer.parseInt(GridItem.CUSTOM_CATEGORY_DRAWABLE_ID));
			gridItems.add(gridItem);
			
			gridItem = new GridItem("Facebook", R.drawable.facebook, Integer.parseInt(GridItem.CUSTOM_CATEGORY_DRAWABLE_ID));
			gridItems.add(gridItem);
			
			gridItem = new GridItem("Gmail", R.drawable.gmail, Integer.parseInt(GridItem.CUSTOM_CATEGORY_DRAWABLE_ID));
			gridItems.add(gridItem);
			
			gridItem = new GridItem("Hotmail", R.drawable.hotmail, Integer.parseInt(GridItem.CUSTOM_CATEGORY_DRAWABLE_ID));
			gridItems.add(gridItem);
			
			gridItem = new GridItem("Linkedin", R.drawable.linkedin, Integer.parseInt(GridItem.CUSTOM_CATEGORY_DRAWABLE_ID));
			gridItems.add(gridItem);
		}
		
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
	public long getItemId(int i) {
		return gridItems.get(i).getDrawableId();
	}

	@Override
	public View getView(int i, View view, ViewGroup viewGroup) {
		View v = view;
		ImageView picture;
		TextView name;

		if(v == null) {
			int layoutId = -1;
			
			if(!parent.equals(GridItem.CUSTOM_CATEGORY_DRAWABLE_ID)){
				layoutId = R.layout.category_grid_item;
			}else{
				layoutId = R.layout.subcategory_image_chooser_grid_item;
			}
			
			v = inflater.inflate(layoutId, viewGroup, false);
			v.setTag(R.id.category_picture, v.findViewById(R.id.category_picture));
			
			if(!parent.equals(GridItem.CUSTOM_CATEGORY_DRAWABLE_ID)){
				v.setTag(R.id.category_text, v.findViewById(R.id.category_text));
			}
		}
	
		picture = (ImageView) v.getTag(R.id.category_picture);
		
		GridItem gridItem = (GridItem) getItem(i);

		if(!parent.equals(GridItem.CUSTOM_CATEGORY_DRAWABLE_ID)){
			name = (TextView) v.getTag(R.id.category_text);
			name.setText(gridItem.getName());
		}
		
		picture.setImageResource(gridItem.getDrawableId());

		return v;
	}

	public int getGridItemCount(){
		return gridItems.size();
	}

}
