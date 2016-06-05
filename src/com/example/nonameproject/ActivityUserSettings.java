package com.example.nonameproject;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class ActivityUserSettings extends PreferenceActivity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.user_settings);
	}
}
