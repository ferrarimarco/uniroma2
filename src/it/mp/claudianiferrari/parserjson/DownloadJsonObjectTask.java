package it.mp.claudianiferrari.parserjson;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class DownloadJsonObjectTask extends AsyncTask<String, Void, List<Contact>> {
	

	private Activity activityContext;
	
	public DownloadJsonObjectTask(Activity activityContext){
		super();
		
		this.activityContext = activityContext;
	}
	
	@Override
	protected List<Contact> doInBackground(String... args) {

		String url = null;

		if(args.length == 1){
			url = args[0];
		}

		// Creating JSON Parser instance
		Parser<List<Contact>> contacts  = new JSONParser();

		// getting JSON string from URL
		return  contacts.parse(url);
		

		
	}
	
	@Override
	protected void onPostExecute(List<Contact> contactList) {
		
		ObjectMapper mapper = new ObjectMapper();
		
		ArrayList<String> contactsListView = new ArrayList<String>();
		
		for(int i = 0; i < contactList.size(); i++){
			contactsListView.add(contactList.get(i).toString());
			
			File result = new File(activityContext.getFilesDir(), i + ".json");
			try {
				mapper.writeValue(result, contactList.get(i));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	
		}
		
		ListView listView = (ListView) activityContext.findViewById(R.id.ListViewContatti);
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(activityContext, android.R.layout.simple_list_item_1, contactsListView);
		listView.setAdapter(arrayAdapter);		
	}
}
