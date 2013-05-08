package it.mp.claudianiferrari.parserjson;

import java.io.IOException;

import android.util.Log;


public abstract class AbstractParser<T> implements Parser<T> {

	protected String getStringFromUrl(String url){
		String str = null;

		try {
			str = HttpHelper.getTextFileFromUrl(url);
		} catch (IOException e) {
			Log.e("HTTP Helper", "Error while downloading text file: " + e.toString());
		}
		
		return str;
	}
	
}
