package com.jojolejobar.nlupdater;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class NLJson {
	
	private JSONObject jObject;
	private JSONArray mVersionArray;
	private String mFile;
	private ArrayList<NLVersionJson> mVersions;
	
	public NLJson(String URL){
		this.mFile = NLHttpClient.getStringPage(URL + "/update.json");
		
		
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
		
		mVersions = new ArrayList<NLVersionJson>();
		
		for(int i = 0; i < mVersionArray.length(); i++){
			String version = null, changelog = null, uri = null, full = null, fromVersion[][] = null;
			JSONObject jObjectVer = null;
			JSONArray versionArray = null;
			try {
				version = mVersionArray.getJSONObject(i).getString("version");
				changelog = mVersionArray.getJSONObject(i).getString("changelog");
				uri = mVersionArray.getJSONObject(i).getString("uri");
				jObjectVer = new JSONObject(NLHttpClient.getStringPage(URL + uri + "/mod.json"));
				full = jObjectVer.getString("full");
				versionArray = jObjectVer.getJSONArray("fromVersion");
				fromVersion = new String[2][versionArray.length()];
				for(int j = 0; j < versionArray.length(); j++){
					fromVersion[0][j] = versionArray.getJSONObject(j).getString("version");
					fromVersion[1][j] = versionArray.getJSONObject(j).getString("uri");
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			mVersions.add(new NLVersionJson(version, changelog, full, fromVersion, uri));
		}
	}

	public ArrayList<NLVersionJson> getVersions(){
		return this.mVersions;
	}
	
}
