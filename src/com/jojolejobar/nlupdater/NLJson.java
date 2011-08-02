package com.jojolejobar.nlupdater;

import java.io.Serializable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class NLJson implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private NLVersion lastVersion;
	
	public NLJson(String URL){
		String mFile = NLNetwork.getStringPage(URL + "main.json");
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
			mUri = mVersionArray.getJSONObject(iLastVersion).getString("uri");
			jObjectVer = new JSONObject(NLNetwork.getStringPage(URL + mUri + "/mod.json"));
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
		
		try {
			mChangelog = mVersionArray.getJSONObject(iLastVersion).getString("changelog");
			if(mChangelog.contains("url://")){
				mChangelog = mChangelog.replace("url://", "");
				mChangelog = NLNetwork.getStringPage(URL + "/" + mChangelog);
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
	
}
