package com.jojolejobar.nlupdater;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class NLJson {
	
	private NLVersion lastVersion;
	
	public NLJson(String URL){
		String mFile = this.getStringPage(URL + "main.json");
		JSONObject jObject = null;
		JSONArray mVersionArray = null;
		double nLastVersion = 0.0;
		int iLastVersion = 0;
		
		lastVersion = null;
		
		try {
			jObject = new JSONObject(mFile);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		try {
			mVersionArray = jObject.getJSONArray("versions");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		for(int i = 0; i < mVersionArray.length(); i++){
			double d = 0;
			try {
				d = Double.valueOf(mVersionArray.getJSONObject(i).getString("version"));
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			if(d > nLastVersion){
				nLastVersion = d;
				iLastVersion = i;
			}
		}
			
		String mUri = null, mVersion = null, mFromVersion[][] = null, mChangelog = null, mFull = null;
		JSONObject jObjectVer = null;
		JSONArray versionArray = null;
		try {
			mVersion = mVersionArray.getJSONObject(iLastVersion).getString("version");
			mChangelog = mVersionArray.getJSONObject(iLastVersion).getString("changelog");
			mUri = mVersionArray.getJSONObject(iLastVersion).getString("uri");
			jObjectVer = new JSONObject(this.getStringPage(URL + mUri + "/mod.json"));
			mFull = jObjectVer.getString("full");
			versionArray = jObjectVer.getJSONArray("fromVersion");
			mFromVersion = new String[2][versionArray.length()];
			for(int j = 0; j < versionArray.length(); j++){
				mFromVersion[0][j] = versionArray.getJSONObject(j).getString("version");
				mFromVersion[1][j] = versionArray.getJSONObject(j).getString("uri");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		if(mVersion != null && mFromVersion != null && mChangelog != null && mFull != null)
			lastVersion = new NLVersion(mVersion, mChangelog, mFull, mFromVersion, mUri);
	}
	
	public NLVersion getLastVersion(){
		return lastVersion;
	}
	
	/**
	 * Function to get the code of the web page
	 * @param url URL of the web page
	 * @return the code of web page
	 * @throws URISyntaxException 
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	public String getStringPage(String url){
    	StringBuffer stringBuffer = new StringBuffer();
    	BufferedReader bufferedReader = null;
    	HttpClient httpClient = null;
    	HttpGet httpGet = null;
    	URI uri = null;
    	HttpResponse httpResponse = null;
    	InputStream inputStream = null;
    	String HTMLCode = null;
    
    	
		//Create client and a query to get the page
    		httpClient = new DefaultHttpClient();
    		httpGet = new HttpGet();
    		
    		//Set the query with the url in parameter
    		try {
				uri = new URI(url);
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
    		httpGet.setURI(uri);
 
    		//Execute the query
    		try {
				httpResponse = httpClient.execute(httpGet);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
 
    		//Create a buffer to get the page
    		try {
				inputStream = httpResponse.getEntity().getContent();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
    		bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
 
    		//Get the buffer caracters
		try {
			HTMLCode = bufferedReader.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		while (HTMLCode!= null){
			stringBuffer.append(HTMLCode);
			stringBuffer.append("\n");
			try {
				HTMLCode = bufferedReader.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
 
    	//Return the string of the page code
    	return stringBuffer.toString();
    }
}
