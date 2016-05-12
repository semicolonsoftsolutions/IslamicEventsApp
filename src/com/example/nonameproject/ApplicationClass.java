package com.example.nonameproject;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

public class ApplicationClass extends Application{
	
	public static int mNotificationCount = 0;
	public static final String ACTION_HANDLE_MESSAGES = "com.example.nonameproject.HANDLE_MESSAGE";
	public static final String ACTION_RELOAD = "com.example.nonameproject.RELOAD";
	public static final String KEY_EVENT = "EVENT_ID";
    public static final String URL_REG_USER   =  "http:/UB/testingapps.netai.net/manage_user.php";
	public static final String URL_GET_EVENT  =  "http://testingapps.netai.net/get_event.php";
	
}
