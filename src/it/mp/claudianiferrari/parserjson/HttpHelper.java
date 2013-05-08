package it.mp.claudianiferrari.parserjson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

public class HttpHelper {

	public static String getTextFileFromUrl(String url) throws IOException {

		InputStream is = null;
		
		// Making HTTP request
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(url);

		HttpResponse httpResponse = httpClient.execute(httpPost);
		HttpEntity httpEntity = httpResponse.getEntity();
		is = httpEntity.getContent();

		String result = "";
		StringBuilder sb = new StringBuilder();
		
		if(is != null){
			BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);

			String line = null;

			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}			

			result = sb.toString();

			is.close();
		}

		return result;

	}
}
