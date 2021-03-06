package it.uniroma2.mp.passwordmanager.model;

import it.uniroma2.mp.passwordmanager.R;
import android.content.Context;


/***
 * Questa classe modella le entit� Categoria e Sottocategoria.
 * Le Categorie hanno parent pari a -1
 * Le Sottocategorie hanno un parent pari all'ID della Categoria Genitore
 * name indica il nome della Categoria/Sottocategoria
 * drawableId � l'ID dell'immagine della Categoria/Sottocategoria 
 * id � l'ID della Categoria/Sottocategoria
 * **/

public class GridItem {

    private final String name;
    private final int drawableId;
    private final int parent;
    private String id;
    
    // to store the password
    // when GridItem is used to show a password, not a category
    private String value;
    
    public static final String PARENT_PARAMETER_NAME = "parentParameter";
    public static final String NULL_PARENT_VALUE = "-1";
    public static final String CUSTOM_CATEGORY_DRAWABLE_ID = "-2";
    public static final String DUMMY_CATEGORY_ID = "-1";
    
    public static final String EMAIL_CATEGORY_VALUE = "EMAIL";
    public static final String DEVICES_CATEGORY_VALUE = "DEVICES";
    public static final String BANK_CATEGORY_VALUE = "BANK";
    public static final String WEB_CATEGORY_VALUE = "WEB";
    public static final String OTHER_CATEGORY_VALUE = "OTHER";
    public static final String EMPTY_CATEGORY_VALUE = "EMPTY";

    public GridItem(String name, int drawableId, int parent, String id) {
        this.name = name;
        this.drawableId = drawableId;
        this.parent = parent;
        this.id = id;
    }
    
    /***
	 * Dato il valore ti restituisce la stringa corrispondente dal file delle stringe
	 * **/  
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
    	
    	String result = "";
    	
    	if(descriptionId != -1){
    		result = context.getString(descriptionId);
    	}
    	
    	return result;
    }
    
    /***
	 * Data la stringa ti restituisce il valore corrispondente
	 * **/
    public static String getValueFromDescription(String description, Context context){
    	
    	String result = "";
    	
    	if(description.equals(context.getString(R.string.email_category))){
    		result = EMAIL_CATEGORY_VALUE;
    	}else if(description.equals(context.getString(R.string.devices_category))){
    		result = DEVICES_CATEGORY_VALUE;
    	}else if(description.equals(context.getString(R.string.bank_category))){
    		result = BANK_CATEGORY_VALUE;
    	}else if(description.equals(context.getString(R.string.web_category))){
    		result = WEB_CATEGORY_VALUE;
    	}else if(description.equals(context.getString(R.string.other_category))){
    		result = OTHER_CATEGORY_VALUE;
    	}else if(description.equals(context.getString(R.string.empty_category))){
    		result = EMPTY_CATEGORY_VALUE;
    	}
    	
    	return result;
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

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
}
