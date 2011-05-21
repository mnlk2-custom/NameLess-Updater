package com.jojolejobar.nlupdater;

public class NLVersionJson {
	private String mVersion, mFromVersion[][], mChangelog, mFull, mUri;
	
	public NLVersionJson(String version, String changelog, String full, String fromVersion[][], String uri){
		this.mVersion = version;
		this.mChangelog = changelog;
		this.mFull = full;
		this.mFromVersion = fromVersion;
		this.mUri = uri;
	}

	public String getVersion() {
		return mVersion;
	}

	public String[][] getFromVersion() {
		return mFromVersion;
	}

	public String getChangelog() {
		return mChangelog;
	}

	public String getFull() {
		return mFull;
	}	
	
	public String getUri() {
		return mUri;
	}	
		
}
