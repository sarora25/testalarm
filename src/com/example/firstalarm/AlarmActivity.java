package com.example.firstalarm;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

/** Class for managing the "Snooze"(AlarmActivity) Activity */

public class AlarmActivity extends Activity implements SensorEventListener {

		public RadioButton SnoozeRadio1;
		public RadioButton SnoozeRadio2;
		public RadioButton SnoozeRadio5;
		public RadioButton SnoozeRadio10;
		public int SnoozeMin;
		public MediaPlayer mMediaPlayer;
		public boolean AlarmEnabled;
		public boolean Snoozed;
		TextView proxText;
		SensorManager sm;
		Sensor proxSensor;
		
		/** Called when the activity is first created. */
		@Override 
		public void onCreate(Bundle savedInstanceState) {
			
		    super.onCreate(savedInstanceState);
		    Snoozed = false;
	        
		    //Set window features to hide the notification bar and make the UI fullscreen before assigning the layout
		    requestWindowFeature(Window.FEATURE_NO_TITLE);
		    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);        
	        setContentView(R.layout.alarm);
	        
	        //Enable Proximity sensor
	        sm = (SensorManager) getSystemService(SENSOR_SERVICE);
	        proxSensor = sm.getDefaultSensor(Sensor.TYPE_PROXIMITY);
	        sm.registerListener(this, proxSensor, SensorManager.SENSOR_DELAY_NORMAL);
	        
	        //Get Last used Snooze Value and use as default
	        SharedPreferences settings = getSharedPreferences(MainActivity.PREFS_NAME, 0);
	        //AlarmEnabled = settings.getBoolean(MainActivity.ALARM_ENABLED, false);
	        SnoozeMin = settings.getInt(MainActivity.SNOOZE_MINUTE, 10);
	        
	        //Assign UI components to local variables and define Click Event Listeners
	        SnoozeRadio1 = (RadioButton)findViewById(R.id.SnoozeMin1);
	        SnoozeRadio1.setOnClickListener(new MyRadioListener1());        
	        SnoozeRadio2 = (RadioButton)findViewById(R.id.SnoozeMin2);
	        SnoozeRadio2.setOnClickListener(new MyRadioListener2());        
	        SnoozeRadio5 = (RadioButton)findViewById(R.id.SnoozeMin5);
	        SnoozeRadio5.setOnClickListener(new MyRadioListener5());        
	        SnoozeRadio10 = (RadioButton)findViewById(R.id.SnoozeMin10);
	        SnoozeRadio10.setOnClickListener(new MyRadioListener10());
	        Button SnoozeButton = (Button)this.findViewById(R.id.snooze_button);
	        SnoozeButton.setOnClickListener(new MySnoozeListener());
	      
	        //Initialize UI Components
	        SnoozeRadio1.setChecked(false);
	    	SnoozeRadio2.setChecked(false);   	    
	    	SnoozeRadio5.setChecked(false);    
	    	SnoozeRadio10.setChecked(false);   
	    	
	    	//Set Default Value based on saved preference
	        if (SnoozeMin == 1) {
	            SnoozeRadio1.setChecked(true);   		
	        }
	        else if(SnoozeMin == 2) {
	        	SnoozeRadio2.setChecked(true);    
	        }
	        else if(SnoozeMin == 5 ) {  	    
	        	SnoozeRadio5.setChecked(true);     
	        }
	        else if(SnoozeMin == 10) {
	        	SnoozeRadio10.setChecked(true);     	
	        }	    
	        
	        //Create Media Player for sounding the system alarm
	        Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM); 
	        mMediaPlayer = new MediaPlayer();
	        try {
	      	  mMediaPlayer.setDataSource(this, alert);
	  		  mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
	  		  mMediaPlayer.setLooping(true);
	  		  mMediaPlayer.prepare();
	  		  mMediaPlayer.start();
	        }
	        catch(Exception e) {
	        }
	        
	        /**OnShake Activity**/
	        
	        ShakeListener MyShake = new ShakeListener((SensorManager) getSystemService(SENSOR_SERVICE));
	        MyShake.setForceThreshHold(1.9);
	        MyShake.setOnShakeListener(new ShakeListener.OnShakeListener() {
	        	
	        	public void onShake() {
	        		try {
	    	      	  	mMediaPlayer.stop();
	    	        }
	    	        catch(Exception e) {
	    	        }
	    		  	
	    	    	Calendar calendar = Calendar.getInstance();
	    	   		calendar.add(Calendar.MINUTE, SnoozeMin);
	    	      	  	
	         	  	//Build Intent and Pending Intent to Set Snooze Alarm               
	        		Intent AlarmIntent = new Intent(AlarmActivity.this, AlarmReceiver.class);
	  	        	AlarmManager AlmMgr = (AlarmManager)getSystemService(ALARM_SERVICE);
	   	        	PendingIntent Sender = PendingIntent.getBroadcast(AlarmActivity.this, 0, AlarmIntent, 0);   
	   	    		AlmMgr.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), Sender);
	    	        
	    			
	    		    //Cancel the Notification. Will re-occur on next alarm occurance
	    			NotificationManager manager = (NotificationManager)getSystemService(android.content.Context.NOTIFICATION_SERVICE);
	    			manager.cancel(R.string.app_name);
	    		
	    			Snoozed = true;
	    			
	    	  	  	//Close Activity
	    	        finish(); 
	             }
	        });
		}	
		
		//Class for implementing the Click Event listener for the Snooze Button
	    public class MySnoozeListener implements Button.OnClickListener {
	    	@Override
	    	public void onClick(View v) {  

	    		//Stop Alarm Sound
	            try {
		      	  	mMediaPlayer.stop();
	            }
	            catch(Exception e) {

	            }
	        	
	        	//Check Alarm Enabled Preference: Enabled = Snooze Alarm, Disabled = Disarm Alarm
	            //if (AlarmEnabled) {        	
		        //Set Calendar Value for Snooze Alarm
		    	Calendar calendar = Calendar.getInstance();
		   		calendar.add(Calendar.MINUTE, SnoozeMin);
		      	  	
		   	  	//Build Intent and Pending Intent to Set Snooze Alarm               
	    		Intent AlarmIntent = new Intent(AlarmActivity.this, AlarmReceiver.class);
	        	AlarmManager AlmMgr = (AlarmManager)getSystemService(ALARM_SERVICE);
	        	PendingIntent Sender = PendingIntent.getBroadcast(AlarmActivity.this, 0, AlarmIntent, 0);   
		    	AlmMgr.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), Sender);
	           // }
	    		
	    	    //Cancel the Notification. Will re-occur on next alarm occurance
	    		NotificationManager manager = (NotificationManager)getSystemService(android.content.Context.NOTIFICATION_SERVICE);
	    		manager.cancel(R.string.app_name);
	      	  	
	    		Snoozed = true;
	    		
	      	  	//Close Activity
	            finish();  
	    	}
	    }
	    
	    /** Proximity Sensor**/
	    public void onAccuracyChanged(Sensor arg0, int arg1) {
			// TODO Auto-generated method stub
		}

		public void onSensorChanged(SensorEvent arg0) {
			try {
	      	  	mMediaPlayer.stop();
	        }
	        catch(Exception e) {
	        }
		
	  		Calendar calendar = Calendar.getInstance();
	   		calendar.add(Calendar.MINUTE, SnoozeMin);
	      	  	
     	  	//Build Intent and Pending Intent to Set Snooze Alarm               
	   		Intent AlarmIntent = new Intent(AlarmActivity.this, AlarmReceiver.class);
	       	AlarmManager AlmMgr = (AlarmManager)getSystemService(ALARM_SERVICE);
	       	PendingIntent Sender = PendingIntent.getBroadcast(AlarmActivity.this, 0, AlarmIntent, 0);   
	    	AlmMgr.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), Sender);
			
		    //Cancel the Notification. Will re-occur on next alarm occurance
			NotificationManager manager = (NotificationManager)getSystemService(android.content.Context.NOTIFICATION_SERVICE);
			manager.cancel(R.string.app_name);
		
			Snoozed = true;
			
	  	  	//Close Activity
	        finish();  
}

		//Class for implementing the Click Event listener for the Radio Button snooze 1
		public class MyRadioListener1 implements RadioButton.OnClickListener {

			public void onClick(View v) {
		        SnoozeRadio1.setChecked(true);
		    	SnoozeRadio2.setChecked(false);   	    
		    	SnoozeRadio5.setChecked(false);    
		    	SnoozeRadio10.setChecked(false);   
		    	SnoozeMin = 1;
			}
		}

		//Class for implementing the Click Event listener for the Radio Button snooze 2
		public class MyRadioListener2 implements RadioButton.OnClickListener {

			public void onClick(View v) {
		    	SnoozeRadio1.setChecked(false); 
		        SnoozeRadio2.setChecked(true);  	    
		    	SnoozeRadio5.setChecked(false);    
		    	SnoozeRadio10.setChecked(false);   
		    	SnoozeMin = 2; 
			}
		}

		//Class for implementing the Click Event listener for the Radio Button snooze 5
		public class MyRadioListener5 implements RadioButton.OnClickListener {
			
			public void onClick(View v) { 	    
		    	SnoozeRadio1.setChecked(false);  
		    	SnoozeRadio2.setChecked(false);  
		        SnoozeRadio5.setChecked(true);  
		    	SnoozeRadio10.setChecked(false);   
		    	SnoozeMin = 5;    
			}
		}

		//Class for implementing the Click Event listener for the Radio Button snooze 10
		public class MyRadioListener10 implements RadioButton.OnClickListener {
			
			public void onClick(View v) {  
		    	SnoozeRadio1.setChecked(false);  
		    	SnoozeRadio2.setChecked(false);   	    
		    	SnoozeRadio5.setChecked(false);  
		        SnoozeRadio10.setChecked(true);   
		    	SnoozeMin = 10;  
			}
		}
		
		//Override Pause because it is always called before an Activity is Destroyed
		//And I use it to Save my Settings
	   
	    public void onPause() {
			//Save settings Enabled, Alarm Hour, Alarm Minute
	        SharedPreferences settings = getSharedPreferences(MainActivity.PREFS_NAME, 0);
	        SharedPreferences.Editor editor = settings.edit();
	        //editor.putBoolean(MainActivity.ALARM_ENABLED, AlarmEnabled);
	        editor.putInt(MainActivity.SNOOZE_MINUTE, SnoozeMin);

	        //Commit the edits
	        editor.commit();
	                
	        super.onPause();
	    }
	    
	    //Override this to restore settings when Activity is restored
	    
	    public void onRestoreInstanceState(Bundle savedInstanceState) {
			super.onRestoreInstanceState(savedInstanceState);
			
	        //Fetch Saved settings (Alarm Hour, Minute, Enable) and assign values accordingly
	        SharedPreferences settings = getSharedPreferences(MainActivity.PREFS_NAME, 0);
 	        //AlarmEnabled = settings.getBoolean(MainActivity.ALARM_ENABLED, false);
	        SnoozeMin = settings.getInt(MainActivity.SNOOZE_MINUTE, 10);

	        //Re-Initialize UI Components
	        SnoozeRadio1.setChecked(false);
	    	SnoozeRadio2.setChecked(false);   	    
	    	SnoozeRadio5.setChecked(false);    
	    	SnoozeRadio10.setChecked(false);   
	    	
	    	//Set Default Value based on saved preference
	        if (SnoozeMin == 1) {
	            SnoozeRadio1.setChecked(true);   		
	        }
	        else if(SnoozeMin == 2) {
	        	SnoozeRadio2.setChecked(true);    
	        }
	        else if(SnoozeMin == 5 ) {  	    
	        	SnoozeRadio5.setChecked(true);     
	        }
	        else if(SnoozeMin == 10) {
	        	SnoozeRadio10.setChecked(true);     	
	        }	
	    }		
}

