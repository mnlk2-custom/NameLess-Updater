package com.jojolejobar.nlupdater;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class NLAlert {
	
	public static void downloadStopAlert(Context context){
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(context.getString(R.string.error));
		builder.setIcon(android.R.drawable.ic_dialog_alert);
		builder.setMessage(R.string.downloadStoped);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int which) {
		    dialog.dismiss();
		    }
		});
		AlertDialog alert = builder.create();
		alert.show();
    }
	
	public static void errorWriteAlert(Context context, String message){
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(context.getString(R.string.error));
		builder.setIcon(android.R.drawable.ic_dialog_alert);
		builder.setMessage(context.getString(R.string.errorWriting) + " (" + message + ")");
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int which) {
		    dialog.dismiss();
		    }
		});
		AlertDialog alert = builder.create();
		alert.show();
    }
	
	public static void downloadSucessAlert(Context context, String message){
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(context.getString(R.string.information));
		builder.setMessage(context.getString(R.string.saveIn) + " : " + message);
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int which) {
		    dialog.dismiss();
		    }
		});
		AlertDialog alert = builder.create();
		alert.show();
    }
	
	public static void noDownloadAlert(Context context){
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(context.getString(R.string.error));
		builder.setIcon(android.R.drawable.ic_dialog_alert);
		builder.setMessage(R.string.errorDownload);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int which) {
		    dialog.dismiss();
		    }
		});
		AlertDialog alert = builder.create();
		alert.show();
    }
}
