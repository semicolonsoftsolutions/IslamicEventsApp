package com.example.nonameproject.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.example.nonameproject.model.Event;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.util.Log;

import com.example.nonameproject.ApplicationClass;

public class ServerUtil {
	public ServerUtil (){
		
	}
	
	public String submitEvent(String eventTitle,String eventCat,String eventAddress,
							String eventDate, String eventDesc,String image,String city) throws Exception{
		
		//CREATE PARAMETERS TO SENT TO PHP
		List<NameValuePair> params = new ParamBuilder().addParam("event_title", eventTitle)
		.addParam("event_cat", eventCat)
		.addParam("event_address", eventAddress)
		.addParam("event_date", eventDate)
		.addParam("event_desc", eventDesc)
		.addParam("event_city", city)
		.addParam("event_image", image).build();
		
		
		String response = executeRequest("http://testingapps.netai.net/submit.php", params);
		
		return response;
		
	}
	public String registerUser(String deviceID , String phoneNumber, String gcmRegID, String name)throws Exception{
		
		//CREATE PARAMETERS TO SEND TO PHP
		/***
		 * Self complement
		 * Nice work dude!!!!!
		 */
		List<NameValuePair> params = new ParamBuilder().addParam("user_device_id", deviceID)
		.addParam("user_phone_no", phoneNumber)
		.addParam("gcm_reg_id", gcmRegID)
		.addParam("name", name).build();
		
		String response = executeRequest(ApplicationClass.URL_REG_USER, params);
		
		return response;
		
	}
	
	private String executeRequest(String url, List<NameValuePair> params) throws Exception{
		
		//CLIENT TO EXECUTE REQUESTS
		HttpClient client = new DefaultHttpClient();
		
		//HTTP REQUEST WITH PARAMETERS
		HttpPost postRequest = new HttpPost(url);
		postRequest.setEntity(new UrlEncodedFormEntity(params));
		
		//RESPONSE
		HttpResponse response = client.execute(postRequest);
		
		//GET RESPONSE INPUT STREAM AND READ THE RESPONSE
		InputStream is = response.getEntity().getContent();
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is));
		StringBuilder stringBuilder = new StringBuilder();
		String line = null;
		while ((line = bufferedReader.readLine()) != null) {
			stringBuilder.append(line);
		}
		is.close();
		
		return stringBuilder.toString();
		
	}
}
