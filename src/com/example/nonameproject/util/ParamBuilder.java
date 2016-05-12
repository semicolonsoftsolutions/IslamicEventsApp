package com.example.nonameproject.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

public class ParamBuilder {
	
	public List<NameValuePair> bParams;
	
	public ParamBuilder addParam(String key, String value){
		if (bParams == null) {
			bParams = new ArrayList<NameValuePair>();
		}
		
		NameValuePair param = new BasicNameValuePair(key,value);
		bParams.add(param);
		
		return this;
	}
	
	public List<NameValuePair> build(){
		return bParams;
	}
	
	
}
