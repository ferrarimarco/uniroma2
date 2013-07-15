package it.uniroma2.mp.passwordmanager.model;

import it.uniroma2.mp.passwordmanager.R;
import android.content.Context;

public class Category {

    private final String name;
    private final int drawableId;
    private final int parent;
    
    public static final String PARENT_PARAMETER_NAME = "parentParameter";
    public static final String NULL_PARENT_VALUE = "-1";
    
    public static final String EMAIL_CATEGORY_VALUE = "EMAIL";
    public static final String DEVICES_CATEGORY_VALUE = "DEVICES";
    public static final String BANK_CATEGORY_VALUE = "BANK";
    public static final String WEB_CATEGORY_VALUE = "WEB";
    public static final String OTHER_CATEGORY_VALUE = "OTHER";
    public static final String EMPTY_CATEGORY_VALUE = "EMPTY";

    public Category(String name, int drawableId, int parent) {
        this.name = name;
        this.drawableId = drawableId;
        this.parent = parent;
    }
    
    public static String getDescription(String value, Context context){
    	
    	int descriptionId = -1;
    	
    	if(value.equals(EMAIL_CATEGORY_VALUE)){
    		descriptionId = R.string.email_category;
    	}else if(value.equals(DEVICES_CATEGORY_VALUE)){
    		descriptionId = R.string.devices_category;
    	}else if(value.equals(BANK_CATEGORY_VALUE)){
    		descriptionId = R.string.bank_category;
    	}else if(value.equals(WEB_CATEGORY_VALUE)){
    		descriptionId = R.string.web_category;
    	}else if(value.equals(OTHER_CATEGORY_VALUE)){
    		descriptionId = R.string.other_category;
    	}else if(value.equals(EMPTY_CATEGORY_VALUE)){
    		descriptionId = R.string.empty_category;
    	}
    	
    	return context.getString(descriptionId);
    }
	
	public String getName() {
		return name;
	}
	
	public int getDrawableId() {
		return drawableId;
	}

	public int getParent() {
		return parent;
	}
	
}
