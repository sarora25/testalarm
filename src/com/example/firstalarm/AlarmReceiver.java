package com.example.firstalarm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/** Class for receiving broadcast when the Alarm occurs */
public class AlarmReceiver extends BroadcastReceiver {

	    public static final String ALARM_ALERT_ACTION = "com.android.alarmclock.ALARM_ALERT";
	    public static final String ALARM_INTENT_EXTRA = "intent.extra.alarm";
	    
	    @SuppressWarnings("deprecation")
		public void onReceive(Context context, Intent intent) { 
	    	
	      //Create Intent to Start the AlarmActivity "Snooze" Activity
    	  Intent myIntent = new Intent(context, AlarmActivity.class);
	  	  myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	  	  context.startActivity(myIntent);    	    	
	     
	  	  //Build pending intent from calling information to display Notification
	  	  PendingIntent Sender = PendingIntent.getBroadcast(context, 0, intent, 0);
	      NotificationManager manager = (NotificationManager)context.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
	      Notification notification = new Notification(R.drawable.alarm_launcher, "Wake up alarm", System.currentTimeMillis());
	      notification.setLatestEventInfo(context, "Wave2wake", "WAKE UP!!!", Sender);
	      notification.flags = Notification.FLAG_NO_CLEAR;
	      manager.notify(R.string.app_name, notification);  
	      
	    }
	}

