package com.example.nonameproject.model;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.example.nonameproject.util.Json;

public class Event implements Parcelable {
	private int event_id;
	private String event_desc = "";
	
	/*
	 * General purpose use columns. 
	 * I'll map these attributes to event model attributes. 
	 */
	private String attribute1 ="";
	private String attribute2 = "";
	private String attribute3 ="";
	private String attribute4 = "";
	private String attribute5 = "";
	private String attribute6 = "";
	private String attribute7 = "";
	private String attribute8 = "";
	private String attribute9 = "";
	private String attribute10 = "";
	
	/*Default constructor*/
	public Event(){}
	
	public Event(Parcel in){
		event_id = in.readInt();
		event_desc = in.readString();
		attribute1 = in.readString();
		attribute2 = in.readString();
		attribute3 = in.readString();
		attribute4 = in.readString();
		attribute5 = in.readString();
		attribute6 = in.readString();
		attribute7 = in.readString();
		attribute8 = in.readString();
		attribute9 = in.readString();
		attribute10 = in.readString();
	}
	public static Event createFromJson(String json)throws Exception{
		
		Event event = new Event();
		Json jsonParser = new Json(json);
		
		Log.d("NONAME", json);
		
		event.setEvent_id(Integer.parseInt(jsonParser.getAttribute("event_id").trim()));
		event.setEvent_desc(jsonParser.getAttribute("description"));
		event.setAttribute1(jsonParser.getAttribute("attribute1"));
		event.setAttribute2(jsonParser.getAttribute("attribute2"));
		event.setAttribute3(jsonParser.getAttribute("attribute3"));
		event.setAttribute4(jsonParser.getAttribute("attribute4"));
		event.setAttribute5(jsonParser.getAttribute("attribute5"));
		event.setAttribute6(jsonParser.getAttribute("attribute6"));
		event.setAttribute7(jsonParser.getAttribute("attribute7"));
		event.setAttribute8(jsonParser.getAttribute("attribute8"));
		/**ATTRIBUTE9 AND ATTRIBUTE10 ARE NOT INCLUDED IN THE JSON FROM SERVER
		 * ADD IT INTO THE RESPONSE AND UNCOMMENT THE BELOW COMMENT*/
		//event.setAttribute9(jsonParser.getAttribute("attribute9"));
		//event.setAttribute10(jsonParser.getAttribute("attribute10"));
		
		return event;
	}
	
	public static List<Event> createFromCursor(Cursor c){
		
		List<Event> list = new ArrayList<Event>();
		
		while (c.moveToNext()) {
			Event e = new Event();
			e.setEvent_id(c.getInt(c.getColumnIndex("event_id")));
			e.setEvent_desc(c.getString(c.getColumnIndex("event_desc")));
			e.setAttribute1(c.getString(c.getColumnIndex("attribute1")));
			e.setAttribute2(c.getString(c.getColumnIndex("attribute2")));
			e.setAttribute3(c.getString(c.getColumnIndex("attribute3")));
			e.setAttribute4(c.getString(c.getColumnIndex("attribute4")));
			e.setAttribute5(c.getString(c.getColumnIndex("attribute5")));
			e.setAttribute6(c.getString(c.getColumnIndex("attribute6")));
			e.setAttribute7(c.getString(c.getColumnIndex("attribute7")));
			e.setAttribute8(c.getString(c.getColumnIndex("attribute8")));
			e.setAttribute9(c.getString(c.getColumnIndex("attribute9")));
			e.setAttribute10(c.getString(c.getColumnIndex("attribute10")));
			list.add(e);
		}
		
		return list;
	}
	
	public int getEvent_id() {
		return event_id;
	}
	public void setEvent_id(int event_id) {
		this.event_id = event_id;
	}
	public String getEvent_desc() {
		return event_desc;
	}
	public void setEvent_desc(String event_desc) {
		this.event_desc = event_desc;
	}
	public String getAttribute1() {
		return attribute1;
	}
	public void setAttribute1(String attribute1) {
		this.attribute1 = attribute1;
	}
	public String getAttribute2() {
		return attribute2;
	}
	public void setAttribute2(String attribute2) {
		this.attribute2 = attribute2;
	}
	public String getAttribute3() {
		return attribute3;
	}
	public void setAttribute3(String attribute3) {
		this.attribute3 = attribute3;
	}
	public String getAttribute4() {
		return attribute4;
	}
	public void setAttribute4(String attribute4) {
		this.attribute4 = attribute4;
	}
	public String getAttribute5() {
		return attribute5;
	}
	public void setAttribute5(String attribute5) {
		this.attribute5 = attribute5;
	}
	public String getAttribute6() {
		return attribute6;
	}
	public void setAttribute6(String attribute6) {
		this.attribute6 = attribute6;
	}
	public String getAttribute7() {
		return attribute7;
	}
	public void setAttribute7(String attribute7) {
		this.attribute7 = attribute7;
	}
	public String getAttribute8() {
		return attribute8;
	}
	public void setAttribute8(String attribute8) {
		this.attribute8 = attribute8;
	}
	public String getAttribute9() {
		return attribute9;
	}
	public void setAttribute9(String attribute9) {
		this.attribute9 = attribute9;
	}
	
	public void setAttribute10(String attribute10) {
		this.attribute10 = attribute10;
	}
	public String getAttribute10() {
		return attribute10;
	}
	
	public String toString(){
		return this.attribute1;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(getEvent_id());
		dest.writeString(getEvent_desc());
		dest.writeString(getAttribute1());
		dest.writeString(getAttribute2());
		dest.writeString(getAttribute3());
		dest.writeString(getAttribute4());
		dest.writeString(getAttribute5());
		dest.writeString(getAttribute6());
		dest.writeString(getAttribute7());
		dest.writeString(getAttribute8());
		dest.writeString(getAttribute9());
		dest.writeString(getAttribute10());
	}
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Event createFromParcel(Parcel in) {
            return new Event(in); 
        }

        public Event[] newArray(int size) {
            return new Event[size];
        }
    };
    
	
}
