package com.jojolejobar.nlupdater;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class NLVersion {
	
    public static String getCurrentVersion(){
    	String version = null;
        Process proc = null;
        BufferedReader buf = null;
        try {
	        proc = Runtime.getRuntime().exec("getprop ro.modversion");
	        buf = new BufferedReader(new InputStreamReader(proc.getInputStream()));
	        version = buf.readLine();
        } catch (java.io.IOException e) {
        	
        }
        proc.destroy();
        
        return version/*"NameLess-v.1.2"*/;
    }
    
    public static boolean newVersion(NLJson mainJson){
    	double currentVersion = Double.valueOf(getCurrentVersion().replace("NameLess-v.", "")), lastVersion = 0.0;
    	for(int i = 0; i < mainJson.getVersions().size(); i++){
    		if(Double.valueOf(mainJson.getVersions().get(i).getVersion()) > lastVersion)
    			lastVersion = Double.valueOf(mainJson.getVersions().get(i).getVersion());
    	}
    	if(lastVersion > currentVersion)
    		return true;
    	else
    		return false;
    }
    
    public static NLVersionJson getNewVersion(NLJson mainJson){
    	double lastVersion = 0.0;
    	int intVersion = 0;
    	if(!newVersion(mainJson))
    		return null;
    	
    	for(int i = 0; i < mainJson.getVersions().size(); i++){
    		if(Double.valueOf(mainJson.getVersions().get(i).getVersion()) > lastVersion){
    			lastVersion = Double.valueOf(mainJson.getVersions().get(i).getVersion());
    			intVersion = i;
    		}
    	}
    	
    	return mainJson.getVersions().get(intVersion);
    }
    
    public static String getNewFromVersion(NLJson mainJson) {
    	NLVersionJson newVersion = getNewVersion(mainJson);
    	for(int i = 0; i < newVersion.getFromVersion()[0].length; i++){
    		if(Double.valueOf(newVersion.getFromVersion()[0][i]).equals(Double.valueOf(getCurrentVersion().replace("NameLess-v.", "")))){
    			return newVersion.getFromVersion()[1][i];
    		}
    	}
    	return null;
	}
   
}
