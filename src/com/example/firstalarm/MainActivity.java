package com.example.firstalarm;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TimePicker;
import android.widget.Toast;

public class MainActivity extends Activity {
	
    final static String PREFERENCES = "AlarmClock";
    final static String PREF_CLOCK_FACE = "face";
    final static String PREF_SHOW_CLOCK = "show_clock";
    private static final int REQUEST_ALARM_NEW = 1;
	private static final int REQUEST_ALARM_UPDATE = 2;
    
    private SharedPreferences mPrefs;
	public static final String PREFS_NAME = "MyPrefsFile";
	public static final String ALARM_ENABLED = "ALARM_ENABLED";
	public static final String ALARM_HOUR = "ALARM_HOUR";
	public static final String ALARM_MINUTE = "ALARM_MIN";
	public static final String SNOOZE_MINUTE = "SNOOZE_MIN";
	
    int AlarmHour;
	int AlarmMin;

	TimePicker AlarmTimePicker;
	private ImageButton btnSetTime;
	private ImageButton btnSettings;
	private ImageButton btnAddAlarm;
	private ImageButton btnAlarm;
	private ViewGroup mClockLayout;
	private View mClock = null;
	private String mAm, mPm;
	private int hour;
	private int minute;

	static final int TIME_DIALOG_ID = 999;
	
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.main);
			//getActionBar().setDisplayShowTitleEnabled(false);
			//getActionBar().setDisplayShowHomeEnabled(false);
			addListenerOnButton();
		}
	   
		public void addListenerOnButton() {
	 
			btnSetTime = (ImageButton) findViewById(R.id.btnSetTime);
			//btnSettings = (ImageButton) findViewById(R.id.btnSettings);
			//btnAddAlarm = (ImageButton) findViewById(R.id.btnAdd);
			
			
			btnSetTime.setOnClickListener(new OnClickListener() {
	 
				@Override
				public void onClick(View v) {
					showDialog(TIME_DIALOG_ID);
				}
	 
			});
	
		/*	btnSettings.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
					startActivityForResult(intent, 0);
				}
				
			});
			
			btnAddAlarm.setOnClickListener(new OnClickListener(){
				
				public void onClick(View v) {
					Intent intent = new Intent(MainActivity.this, AlarmListActivity.class);
					startActivityForResult(intent, 0);
				}

			});*/
		}
	 
		public Dialog onCreateDialog(int id) {
			switch (id) {
			case TIME_DIALOG_ID:
				// set time picker as current time
				return new TimePickerDialog(this, 
	                                        timePickerListener, hour, minute,false);
	 
			}
			return null;
		}
	 
		public TimePickerDialog.OnTimeSetListener timePickerListener = 
	            new TimePickerDialog.OnTimeSetListener() {
			public void onTimeSet(TimePicker view, int AlarmHour, int AlarmMin) {
				hour = AlarmHour;
				minute = AlarmMin;
				
				//Build Intent/Pending Intent for setting the alarm
	    		Intent AlarmIntent = new Intent(MainActivity.this, AlarmReceiver.class);
	        	AlarmManager AlmMgr = (AlarmManager)getSystemService(ALARM_SERVICE);
	        	PendingIntent Sender = PendingIntent.getBroadcast(MainActivity.this, 0, AlarmIntent, 0);    	

	        	//Build Calendar objects for setting alarm
	    		Calendar curCalendar = Calendar.getInstance();
	    		Calendar alarmCalendar = Calendar.getInstance();
	    		
	    		//Initialize Seconds and Milliseconds to 0 for both calendars
	    		curCalendar.set(Calendar.SECOND, 0);
	    		curCalendar.set(Calendar.MILLISECOND, 0);    		
	    		alarmCalendar.set(Calendar.SECOND, 0);
	    		alarmCalendar.set(Calendar.MILLISECOND, 0);

	    		//Update alarmCalendar with Alarm Hour and Minute Settings
	    		alarmCalendar.set(Calendar.HOUR_OF_DAY, AlarmHour);
	    		alarmCalendar.set(Calendar.MINUTE, AlarmMin);

	    		//If Alarm Time is now or in the past, set it for tomorrow 24 hours in advance from time selected
	    		if (alarmCalendar.getTimeInMillis() <= curCalendar.getTimeInMillis()) {
	    			alarmCalendar.add(Calendar.HOUR, 24);
	    		}
	    		//Set the alarm
	    		AlmMgr.set(AlarmManager.RTC_WAKEUP, alarmCalendar.getTimeInMillis(), Sender);
	    		
	    		//Build the Strings for displaying the alarm time through Toast
	    		String CalendarHourStr;
	    		if (AlarmHour > 12) {
	    			CalendarHourStr = Integer.toString(AlarmHour - 12);
	    		} else {
	    			CalendarHourStr = Integer.toString(AlarmHour);
	    		}
	    		String CalendarMinStr = Integer.toString(AlarmMin);
	    		if (AlarmMin < 10) {
	    			CalendarMinStr = "0" + CalendarMinStr;
	    		}
	    		
	    		String strAmPM;
	    		if (AlarmHour < 12) {
	    			strAmPM = "AM";
	    		}
	    		else {
	    			strAmPM = "PM";
	    		}
	            Toast.makeText(MainActivity.this, "Alarm Set For " + Integer.toString(alarmCalendar.get(Calendar.MONTH) + 1) + "/" + Integer.toString(alarmCalendar.get(Calendar.DAY_OF_MONTH)) + "/" + Integer.toString(alarmCalendar.get(Calendar.YEAR)) + " " + CalendarHourStr + ":" + CalendarMinStr + " " + strAmPM, Toast.LENGTH_LONG).show();    	
	            //btnAlarm.setVisibility(View.VISIBLE);
	            //btnSetTime.setVisibility(View.GONE);
	        }
		};
		
		public boolean onCreateOptionsMenu(Menu menu) {
			getMenuInflater().inflate(R.menu.activity_alarm_list, menu);
			return true;
		}

		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			switch (item.getItemId()) {
			case android.R.id.home:
	            NavUtils.navigateUpFromSameTask(this);
	            return true;
			case R.id.menu_add_alarm:
				Intent intent = new Intent(this, AlarmListActivity.class);
				startActivityForResult(intent, REQUEST_ALARM_NEW);
				break;
			case R.id.menu_settings:
				Intent intent1 = new Intent(this, SettingsActivity.class);
				startActivityForResult(intent1, 0);
			default:
			}
			return super.onOptionsItemSelected(item);
		}
		
	
	}


