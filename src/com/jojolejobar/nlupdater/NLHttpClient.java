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

public class NLHttpClient {
	
	/**
	 * Function to get the code of the web page
	 * @param url URL of the web page
	 * @return the code of web page
	 * @throws URISyntaxException 
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	public static String getStringPage(String url){
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
