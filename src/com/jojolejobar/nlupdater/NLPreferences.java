package com.jojolejobar.nlupdater;

import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class NLPreferences extends PreferenceActivity{
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        
        Preference preferencesVersion = (Preference) findPreference("preferencesVersion");
        try {
			preferencesVersion.setSummary(getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
		} catch (NameNotFoundException e) {
			preferencesVersion.setSummary("Inconnu");
		}
				
		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
    }

}
