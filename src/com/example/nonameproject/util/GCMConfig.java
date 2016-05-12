package com.example.nonameproject.util;

import com.example.nonameproject.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class GCMConfig {
	public static String REG_ID = "gcm_registration_id"; 
	
	public static String senderID(Context context){
		return context.getString(R.string.SENDER_ID);
	}
	
}
