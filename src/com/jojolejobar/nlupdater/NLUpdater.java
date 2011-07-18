package com.jojolejobar.nlupdater;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class NLUpdater extends Activity {
	
	private TextView tvChangelog;
	private Button buttonUpdate, buttonFull;
	private static final String URL = "http://beta.nameless-rom.fr/";

	private NLJson mJson;
	private String destinationDir = null;
	private SharedPreferences preferences;
	
    /********************* Activity Management **************************/
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        tvChangelog = (TextView) findViewById(R.id.tvChangelog);
        buttonUpdate = (Button) findViewById(R.id.buttonUpdate);
        buttonUpdate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if(mJson != null && NLGetInfo.getFromVersionURL(mJson.getLastVersion()) != null)
					new DownloadFile().execute(URL + mJson.getLastVersion().getUri() + "/" + NLGetInfo.getFromVersionURL(mJson.getLastVersion())
							, destinationDir
							, NLGetInfo.getFromVersionURL(mJson.getLastVersion()));
			}
		});
        buttonFull = (Button) findViewById(R.id.buttonFull);
        buttonFull.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View arg0) {
				if(mJson != null)
					new DownloadFile().execute(URL + mJson.getLastVersion().getUri() + "/" + mJson.getLastVersion().getFull(), destinationDir
							, mJson.getLastVersion().getFull());
			}
		});
        
        getPreferences();
        
        new DownloadJSON().execute(URL);
     
        if(!NLUpdaterService.isRunning() && preferences.getBoolean("checkBoxUpdate", false))
        	startService(new Intent().setComponent(new ComponentName(getPackageName(), NLUpdaterService.class.getName())));
    }
    
    @Override
    protected void onResume(){
    	super.onResume();
    	getPreferences();
    }
    
    /*********************** Menu ***************************************/
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menuUpdateJSON:
        	new DownloadJSON().execute(URL);
            return true;
        case R.id.menuSettings:
        	startActivity(new Intent(this, NLPreferences.class));
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    
    /************************ Network access **************************/
    /**
     * Check the network state
     * @return true if the phone is connected
     */
    private boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            return true;
        }
        return false;
    }
    
    /**
     * Class to check the server and download the JSON file
     * @author Guillaume
     *
     */
    private class DownloadJSON extends AsyncTask<String, Void, Boolean> {
		private ProgressDialog mProgressDialog;
		
		@Override
		protected void onPreExecute(){
	        mProgressDialog = new ProgressDialog(NLUpdater.this);
			mProgressDialog.setTitle(getString(R.string.wait));
			mProgressDialog.setMessage(getString(R.string.verificationProgress));
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			mProgressDialog.setCancelable(true);
			mProgressDialog.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface arg0) {
					NLUpdater.this.finish();			
				}   
		    });
			mProgressDialog.show();
			super.onPreExecute();
		}
		
		@Override
		protected Boolean doInBackground(String... url) {
			if(isConnected())
				mJson = new NLJson(url[0]);
			else{
				return false;
			}
			return true;  
	    }
	
		@Override
	    protected void onPostExecute(Boolean result) {
	        mProgressDialog.dismiss();
	        if(result && mJson != null)
	        	onJSONDownloaded();
	        else{
	        	tvChangelog.setText(R.string.errorCheck);
	        	Toast.makeText(NLUpdater.this, getString(R.string.noConnection), Toast.LENGTH_LONG).show();
	        }
	        super.onPostExecute(result);
	    }
    }

    /**
     * Action when the JSON was downloaded
     */
    private void onJSONDownloaded(){
    	if(mJson == null){
    		tvChangelog.setText(R.string.errorCheck);
        	Toast.makeText(NLUpdater.this, getString(R.string.noConnection), Toast.LENGTH_LONG).show();
        	return;
    	}
    	if(NLGetInfo.hasNewVersion(mJson.getLastVersion())){
    		tvChangelog.setText(getString(R.string.newVersionAvailable) + " : " + mJson.getLastVersion().getVersion() + "\n" +
    				getString(R.string.changelog) + " : " + mJson.getLastVersion().getChangelog());
    		buttonFull.setVisibility(View.VISIBLE);
    		if(NLGetInfo.getFromVersionURL(mJson.getLastVersion()) != null){
    			buttonUpdate.setVisibility(View.VISIBLE);
    		}
    	}
    	else{
    		tvChangelog.setText(R.string.anyUpdate);
    		buttonFull.setVisibility(View.GONE);
    		buttonUpdate.setVisibility(View.GONE);
    	}
    }
    
    /**
     * Class to download file
     * @author Guillaume
     *
     */
    private class DownloadFile extends AsyncTask<String, Integer, Integer>{
    	private ProgressDialog mProgressDialog;
    	private String destination;

    	@Override
    	protected void onPreExecute(){
    		mProgressDialog = new ProgressDialog(NLUpdater.this);
    		mProgressDialog.setTitle(getString(R.string.wait));
    		mProgressDialog.setMessage(getString(R.string.downloadProgress));
    		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
    		mProgressDialog.setCancelable(true);
    		mProgressDialog.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface arg0) {
					cancel(true);
				}   		
		    });
    		mProgressDialog.show();
    		super.onPreExecute();
    	}
    	
    	@Override
		protected Integer doInBackground(String... params) {
    		if(!isConnected())
    			return 2;
			String toDownload = params[0];
			if(!params[1].endsWith("/")){
				params[1] += "/";
			}
			destination = params[1] + params[2];
			
			File dir = new File(params[1]);
			
			if(!dir.exists()){
				try{
					dir.mkdirs();
				} catch(SecurityException se){
					se.printStackTrace();
					return 1;
				}
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
					} catch(SecurityException se){
						se.printStackTrace();
						return 1;
					} catch(FileNotFoundException e){
						e.printStackTrace();
						return 1;
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
				NLAlert.downloadSucessAlert(NLUpdater.this, destination);
			}
			else if(result == 1){
				NLAlert.errorWriteAlert(NLUpdater.this, destination);
			}
			else if(result == 2){
				NLAlert.downloadStopAlert(NLUpdater.this);
			}
			mProgressDialog.dismiss();
			super.onPostExecute(result);
		}
		
		@Override
		protected void onCancelled (){
			NLAlert.noDownloadAlert(NLUpdater.this);
			mProgressDialog.dismiss();
			super.onCancelled();
		}
    	
    }

    /************************ Preferences *******************************/
    public void getPreferences(){
    	preferences = PreferenceManager.getDefaultSharedPreferences(this);
    	
    	if(preferences.getBoolean("checkBoxUpdate", false)){
			if(!NLUpdaterService.isRunning())
				startService(new Intent().setComponent(new ComponentName(getPackageName(), NLUpdaterService.class.getName())));
		}
		else{
				stopService(new Intent().setComponent(new ComponentName(getPackageName(), NLUpdaterService.class.getName())));
		}
    	
		destinationDir = preferences.getString("editDownloadDest", getString(R.string.defaultDownloadDir));
    }
    
    public static String getUrl() {
		return URL;
	}
}