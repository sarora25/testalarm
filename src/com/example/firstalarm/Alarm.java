package com.example.firstalarm;

import java.io.Serializable;
import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import android.annotation.SuppressLint;

@SuppressLint("NewApi")
public class Alarm implements Serializable{

	private static final long serialVersionUID = 1L;
	private long id;
	private boolean enable;
	private boolean[] repeatOn;
	private String ringtone;
	private String time;
	private String message;
	private static String[] weekDays;
	
	static {
		ArrayList<String> daysFormated = new ArrayList<String>();
		for (String shortDay : DateFormatSymbols.getInstance(
				Locale.getDefault()).getShortWeekdays()) {
			// remove first blank element
			if (0 == shortDay.length()) {
				continue;
			}
			// make sure strings are capitalized and don't contain
			// extra punctuation (see Locale.FRENCH)
			daysFormated.add(Character.toUpperCase(shortDay.charAt(0))
					+ shortDay.substring(1, shortDay.length())
							.replace('.', ' ').trim());
		}
		weekDays = daysFormated.toArray(new String[daysFormated.size()]);
	}

	public Alarm() {
		enable = true;
		repeatOn = new boolean[7];
		for (int i = 0; i < repeatOn.length; i++) {
			repeatOn[i] = false;
		}
		ringtone = new String("");
		time = new String("");
		
	}

	public String getRepeatString() {
		if (null == repeatOn) {
			return "";
		}

		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < repeatOn.length; i++) {
			if (repeatOn[i]) {
				sb.append(weekDays[i] + " ");
			}
		}
		return sb.toString().trim();
	}
	
	public int getRepeatInt(){
		if (null == repeatOn) {
			return 0000000;
		}

		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < repeatOn.length; i++) {
			if (repeatOn[i]) {
				sb.append("1");
			}else sb.append("0");
		}
		return Integer.parseInt(sb.toString());
	}

	public long getNextTriggerInMillis() {
		Calendar cal = Calendar.getInstance(Locale.getDefault());
		Calendar now = Calendar.getInstance(Locale.getDefault());
		int hour = Integer.valueOf(time.split(":")[0]);
		int minute = Integer.valueOf(time.split(":")[1]);
		cal.set(Calendar.HOUR, hour);
		cal.set(Calendar.MINUTE, minute);
		cal.set(Calendar.SECOND, 0);

		if (null == repeatOn) {
			if (cal.compareTo(now) > 0) {
				// alarm time is in the future, return offset
				return cal.getTimeInMillis();
			}
			// alarm time is in the past. Since we're not setting a
			// repeatable alarm, add one day and return offset
			cal.add(Calendar.DATE, 1);
			return cal.getTimeInMillis();
		}
		int day = now.get(Calendar.DAY_OF_WEEK);
		// loop in array with active alarm days for the next seven days and
		// locate the next active day
		for (int i = day; i < repeatOn.length + day; i++) {
			if (repeatOn[i % 7]) {
				break;
			}
			cal.add(Calendar.DATE, 1);
		}
		return cal.getTimeInMillis();
	}

	public long[] getAlarmManagerEvents() {
		long[] alarms = new long[2];

		if (!enable) {
			return null;
		}

		alarms[0] = getNextTriggerInMillis();
		alarms[1] = ((null == repeatOn) ? (long) 0.0
				: android.app.AlarmManager.INTERVAL_DAY);

		return alarms;
	}

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean active) {
		this.enable = active;
	}

	public boolean isRepeating() {
		return (null != repeatOn);
	}

	public boolean[] getRepeatOn() {
		return repeatOn;
	}

	public void setRepeatOn(boolean[] repeatOn) {
		this.repeatOn = repeatOn;
	}

	public String getRingtone() {
		return ringtone;
	}

	public void setRingtone(String ringtone) {
		this.ringtone = ringtone;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "Alarm [active=" + enable + ", ringtone=" + ringtone + ", time="
				+ time + ", message=" + message  + ", repeatOn=" + getRepeatString() + "]";
	}

	public void copyFrom(Alarm alarm) {
		enable= alarm.isEnable();
		repeatOn = alarm.getRepeatOn();
		ringtone = alarm.getRingtone();
		time = alarm.getTime();
		message = alarm.getMessage();
	}

}
