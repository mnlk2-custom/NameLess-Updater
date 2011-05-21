package com.jojolejobar.nlupdater;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

public class OnBootReceiver extends BroadcastReceiver {

    public static final String TAG = "TestApp";

    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent().setComponent(new ComponentName(
                context.getPackageName(), NLUpdaterService.class.getName())));
    }
}
