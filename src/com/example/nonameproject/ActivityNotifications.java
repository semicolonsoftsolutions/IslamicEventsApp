package com.example.nonameproject;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.example.nonameproject.adapter.EventListAdapter;
import com.example.nonameproject.model.Event;
import com.example.nonameproject.util.AppPrefs;
import com.example.nonameproject.util.Database;
import com.example.nonameproject.util.System;

public class ActivityNotifications extends ActivityMaster {

	private static final int CODE_REGISTER_DEVICE = 1;
	private Context context;
	private ListView listEvents;
	private EventListAdapter adapter;
	private List<Event> events;
	private int eventsCount;
	
	/**
	 * If the user hasn't registered with the server then below broadcast
	 * receiver will not be executed because i'm not broadcasting messages if
	 * the user hasn't registered. i.e. from MessageHander.java
	 */
	BroadcastReceiver eventNotificationReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

			logMessage("NOTIFICATION ADDED TO THE LIST");
			Database db = new Database(context);
			// WE HAVE TO CLEAR THE PREVIOUS LIST
			// OTHERWISE IT'LL NOT APPEAR IN THE LIST

			/**
			 * Sanity check on events list, was throwing null pointer
			 */
			if (events == null) {
				events = new ArrayList<Event>();
			}else{
				//logMessage("Events is not null, size is: "+events.size());
				events.clear();
			}
			
			//logMessage("Total events in the database: "+db.getEvents().size());

			events.addAll(db.getEvents());
			adapter.setItems(events);
			// TELL ADAPTER THAT DATA HAS CHANGED
			adapter.notifyDataSetChanged();

			/**
			 * TODO:REMOVE NOTIFICAITONS FROM NOTIFICATION BAR AS USER HAS SEEN
			 * THE EVENT IN THE LIST SO NO NEED TO KEEP NOTIFICATIONS
			 */
			System.getInstance(context).removeAllNotifications();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notifications);
		context = this;
		
		logMessage("onCreate:ActivityNotifications.java");
		
		System.getInstance(context).removeAllNotifications();

		/**
		 * SETTING UP LIST VIEW.IF THE USER HASN'T REGISTERED, THERE IS NO NEED
		 * TO SHOW HIM HIS NOTIFICAITONS. ONCE HE IS REGISTERED WE'LL SHOW HIM
		 * ALL OF HIS NOTIFICAITONS THAT HE MIGHT HAVE MISSED.
		 */
		listEvents = (ListView) findViewById(R.id.listEvents);
	
		Database database = new Database(context);	
		if (events == null){
			events = new ArrayList<Event>();
			eventsCount = events.size();
		}
		
		//AS I SAID, DON'T SHOW IF USER HASN'T REGISTERED
		if (new AppPrefs(context).get(AppPrefs.REG_ID) != null) {
			events = database.getEvents();
		}
		
		adapter = new EventListAdapter(context, R.layout.event_list_item,
				events);
		listEvents.setAdapter(adapter);
		/*******************************ABOVE CODE SHOULD BE EXECUTED BEFORE ALL******************************/
		
		/**
		 * LIST CLICK HANDLER
		 */
		listEvents.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Event event = events.get(position);
				Intent showEvent = new Intent(context,ActivityEvent.class);
				showEvent.putExtra("EXTRA_EVENT", event);
				startActivity(showEvent);
				
			}
		});
		
		/***
		 * Check to see if user opens this activity by clicking on notification
		 * from notification bar, if it's the case then reset notification count
		 * variable in System class.
		 */
		Intent intent = getIntent();
		if (intent.hasExtra("NOTIFICATION_CLICKED")
				&& intent.getBooleanExtra("NOTIFICATION_CLICKED", false)) {
			ApplicationClass.mNotificationCount = 0;
		}

		/**
		 * CHECK IF THE DEVICE HAS REGISTRATION ID, IF IT DOES THEN IT IS
		 * REGISTERED
		 **/
		String regID = new AppPrefs(context).get(AppPrefs.REG_ID);
		if (regID == null) {
			/** TAKE USER TO REGISTER ACTIVITY TO REGISTER DEVICE ON CLOUD **/
			Intent registerActivityIntent = new Intent(context,
					ActivityRegister.class);
			startActivityForResult(registerActivityIntent, CODE_REGISTER_DEVICE);
			return;
		}

		/**
		 * TODO: CHECK IF DEVICE NEED RE REGISTRATION COMPARE THE NEW APP
		 * VERSION WITH CURRENT VERSION TO DETERMINE IF THE USER HAS INSTALLED
		 * AN UPDATE FROM GOOGLE PLAY OR NOT, IF USER HAS UPDATED THE APP FROM
		 * GOOGLE PLAY THEN RE REGISTER DEVICE AND UPDATE ON THE SERVER
		 **/

	}

	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(eventNotificationReceiver);
	}

	@Override
	protected void onResume() {
		super.onResume();
		logMessage("on Resume: ActivityNotifications.java");
		
		registerReceiver(eventNotificationReceiver, new IntentFilter(
				ApplicationClass.ACTION_RELOAD));
		//REFRESH ADAPTER IF ANHYTHING IN THE DATABASE HAS CHANGED/DELETE
		Database database = new Database(context);
		int newCount = database.getEventCount();
		int eventCount = events.size();
		displayMessage(newCount+" and "+eventCount);
		if (newCount < eventCount) {
			displayMessage("something has changed");
			events.clear();
			events.addAll(database.getEvents());
			adapter.setItems(events);
			adapter.notifyDataSetChanged();
		}
		
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case CODE_REGISTER_DEVICE:
			/**
			 * FIRST CHECK THE DATA AND THEN SHOW SUCCESS MESSAGE. IF THE USER
			 * HASN'T REGISTERED AND JUST PRESSED THE BACK BUTTON FROM REGISTER
			 * ACTIVITY, FINISH THIS ACTIVITY TOO.
			 */

			switch (resultCode) {
			case RESULT_OK:
				displayMessage("You are registered!");
				break;
			case RESULT_CANCELED:
				finish();
				break;
			default:
				break;
			}

			break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_notifications, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_new) {

			startActivity(new Intent(context, ActivityEvent.class));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
