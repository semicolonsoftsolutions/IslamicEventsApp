package com.example.nonameproject.gcm;

import com.example.nonameproject.ApplicationClass;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;


public class GcmMessageHandler extends IntentService{
	public GcmMessageHandler() {
		super("GcmMessageHandler");
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {
		com.google.android.gms.gcm.GoogleCloudMessaging gcm = com.google.android.gms.gcm.GoogleCloudMessaging.getInstance(getApplicationContext());
		/***
		 * Get the message string from intent(passed from GcmBroadcastReceiver)
		 * and send it to another custom broadcast receiver which will then 
		 * show the message into notification area.
		 */
		String message = intent.getExtras().getString("msg");
		Log.d("", "Message is: "+message);
		
		Intent i = new Intent(ApplicationClass.ACTION_HANDLE_MESSAGES);
		i.putExtra(ApplicationClass.KEY_EVENT, message);
		sendBroadcast(i);
		
		GcmBroadcastReceiver.completeWakefulIntent(intent);
	}
}