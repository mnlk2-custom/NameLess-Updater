package com.jojolejobar.nlupdater;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;

public class OnBootReceiver extends BroadcastReceiver {

	@Override
    public void onReceive(Context context, Intent intent) {
		if(PreferenceManager.getDefaultSharedPreferences(context).getBoolean("checkBoxUpdate", false))
	        context.startService(new Intent().setComponent(new ComponentName(context.getPackageName(), NLUpdaterService.class.getName())));
    }

}
