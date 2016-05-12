package com.example.nonameproject;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class ActivityMaster extends Activity{
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	}
	
	protected void displayMessage (String message) {
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}
	
	protected void logMessage(String message){
		Log.d("NONAME", message);
	}
}
