package com.example.nonameproject.util;

import java.util.ArrayList;
import java.util.List;

import com.example.nonameproject.model.Event;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class Database extends SQLiteOpenHelper{
	
	private static final String DB_NAME = "islamic_events.dblite";
	private static final int DB_VERSION = 3;
	public static final String EVENT_TABLE = "event";
	public static final String EVENT_ID = "event_id";
	public static final String EVENT_DESCRIPTION = "event_desc";
	public static final String ATTRIBUTE1 = "attribute1";
	public static final String ATTRIBUTE2 = "attribute2";
	public static final String ATTRIBUTE3 = "attribute3";
	public static final String ATTRIBUTE4 = "attribute4";
	public static final String ATTRIBUTE5 = "attribute5";
	public static final String ATTRIBUTE6 = "attribute6";
	public static final String ATTRIBUTE7 = "attribute7";
	public static final String ATTRIBUTE8 = "attribute8";
	public static final String ATTRIBUTE9 = "attribute9";
	public static final String ATTRIBUTE10 = "attribute10";
	
	private static final String CREATE_EVENT_TABLE = "CREATE TABLE "+EVENT_TABLE+" ( "+
			EVENT_ID+" INTEGER NOT NULL, "+
			EVENT_DESCRIPTION+" TEXT NOT NULL, "+
			ATTRIBUTE1+" TEXT NOT NULL, "+
			ATTRIBUTE2+" TEXT NOT NULL, "+
			ATTRIBUTE3+" TEXT NOT NULL, "+
			ATTRIBUTE4+" TEXT NOT NULL, "+
			ATTRIBUTE5+" TEXT NOT NULL, "+
			ATTRIBUTE6+" TEXT NOT NULL, "+
			ATTRIBUTE7+" TEXT NOT NULL, "+
			ATTRIBUTE8+" TEXT NOT NULL, "+
			ATTRIBUTE9+" TEXT NOT NULL, "+
			ATTRIBUTE10+" TEXT NOT NULL );";
	
	public Database(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_EVENT_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS "+EVENT_TABLE);
		onCreate(db);
	}
	
	public long saveEvent(Event event){

		SQLiteDatabase database = this.getWritableDatabase();
		
		ContentValues cv = new ContentValues();
		cv.put(EVENT_ID, event.getEvent_id());
		cv.put(EVENT_DESCRIPTION, event.getEvent_desc());
		cv.put(ATTRIBUTE1, event.getAttribute1());
		cv.put(ATTRIBUTE2, event.getAttribute2());
		cv.put(ATTRIBUTE3, event.getAttribute3());
		cv.put(ATTRIBUTE4, event.getAttribute4());
		cv.put(ATTRIBUTE5, event.getAttribute5());
		cv.put(ATTRIBUTE6, event.getAttribute6());
		cv.put(ATTRIBUTE7, event.getAttribute7());
		cv.put(ATTRIBUTE8, event.getAttribute8());
		cv.put(ATTRIBUTE9, event.getAttribute9());
		cv.put(ATTRIBUTE10, event.getAttribute10());
		long id = database.insert(EVENT_TABLE, null, cv);
		database.close();
		
		return id;
		
	}
	
	public List<Event> getEvents(){
	
		SQLiteDatabase database = this.getWritableDatabase();
		Cursor c = database.query(EVENT_TABLE, null, null, null, null, null, null);
		List<Event> list = Event.createFromCursor(c);
		database.close();
		return list;
		
	}
	
	
	
	
}
