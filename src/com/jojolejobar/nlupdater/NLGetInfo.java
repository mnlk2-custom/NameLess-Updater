package com.jojolejobar.nlupdater;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class NLGetInfo {
	
	public static boolean hasNewVersion(NLVersion lastVersion){
		if(Double.valueOf(lastVersion.getVersion()) > Double.valueOf(getCurrentVersion().replace("NameLess-v.", "")))
			return true;
		return false;
	}
	
	/**
	 * Get the current version of system
	 * @return the current version (NameLess-vx.y)
	 */
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
        
        if(version == null || version.isEmpty())
        	version = "NameLess-v.0.0";
        
        return version;
    }
	
	public static String getFromVersionURL(NLVersion version){
		for(int i = 0; i < version.getFromVersion()[0].length; i++){
    		if(Double.valueOf(version.getFromVersion()[0][i]).equals(Double.valueOf(getCurrentVersion().replace("NameLess-v.", "")))){
    			return version.getFromVersion()[1][i];
    		}
    	}
    	return null;
	}
}
