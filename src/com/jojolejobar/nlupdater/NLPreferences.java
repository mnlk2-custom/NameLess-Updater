package com.jojolejobar.nlupdater;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class NLPreferences extends PreferenceActivity implements OnSharedPreferenceChangeListener{
	private ListPreference listUpdate;
	private EditTextPreference editDownloadDest;
	
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
		
		listUpdate = (ListPreference) findPreference("listUpdate");
		listUpdate.setSummary(listUpdate.getEntry());
		
		editDownloadDest = (EditTextPreference) findPreference("editDownloadDest");
		editDownloadDest.setSummary(editDownloadDest.getText());
		
		Preference preferenceDownloadDest = (Preference) findPreference("preferenceDownloadDest");
		preferenceDownloadDest.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference arg0) {
				editDownloadDest.setText(getString(R.string.defaultDownloadDir));
				return true;
			}
		});
		
	}
	
	@Override
	public void onSharedPreferenceChanged(SharedPreferences arg0, String arg1) {
		listUpdate.setSummary(listUpdate.getEntry());
		if(!editDownloadDest.getText().isEmpty())
			editDownloadDest.setSummary(editDownloadDest.getText());
		else
			editDownloadDest.setText(getString(R.string.defaultDownloadDir));
	}

}
