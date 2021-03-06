package com.example.nonameproject.adapter;

import java.util.List;

import com.example.nonameproject.R;
import com.example.nonameproject.R.id;
import com.example.nonameproject.model.Event;

import android.app.Activity;
import android.app.LauncherActivity.ListItem;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class EventListAdapter extends ArrayAdapter<Event>{

	private int resourceID;
	private Context context;
	private List<Event> items;
	
	public EventListAdapter(Context context, int resource,List<Event> items) {
		super(context, resource, items);
		
		this.resourceID = resource;
		this.context = context;
		this.items = items;
		
		//Log.d("NONAME", items.size()+" ");
	}
	
	public void setItems(List<Event> items){
		this.items = items;
	}
	
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		Item item;
		
		if (view == null) {
			
			item = new Item();
			
			view = ((Activity)(context)).getLayoutInflater().inflate(resourceID, parent,false);
			item.tvEventTitle = (TextView) view.findViewById(R.id.tvItemEventTitle);
			item.tvEventAddress = (TextView) view.findViewById(R.id.tvItemEventAddress);
			item.tvEventDate = (TextView) view.findViewById(R.id.tvItemEventDate);
			
			view.setTag(item);
			
		}else{
			
			item = (Item) view.getTag();
		}
		//Log.d("NONAME", items.get(position).getAttribute1()+" "+items.get(position).getAttribute5());
		item.tvEventTitle.setText(items.get(position).getAttribute1());
		item.tvEventAddress.setText(items.get(position).getAttribute3());
		item.tvEventDate.setText(items.get(position).getAttribute2());
		
		
		return view;
	}
	
	private class Item{
		public TextView tvEventTitle;
		public TextView tvEventAddress;
		public TextView tvEventDate;
	}
	
	
}
