package com.example.firstalarm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.Application;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class AlarmClockApplication extends Application {

	private List<Alarm> alarms = null;
	private SQLiteDatabase db;
	private Alarm lastRemovedAlarm;

	public void onCreate() {
		super.onCreate();
		
		AlarmsSQLiteOpenHelper helper = new AlarmsSQLiteOpenHelper(this);
		db = helper.getWritableDatabase();
		if (null == alarms) {
			loadAlarms();
		}
	}

	private void loadAlarms() {
		//alarms = Collections.synchronizedList(new ArrayList<Alarm>());
		alarms = new ArrayList<Alarm>();
		Cursor alarmsCursor = db.query(AlarmsSQLiteOpenHelper.ALARMS_TABLE, new String[] { AlarmsSQLiteOpenHelper.ALARM_ID,
				AlarmsSQLiteOpenHelper.ALARM_ENABLED, AlarmsSQLiteOpenHelper.ALARM_TIME, AlarmsSQLiteOpenHelper.ALARM_RECURRING }, null, null, null, null, AlarmsSQLiteOpenHelper.ALARM_TIME);

		alarmsCursor.moveToFirst();
		Alarm alarm = null;

		while (!alarmsCursor.isAfterLast()) {
			long id = alarmsCursor.getLong(0);
			boolean enabled = alarmsCursor.getString(1).equals("0") ? false	: true;
			String time = alarmsCursor.getString(2);
			long recurring = alarmsCursor.getLong(3);
			boolean[] repeatOn = getArrayRecurringDaysFromLong(recurring);

			alarm = new Alarm();
			alarm.setId(id);
			alarm.setEnable(enabled);
			alarm.setTime(time);
			alarm.setRepeatOn(repeatOn);
			
			alarms.add(alarm);

			alarmsCursor.moveToNext();
		}
		
		alarmsCursor.close();
	}

	public void addAlarm(Alarm alarm) {
		assert (null != alarm);

		ContentValues values = new ContentValues();

		values.put(AlarmsSQLiteOpenHelper.ALARM_ENABLED, alarm.isEnable() ? 1 : 0);
		values.put(AlarmsSQLiteOpenHelper.ALARM_TIME, alarm.getTime());
		values.put(AlarmsSQLiteOpenHelper.ALARM_MESSAGE, alarm.getMessage());
		values.put(AlarmsSQLiteOpenHelper.ALARM_RECURRING, alarm.getRepeatInt());
		values.put(AlarmsSQLiteOpenHelper.ALARM_RINGTONE, alarm.getRingtone());
		alarm.setId(db.insert(AlarmsSQLiteOpenHelper.ALARMS_TABLE, null, values));
		alarms.add(alarm);
	
	}

	public void saveAlarm(Alarm alarm) {
		assert (null != alarm);

		ContentValues values = new ContentValues();

		values.put(AlarmsSQLiteOpenHelper.ALARM_ENABLED, alarm.isEnable() ? 1 : 0);
		values.put(AlarmsSQLiteOpenHelper.ALARM_TIME, alarm.getTime());
		values.put(AlarmsSQLiteOpenHelper.ALARM_MESSAGE, alarm.getMessage());
		values.put(AlarmsSQLiteOpenHelper.ALARM_RECURRING, alarm.getRepeatInt());
		values.put(AlarmsSQLiteOpenHelper.ALARM_RINGTONE, alarm.getRingtone());
		
		long id = alarm.getId();
		String where = String.format("%s = ?", AlarmsSQLiteOpenHelper.ALARM_ID);
		db.update(AlarmsSQLiteOpenHelper.ALARMS_TABLE, values, where, new String[] { id + "" });
		
		synchronized (alarms) {
			Iterator<Alarm> i = alarms.iterator();
			while (i.hasNext()) {
				Alarm outdatedAlarm = i.next();
				if (outdatedAlarm.getId() == id) {
					outdatedAlarm.copyFrom(alarm);
					break;
				}
			}
		}
	}

	public void deleteAlarm(int position) {
		Alarm a = alarms.remove(position);
		String where = String.format("%s = %s", AlarmsSQLiteOpenHelper.ALARM_ID, a.getId());
		db.delete(AlarmsSQLiteOpenHelper.ALARMS_TABLE, where, null);		
		/*Iterator<Alarm> i = alarms.iterator();		
		for (Iterator<Alarm> it = alarms.iterator(); it.hasNext(); )
	        if (id == it.next().getId()){
	            it.remove();
	            break;
	        }*/
	}

	public void undoDeleteLastAlarm() {
		if (null == lastRemovedAlarm) {
			return;
		}

		addAlarm(lastRemovedAlarm);
		lastRemovedAlarm = null;
	}

	public Alarm get(int position) {
		return alarms.get(position);		
	}

	
	private long getLongRecurringDaysFromArray(boolean[] recurringArray) {
		long recurring = 0;
		if (null == recurringArray) {
			return recurring;
		}
		for (int day = 0; day < 7; day++) {
			int val = recurringArray[day] ? 1 : 0;
			recurring = recurring | (val << day);
		}
		return recurring;
	}

	private boolean[] getArrayRecurringDaysFromLong(long recurring) {
		boolean[] recurringArray = new boolean[7];

		for (int day = 0; day < 7; day++) {
			recurringArray[day] = (((recurring & (1 << day)) > 0) ? true
					: false);
		}
		return recurringArray;
	}

	public List<Alarm> getAlarms() {
		return alarms;
	}

	public void setAlarms(List<Alarm> alarms) {
		this.alarms = alarms;
	}

}
