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
import android.os.IBinder;
import android.preference.PreferenceManager;

public class NLUpdaterService extends Service{

	private Timer timer ; 
	private SharedPreferences preferences;
	private static boolean running = false;
	 
	@Override
	public void onCreate() { 
	    super.onCreate(); 
	    timer = new Timer(); 
	    preferences = PreferenceManager.getDefaultSharedPreferences(this);
	    running = true;
	} 
	 
	@Override 
	public int onStartCommand(Intent intent, int flags, int startId) { 
	    timer.scheduleAtFixedRate(new TimerTask() { 
	        public void run() { 
	        	checkServer();
	        } 
	    }, 60000, Long.valueOf(preferences.getString("listUpdate", "86400000"))); 
	 
	    return START_NOT_STICKY; 
	} 
	 
	@Override 
	public void onDestroy() { 
	    this.timer.cancel(); 
	    running = false;
	}
	
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	public void checkServer(){
		NLJson mJson;
		if(NLNetwork.isConnected(this))
			mJson = new NLJson(NLUpdater.getUrl());
		else
			return;		
		
		if(NLGetInfo.hasNewVersion(mJson.getLastVersion())){
			String ticker = getString(R.string.updateAvalaibleNotifTitle);
			String text = getString(R.string.newVersionNotif);
			
			NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
	    	nm.cancel(R.string.app_name);
	    	Notification notification = new Notification(R.drawable.icon , ticker, System.currentTimeMillis());  	
	    	PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, NLUpdater.class), 0);
	    	notification.setLatestEventInfo(this, getString(R.string.app_name), text, contentIntent);
	    	notification.flags |= Notification.FLAG_AUTO_CANCEL;
	    	nm.notify(R.string.app_name, notification);
		}
	}
	
	public static boolean isRunning(){
		return running;
	}

}
