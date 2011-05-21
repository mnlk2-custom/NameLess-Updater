package com.jojolejobar.nlupdater;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.preference.PreferenceManager;

public class NLUpdaterService extends Service{

	private Timer timer ; 
	private SharedPreferences preferences;
	 
	@Override
	public void onCreate() { 
	    super.onCreate(); 
	    timer = new Timer(); 
	    preferences = PreferenceManager.getDefaultSharedPreferences(this);
	} 
	 
	@Override 
	public int onStartCommand(Intent intent, int flags, int startId) { 
	    timer.scheduleAtFixedRate(new TimerTask() { 
	        public void run() { 
	        	notificationVerif();
	        } 
	    }, 0, Long.valueOf(preferences.getString("listActualisation", "86400000"))); 
	 
	    return START_NOT_STICKY; 
	} 
	 
	@Override 
	public void onDestroy() { 
	    this.timer.cancel(); 
	}
	
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	public void notificationVerif(){
		NLJson mainJson;
		if(isNetworkAvailable())
			mainJson = new NLJson(NLUpdater.getUrl());
		else
			return;		
		
		if(NLVersion.newVersion(mainJson)){
			String ticker = "Une nouvelle version de NameLess est disponible";
			String text = "Nouvelle version : " + NLVersion.getNewVersion(mainJson).getVersion();
			
			NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
	    	nm.cancel(R.string.app_name);
	    	Notification notification = new Notification(R.drawable.icon , ticker, System.currentTimeMillis());  	
	    	PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, NLUpdater.class), 0);
	    	notification.setLatestEventInfo(this, getString(R.string.app_name), text, contentIntent);
	    	notification.flags |= Notification.FLAG_AUTO_CANCEL;
	    	nm.notify(R.string.app_name, notification);
		}
	}
	
	public boolean isNetworkAvailable() {
    	ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni!=null && ni.isAvailable() && ni.isConnected()) {
            return true;
        } else {
            return false; 
        }
	}

}
