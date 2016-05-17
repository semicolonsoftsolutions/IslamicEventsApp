package com.example.nonameproject.util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.example.nonameproject.ActivityNotifications;
import com.example.nonameproject.ApplicationClass;
import com.example.nonameproject.R;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.Log;

public class System {

	private static System INSTANCE;
	private static Context context;
	public static int notificationCount;
	private static final int NOTIFICATION_ID = 7777;
	private static final String IMAGE_DIRECTORY_NAME = "EventsImages";
	private static final int MEDIA_TYPE_IMAGE = 0;

	/**So no one could create direct instance**/
	private System() {notificationCount = 0;}

	public static System getInstance(Context context) {
		if (INSTANCE == null)
			INSTANCE = new System();
		System.context = context;
		return INSTANCE;
	}

	/***
	 * 
	 * @return IMEI number of device
	 */
	public String getDeviceId() {
		TelephonyManager manager = (TelephonyManager) context
				.getSystemService(Activity.TELEPHONY_SERVICE);
		return manager.getDeviceId();
	};

	/**
	 * Displays notification in the notification bar, if notification doesn't exists it
	 * creates and if exists then it updates the event count
	 */
	public void showNotification() {

		NotificationCompat.Builder mBuilder =
		        new NotificationCompat.Builder(context)
		        .setSmallIcon(R.drawable.ic_launcher)
		        .setContentTitle("My notification")
		        .setContentText("Hello World!")
		        .setAutoCancel(true);
		
		mBuilder.setContentText("Hello World").setNumber(++ApplicationClass.mNotificationCount);
		
		// Creates an explicit intent for an Activity in your app
		Intent resultIntent = new Intent(context, ActivityNotifications.class);
		
		resultIntent.putExtra("NOTIFICATION_CLICKED", true);
		// The stack builder object will contain an artificial back stack for the
		// started Activity.
		// This ensures that navigating backward from the Activity leads out of
		// your application to the Home screen.
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
		// Adds the back stack for the Intent (but not the Intent itself)
		stackBuilder.addParentStack(ActivityNotifications.class);
		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent =
		        stackBuilder.getPendingIntent(
		            0,
		            PendingIntent.FLAG_UPDATE_CURRENT
		        );
		mBuilder.setContentIntent(resultPendingIntent);
		NotificationManager mNotificationManager =
		    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		// mId allows you to update the notification later on.
		mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());

	}
	
	public Uri getOutputMediaFileUri(int type) {
		return Uri.fromFile(getOutputMediaFile(type));
	}

	private static File getOutputMediaFile(int type) {

		// External sdcard location
		File mediaStorageDir = new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				IMAGE_DIRECTORY_NAME);

		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create "
						+ IMAGE_DIRECTORY_NAME + " directory");
				return null;
			}
		}

		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
				Locale.getDefault()).format(new Date());
		File mediaFile;
		if (type == MEDIA_TYPE_IMAGE) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator
					+ "IMG_" + timeStamp + ".jpg");
		} else {
			return null;
		}

		return mediaFile;
	}

}
