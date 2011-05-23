package com.jojolejobar.nlupdater;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

public class NLUpdater extends Activity{
		
	private static final String URL = "http://beta.nameless-rom.fr/";
	private static final int CODE_PREFERENCES = 0;
	private NLJson mainJson = null;
	private TextView textViewVersion = null;
	private Button buttonUpdate = null, buttonFullVersion = null;	
	private AlertDialog alert;
	private String dir;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);     
        
        /*****************Get widgets**************/
        textViewVersion = (TextView) findViewById(R.id.textViewVersion);
        buttonUpdate = (Button) findViewById(R.id.buttonUpdate);
        buttonFullVersion = (Button) findViewById(R.id.buttonFullVersion);
             
        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	new DownloadFile().execute(URL + NLVersion.getNewVersion(mainJson).getUri() + "/" + NLVersion.getNewFromVersion(mainJson), 
            			dir ,NLVersion.getNewFromVersion(mainJson));
            }
        });
        
        buttonFullVersion.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	new DownloadFile().execute(URL + NLVersion.getNewVersion(mainJson).getUri() + "/" + NLVersion.getNewVersion(mainJson).getFull(), 
                		dir ,NLVersion.getNewVersion(mainJson).getFull());
            }
        });
        
        new DownloadJSON().execute(URL);
        
        this.getPreferences();

    }
    
    public void onJSONDownloaded(){
    	Toast.makeText(NLUpdater.this, getString(R.string.serverCheckSuccess), Toast.LENGTH_SHORT).show();
    	if(NLVersion.newVersion(mainJson)){
        	this.textViewVersion.setText(getString(R.string.newVersion) + " " + NLVersion.getNewVersion(mainJson).getVersion() + "\n\n" +
        			getString(R.string.changelog) + " " + NLVersion.getNewVersion(mainJson).getChangelog());
        	if(NLVersion.getNewFromVersion(mainJson) != null)
        		this.buttonUpdate.setVisibility(View.VISIBLE);
        	else
        		this.buttonUpdate.setVisibility(View.GONE);
        	
        	this.buttonFullVersion.setVisibility(View.VISIBLE);
        }
        else{
        	this.textViewVersion.setText(getString(R.string.noNewVersion) + NLVersion.getCurrentVersion());
        	this.buttonUpdate.setVisibility(View.GONE);
        	this.buttonFullVersion.setVisibility(View.GONE);
        }
    }
    
    
    /*****************Menus**************/
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menuCheck:
        	new DownloadJSON().execute(URL);
            return true;
        case R.id.menuSettings:
        	startActivityForResult(new Intent(this, NLPreferences.class), CODE_PREFERENCES);
        	return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    
    /*****************Menus**************/
    
    public void SDCardAlert(){
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(getString(R.string.noSDCard))
		       .setCancelable(false)
		       .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                NLUpdater.this.finish();
		           }
		       }).setIcon(android.R.drawable.ic_dialog_info).setTitle(getString(R.string.information));
		alert = builder.create();
		alert.show();
    }
    
    public void noDownloadAlert(){
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(getString(R.string.impossibleDownload))
		       .setCancelable(false)
		       .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                NLUpdater.this.finish();
		           }
		       }).setIcon(android.R.drawable.ic_dialog_alert).setTitle(getString(R.string.error));
		alert = builder.create();
		alert.show();
    }
    
    public void downloadSucessAlert(){
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(getString(R.string.successDownload))
		       .setCancelable(false)
		       .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                NLUpdater.this.finish();
		           }
		       }).setIcon(android.R.drawable.ic_dialog_info).setTitle(getString(R.string.information));
		alert = builder.create();
		alert.show();
    }
    
    public void downloadStopAlert(){
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(getString(R.string.stopDownload))
		       .setCancelable(false)
		       .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                NLUpdater.this.finish();
		           }
		       }).setIcon(android.R.drawable.ic_dialog_alert).setTitle(getString(R.string.error));
		alert = builder.create();
		alert.show();
    }
   
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
    	if(requestCode == CODE_PREFERENCES){
    		this.getPreferences();
    	}
    }
    
    public void getPreferences(){
    	SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

		if(preferences.getBoolean("checkBoxActu", true)){
			if(!this.isServiceRunning())
				this.startService(new Intent(this, NLUpdaterService.class));
		}
		else{
			this.stopService(new Intent(this, NLUpdaterService.class));
		}
		
		this.dir = preferences.getString("editDownDest", "/mnt/sdcard/");
    }
   
    public static String getUrl(){
    	return URL;
    }
    
    private boolean isServiceRunning() {
        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<RunningServiceInfo> services = am.getRunningServices(100);        
        for(ActivityManager.RunningServiceInfo rsi:services){     
        	System.out.println(rsi.service.getClassName());
            if(rsi.service.getClassName().equals(this.getPackageName() + ".NLUpdaterService")){   
            	
                return true;
            }
        }
        return false;
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
    
    /**************Update JSON***********/ 
    private class DownloadJSON extends AsyncTask<String, Void, Boolean> {
		private ProgressDialog mProgressDialog;
		
		@Override
		protected void onPreExecute(){
	        mProgressDialog = new ProgressDialog(NLUpdater.this);
			mProgressDialog.setTitle(getString(R.string.wait));
			mProgressDialog.setMessage(getString(R.string.verificationProgress));
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			mProgressDialog.setCancelable(true);
			mProgressDialog.setOnKeyListener(new OnKeyListener() {   
				@Override
				public boolean onKey(DialogInterface arg0, int keyCode, KeyEvent event) {
					if(keyCode == KeyEvent.KEYCODE_BACK){
						NLUpdater.this.finish();
						return true;
					}
					return false;
				}
		    });
			mProgressDialog.show();
			super.onPreExecute();
		}
		
		@Override
		protected Boolean doInBackground(String... url) {
			if(isNetworkAvailable())
				mainJson = new NLJson(url[0]);
			else{
				NLUpdater.this.finish();
				return false;
			}
			return true;  
	    }
	
		@Override
	    protected void onPostExecute(Boolean result) {
	        mProgressDialog.dismiss();
	        if(result)
	        	onJSONDownloaded();
	        super.onPostExecute(result);
	    }
    }
    /**************Update JSON***********/
    
    /**************Download file**********/
    private class DownloadFile extends AsyncTask<String, Integer, Integer>{
    	private ProgressDialog mProgressDialog;

    	@Override
    	protected void onPreExecute(){
    		mProgressDialog = new ProgressDialog(NLUpdater.this);
    		mProgressDialog.setTitle(getString(R.string.wait));
    		mProgressDialog.setMessage(getString(R.string.downloadProgress));
    		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
    		mProgressDialog.setCancelable(true);
    		mProgressDialog.setOnKeyListener(new OnKeyListener() {   
				@Override
				public boolean onKey(DialogInterface arg0, int keyCode, KeyEvent event) {
					if(keyCode == KeyEvent.KEYCODE_BACK){
						cancel(true);
						return true;
					}
					return false;
				}
		    });
    		mProgressDialog.show();
    		super.onPreExecute();
    	}
    	
    	@Override
		protected Integer doInBackground(String... params) {
			String toDownload = params[0];
			if(!params[1].endsWith("/")){
				params[1] += "/";
			}
			String destination = params[1] + params[2];
			
			File dir = new File(params[1]);
			
			if(!dir.exists()){
				dir.mkdirs();
			}

			if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
        		return 1;
        	}

			// initialize the progress dialog
			publishProgress(0);

			try {
				// initialize some timeouts
				HttpParams httpParameters = new BasicHttpParams();
				HttpConnectionParams.setConnectionTimeout(httpParameters,3000);

				// create the connection
				URL url = new URL(toDownload);
				URLConnection connection = url.openConnection();
				HttpURLConnection httpConnection = (HttpURLConnection) connection;

				// connection accepted
				if(httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
					try {
						File file = new File(destination);
						// delete the file if exists
						file.delete();
					} catch (Exception e) {
						// nothing
					}

					int size = connection.getContentLength();

					int index = 0;
					int current = 0;

					try {
						File file = new File(destination);
						file.delete();
						FileOutputStream output = new FileOutputStream(file);
						InputStream input = connection.getInputStream();
						BufferedInputStream buffer = new BufferedInputStream(input);
						byte[] bBuffer = new byte[10240];

						while((current = buffer.read(bBuffer)) != -1) {
							if(isCancelled()){
								file.delete();
								break;
							}
							
							try {
								output.write(bBuffer, 0, current);
							} catch (IOException e) {
								e.printStackTrace();
							}
							index += current;
							publishProgress(index/(size/100));
						}
						output.close();
					} catch (Exception e) {
						e.printStackTrace();
						return 2;
					}

					return 0;
				}

				// connection refused
				return 2;
			} catch (IOException e) {
				return 2;
			}
		}

		protected void onProgressUpdate(Integer... progress) {
            mProgressDialog.setProgress(progress[0]);
		}
		
		@Override
		protected void onPostExecute(Integer result) {
			if(result == 0){
				downloadSucessAlert();
			}
			//SD card not mounted or read only
			else if(result == 1){
				SDCardAlert();
			}
			else if(result == 2){
				noDownloadAlert();
			}
			mProgressDialog.dismiss();
			super.onPostExecute(result);
		}
		
		@Override
		protected void onCancelled (){
			downloadStopAlert();
			mProgressDialog.dismiss();
			super.onCancelled();
		}
    	
    }
    /**************Download file**********/

}