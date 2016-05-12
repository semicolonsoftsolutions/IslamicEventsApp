package com.example.nonameproject.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class AppPrefs {
	public static String REG_ID = "registration_id";
	
	private SharedPreferences prefs;
	private Context context;
	
	public AppPrefs(Context context){
		prefs = getPrefs(context);
		this.context = context;
	}
	
	public boolean save(String key,String value){
		Editor editor = prefs.edit();
		editor.putString(key, value);
		return editor.commit();
	}
	
	public String get(String key){
		if (!prefs.contains(key))return null;
		return prefs.getString(key, null);
	}
	
	private static SharedPreferences getPrefs(Context context){
		return PreferenceManager.getDefaultSharedPreferences(context);
	}
}
