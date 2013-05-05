package com.example.firstalarm;

import android.app.Application;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;


public class AlarmListAdapter extends ArrayAdapter<Alarm> implements
		OnCheckedChangeListener{

	// This class is responsible for building up list view
	private Context context;
	private AlarmClockApplication application;
	
	public AlarmListAdapter(Application application, Context context) {
		super(context, android.R.layout.simple_expandable_list_item_1,
				((AlarmClockApplication) application).getAlarms());
		this.application = (AlarmClockApplication) application;
		this.context = context;
		
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		
		final AlarmListItem alarmListItem = new AlarmListItem(context, null);
		View view;				
		if (null == convertView) {
			view = AlarmListItem.inflate(context, R.layout.list_item, alarmListItem); 			
			alarmListItem.setAlarm(application.get(position));
		
		} else {
			view = convertView;
		}		
		
		return view;
	}
	
	public void addAlarm(Alarm alarm) {
		application.addAlarm(alarm);
		notifyDataSetChanged();
	}

	public void removeAlarm(int position) {
		application.deleteAlarm(position);
		notifyDataSetChanged();
	}

	public void undoDeleteAlarm() {
		application.undoDeleteLastAlarm();
		notifyDataSetChanged();
	}

	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		AlarmListItem li = (AlarmListItem) buttonView.getParent();
		Alarm alarm = li.getAlarm();
		alarm.setEnable(isChecked);
		application.saveAlarm(alarm);
	}

	public void saveAlarm(Alarm alarm) {
		application.saveAlarm(alarm);
		notifyDataSetChanged();
	}

}
