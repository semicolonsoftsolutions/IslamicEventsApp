package com.example.nonameproject.util;

import org.json.JSONException;
import org.json.JSONObject;

public class Json {
	private String jsonString;
	private JSONObject jsonObject;
	
	
	public Json(String newString) throws JSONException{
		
		this.jsonString = newString;
		jsonObject = new JSONObject(this.jsonString);
		
	}
	
	public String getAttribute(String key) throws JSONException{
		return jsonObject.getString(key)+" ";//Because we also created json so we know it's an integer
	}
	
}