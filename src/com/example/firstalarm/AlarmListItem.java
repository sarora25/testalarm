package com.example.firstalarm;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

public class AlarmListItem extends RelativeLayout implements 
				OnItemClickListener {

	private TextView repeatText;
	private TextView alarmTime;
	private EditText text;
	private Alarm alarm;
	private boolean hasFoundViews;
	private ListView alarmList;
	private RelativeLayout rlayout;
	private boolean layoutVisible = false;
	private CheckBox repeat;
	private CheckBox editRepeat;
	private CheckBox enable;
	private CheckBox editEnable;
	private ImageButton delete;
	
	private static int[] edit_daysIDList = new int[]{ R.id.edit_sun_tgl, R.id.edit_mon_tgl,
		R.id.edit_tue_tgl, R.id.edit_wed_tgl, R.id.edit_thu_tgl, R.id.edit_fri_tgl,
		R.id.edit_sat_tgl };
	private static String[] weekDays;
	private static final int SWIPE_MIN_DISTANCE = 150;
	private static final int SWIPE_MAX_OFF_PATH = 100;
	private static final int SWIPE_THRESHOLD_VELOCITY = 100;
	private int lastDeletedPosition;
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
	
	
	public AlarmListItem(Context context, AttributeSet attrs) {
		super(context, attrs);
		hasFoundViews = false;
	}
            
	private void findViews() {
		
		if (!hasFoundViews)
		{
			alarmTime = (TextView) findViewById(R.id.list_item_text);
			repeatText = (TextView) findViewById(R.id.alarm_repeat);
			rlayout = (RelativeLayout)findViewById(R.id.layout2);
			setRepeat((CheckBox)findViewById(R.id.repeat_chk));
			editRepeat = (CheckBox)findViewById(R.id.edit_repeat_chk);
			//name = (TextView) findViewById(R.id.name);
			text = (EditText) findViewById(R.id.label);
			setEnable((CheckBox)findViewById(R.id.enable));
			editEnable = ((CheckBox)findViewById(R.id.edit_enable));
			text.setOnKeyListener(new OnKeyListener(){

				public boolean onKey(View v, int keyCode, KeyEvent event) {
					//name.setText(text.getText().toString());
					return true;
				}
				
		    });
			// add view with set  visibility false.
			rlayout.setVisibility(View.GONE);
		}
	}
	
	private void setDaysToggles(boolean isChecked){
		ToggleButton toggle;
		for (int i = 0; i < edit_daysIDList.length; i++) {
			toggle = (ToggleButton) findViewById(edit_daysIDList[i]);
			toggle.setEnabled(isChecked);
		}
	}
	
	public void setAlarm(Alarm alarm) {

		findViews();
		this.alarm = alarm;
		alarmTime.setText(alarm.getTime());
		repeatText.setText(alarm.getRepeatString());
		//name.setText(text.getText().toString());
	}

	public Alarm getAlarm() {
		return alarm;
	}
	
	private boolean[] getEditRepeatOn() {
		boolean[] repeatOn = null;
		CheckBox edrep = (CheckBox) findViewById(R.id.edit_repeat_chk);
		if (edrep.isChecked()) {
			repeatOn = new boolean[7];
			ToggleButton toggle;
			for (int i = 0; i < edit_daysIDList.length; i++) {
				toggle = (ToggleButton) findViewById(edit_daysIDList[i]);
				repeatOn[i] = toggle.isChecked();
			}
		}
		return repeatOn;
	}

	private boolean getEditEnableOn(){
		CheckBox enable = (CheckBox) findViewById(R.id.edit_enable);
		return enable.isChecked();
	}
	
	public void setOnCheckedChangeListener(Alarm a) {
		findViews();
		
		if(!layoutVisible){
    		rlayout.setVisibility(View.VISIBLE);
    		
    		if(a.isRepeating()){
    			editRepeat.setChecked(true);
    			setDaysToggles(true);
    		}
    		if(a.isEnable())
    			editEnable.setChecked(true);
    		layoutVisible = true;
    	}
    	else{
    		// This means we have now edited the alarm options and now we want to save it.
    		a.setRepeatOn(getEditRepeatOn());
    		a.setEnable(getEditEnableOn());
    		rlayout.setVisibility(View.GONE);
    		layoutVisible = false;
    	}
		
		editRepeat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				setDaysToggles(isChecked);
			}
		});
		
	}

	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
		// Edit this Alarm Item
		
		// Text Edit
		
		// Repeat Edit as well as days
		
		// Label Edit

	}
	

public CheckBox getRepeat() {
	return repeat;
}

public void setRepeat(CheckBox repeat) {
	this.repeat = repeat;
}

public CheckBox getEnable() {
	return enable;
}

public void setEnable(CheckBox enable) {
	this.enable = enable;
}

public ImageButton getDelete() {
	return delete;
}

public void setDelete(ImageButton delete) {
	this.delete = delete;
}
}

