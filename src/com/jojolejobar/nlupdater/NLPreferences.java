package com.jojolejobar.nlupdater;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class NLPreferences extends PreferenceActivity implements OnSharedPreferenceChangeListener{
	
	private ListPreference listActualisation;
	private EditTextPreference editDownDest;
	
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        
        //Get application version
        Preference preferencesVersion = (Preference) findPreference("preferencesVersion");
        try {
			preferencesVersion.setSummary(getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
		} catch (NameNotFoundException e) {
			preferencesVersion.setSummary("Inconnu");
		}
		
		//Default value
		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
		
		
		//Set summary
		this.listActualisation = (ListPreference) findPreference("listActualisation");
		this.listActualisation.setSummary(this.listActualisation.getEntry());
		editDownDest = (EditTextPreference) findPreference("editDownDest");
		this.editDownDest.setSummary(this.editDownDest.getText());
		
		//Reset the download destination dir
		Preference preferenceDownDest = (Preference) findPreference("preferenceDownloadDestination");
		preferenceDownDest.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference pref) {
				editDownDest.setText("/mnt/sdcard/");
				return false;
			}
		});
		
		
    }

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreference, String key) {
		if(key.equals("listActualisation")){
			this.listActualisation.setSummary(this.listActualisation.getEntry());
		}
		else if(key.equals("editDownDest")){
			this.editDownDest.setSummary(this.editDownDest.getText());
		}
	}

}
