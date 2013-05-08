package it.mp.claudianiferrari.parserjson;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class DownloadJsonObjectTask extends AsyncTask<String, Void, JSONObject> {
	
	// JSON Node names
	private static final String TAG_CONTACTS = "contacts";
	private static final String TAG_ID = "id";
	private static final String TAG_NAME = "name";
	private static final String TAG_EMAIL = "email";
	private static final String TAG_ADDRESS = "address";
	private static final String TAG_GENDER = "gender";
	private static final String TAG_PHONE = "phone";
	private static final String TAG_PHONE_MOBILE = "mobile";
	private static final String TAG_PHONE_HOME = "home";
	private static final String TAG_PHONE_OFFICE = "office";

	// contacts JSONArray
	private JSONArray contacts;
	
	private Activity activityContext;
	
	public DownloadJsonObjectTask(Activity activityContext){
		super();
		
		this.activityContext = activityContext;
	}
	
	@Override
	protected JSONObject doInBackground(String... args) {

		String url = null;

		JSONObject json = null;

		if(args.length == 1){
			url = args[0];

			// Creating JSON Parser instance
			Parser<JSONObject> jsonParser = new JSONParser();

			// getting JSON string from URL
			json = jsonParser.parse(url);
		}

		return json;
	}

	@Override
	protected void onPostExecute(JSONObject json) {

		Log.d("JSON Data: ", json.toString());
		
		// Hashmap for ListView
		List<Contact> contactList = new ArrayList<Contact>();

		try {
			// Getting Array of Contacts
			contacts = json.getJSONArray(TAG_CONTACTS);

			// looping through All Contacts
			for(int i = 0; i < contacts.length(); i++){
				JSONObject c = contacts.getJSONObject(i);

				// Storing each json item in variable
				String id = c.getString(TAG_ID);
				String name = c.getString(TAG_NAME);
				String email = c.getString(TAG_EMAIL);
				String address = c.getString(TAG_ADDRESS);
				String gender = c.getString(TAG_GENDER);

				// Phone number is again JSON Object
				JSONObject phone = c.getJSONObject(TAG_PHONE);
				String mobile = phone.getString(TAG_PHONE_MOBILE);
				String home = phone.getString(TAG_PHONE_HOME);
				String office = phone.getString(TAG_PHONE_OFFICE);
				
				contactList.add(new Contact(id, name, email, address, gender, mobile, home, office));				
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		ArrayList<String> contactsListView = new ArrayList<String>();
		
		for(int i = 0; i < contactList.size(); i++){
			contactsListView.add(contactList.get(i).toString());
		}
		
		ListView listView = (ListView) activityContext.findViewById(R.id.ListViewContatti);
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(activityContext, android.R.layout.simple_list_item_1, contactsListView);
		listView.setAdapter(arrayAdapter);		
	}
}
