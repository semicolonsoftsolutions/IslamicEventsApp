package com.example.nonameproject.gcm;

import org.json.JSONException;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.nonameproject.ApplicationClass;
import com.example.nonameproject.model.Event;
import com.example.nonameproject.util.AppPrefs;
import com.example.nonameproject.util.Database;
import com.example.nonameproject.util.Json;
import com.example.nonameproject.util.ServerUtil;
import com.example.nonameproject.util.System;

public class MessageHandler extends BroadcastReceiver {

	private String event;

	@Override
	public void onReceive(Context context, Intent intent) {
		String eventJson = intent.getStringExtra(ApplicationClass.KEY_EVENT);

		// CREATE EVENT FROM JSON
		Event e = null;
		try {
			e = Event.createFromJson(eventJson);
		} catch (Exception ex) {
			Log.d("NONAME", ex.getMessage());
			ex.printStackTrace();
		}

		// SAVE EVENT IN LOCAL DATABASE
		Database db = new Database(context);
		db.saveEvent(e);

		/**
		 * BEFORE SHOWING NOTIFICATION FIRST CHECK IF THE USER HAS
		 * REGISTERED WITH THE SERVER. IF NOT THEN STORE THE EVENT INTO THE
		 * DATABASE BUT DON'T SHOW NOTIFICATION.
		 */

		if (new AppPrefs(context).get(AppPrefs.REG_ID) == null) {
			return;
		}

		/*
		 * TODO: SHOW EVENT IN NOTIFICATION CURRENTLY DUMMY NOTIFICAITON IS
		 * BEING SHOWN
		 */
		System.getInstance(context).showNotification();

		/********
		 * SEND BROADCAST TO THE RECEIVER REGISTER IN NOTIFICATION ACTIVITY THAT
		 * WILL UPDATE THE EVENT LIST THERE AND REMOVE THE NOTIFICAION.
		 */

		Intent i = new Intent(ApplicationClass.ACTION_RELOAD);
		context.sendBroadcast(i);
	}

}
