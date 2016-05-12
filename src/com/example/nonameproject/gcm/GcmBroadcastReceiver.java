package com.example.nonameproject.gcm;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {

	private static String CLASS_NAME = "GcmBroadcastReceive";

	@Override
	public void onReceive(Context context, Intent intent) {
		/***
		 * start intent service from this broadcast receiver
		 * using the intent where the broadcasted message is
		 * form GCM
		 */
		ComponentName comp = new ComponentName(context.getPackageName(),
				GcmMessageHandler.class.getName());
		startWakefulService(context, (intent.setComponent(comp)));
		setResultCode(Activity.RESULT_OK);
	}

}