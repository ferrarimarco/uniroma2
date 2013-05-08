package it.mp.claudianiferrari.parserjson;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class JSONParser extends AbstractParser<JSONObject>{

	@Override
	public JSONObject parse(String url) {
		
		String json = this.getStringFromUrl(url);

		JSONObject jObj = null;
		
		// try parse the string to a JSON object
		try {
			jObj = new JSONObject(json);
		} catch (JSONException e) {
			Log.e("JSON Parser", "Error parsing data " + e.toString());
		}

		// return JSON String
		return jObj;
	}
}