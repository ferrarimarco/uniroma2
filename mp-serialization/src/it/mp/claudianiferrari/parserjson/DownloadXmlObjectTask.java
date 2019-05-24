package it.mp.claudianiferrari.parserjson;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class DownloadXmlObjectTask extends AsyncTask<String, Void, List<Entry>>  {
	
	private Activity activityContext;
	
	public DownloadXmlObjectTask(Activity activityContext){
		super();
		
		this.activityContext = activityContext;
	}
	
	@Override
	protected List<Entry> doInBackground(String... params) {
		// TODO Auto-generated method stub
		
		String url = null;
		
		if(params.length == 1){
			url = params[0];
		}
		
		Parser<List<Entry>> xmlEntryParser = new XMLEntryParser();
		
		return xmlEntryParser.parse(url);
	}

	@Override
	protected void onPostExecute(List<Entry> entries) {
		
		List<String> entriesView = new ArrayList<String>();

		Serializer serializer = new Persister();
		
		// looping through All Contacts
		for(int i = 0; i < entries.size(); i++){
			entriesView.add(entries.get(i).toString());
			
			
			if (i == 0) {
				Entry entry = new Entry(entries.get(i).getTitle(), entries.get(i).getSummary(), entries.get(i).getLink());
				File result = new File(activityContext.getFilesDir(), i + ".xml");
				try {
					serializer.write(entry, result);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		ListView listView = (ListView) activityContext.findViewById(R.id.ListViewContatti);
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(activityContext, android.R.layout.simple_list_item_1, entriesView);
		listView.setAdapter(arrayAdapter);		
	}
}
