package com.example.firstalarm;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.example.firstalarm.UndoBarController.UndoListener;

public class AlarmListActivity extends ListActivity implements UndoListener{
	
	private ListView alarmList;
	private UndoBarController undoBarController;
	private Context context;
	private AlarmClockApplication application;
	private static final int REQUEST_ALARM_NEW = 1;
	private static final int REQUEST_ALARM_UPDATE = 2;
	private int lastDeletedPosition;
	private static final int SWIPE_MIN_DISTANCE = 150;
	private static final int SWIPE_MAX_OFF_PATH = 100;
	private static final int SWIPE_THRESHOLD_VELOCITY = 100;
	private GestureDetector gestureScanner;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_alarm_list);
		alarmList = getListView();
		alarmList.setAdapter(new AlarmListAdapter(getApplication(), this));
		alarmList.setClickable(true);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		this.application = (AlarmClockApplication) application;
		this.context = context;
				
		//Button add = (Button)findViewById(R.id.add_alarm);
		
		gestureScanner = new GestureDetector(this, simpleOnGestureListener);
		undoBarController = new UndoBarController(findViewById(R.id.undobar),
				this);
		
		
		/*add.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent intent = new Intent(AlarmListActivity.this, AlarmAddActivity.class);
				startActivityForResult(intent, REQUEST_ALARM_NEW);
			}
		});
		*/
		
		View.OnTouchListener gestureListener = new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				return gestureScanner.onTouchEvent(event);
			}
		};
		alarmList.setOnTouchListener(gestureListener);
    }
	
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.new_alarm, menu);
		getMenuInflater().inflate(R.menu.settings, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		
	    case android.R.id.home: 
	        onBackPressed();
	        break;
		case R.id.menu_add_new_alarm:
			Intent intent = new Intent(this, AlarmAddActivity.class);
			startActivityForResult(intent, REQUEST_ALARM_NEW);
			break;
		case R.id.menu_settings:
			Intent intent1 = new Intent(this, SettingsActivity.class);
			startActivityForResult(intent1, 0);
			break;
		default:
		}
		return super.onOptionsItemSelected(item);
	}

	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (RESULT_OK != resultCode) {
			return;
		}
		Bundle extras = data.getExtras();
		Alarm alarm = (Alarm) extras.getSerializable("ALARM");
		switch (requestCode) {
		case REQUEST_ALARM_NEW:
			((AlarmListAdapter) alarmList.getAdapter()).addAlarm(alarm);
			break; 
		case REQUEST_ALARM_UPDATE:
			((AlarmListAdapter) alarmList.getAdapter()).saveAlarm(alarm);
			break;
		default:
		}
	}
	
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		undoBarController.onSaveInstanceState(outState);
	}

	protected void onRestoreInstanceState(Bundle state) {
		super.onRestoreInstanceState(state);
		undoBarController.onRestoreInstanceState(state);
	}
	
	MotionEvent mLastOnDownEvent = null;

	GestureDetector.SimpleOnGestureListener simpleOnGestureListener = new GestureDetector.SimpleOnGestureListener(){
		
		public boolean onDown(MotionEvent event) {
			   mLastOnDownEvent = event;
	           return true;
	    }
		
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			if(e1==null){
				e1 = mLastOnDownEvent;
			}
			if (e1 == null || e2 == null) {
				return false;
			}

			lastDeletedPosition = alarmList.pointToPosition((int) e1.getX(),
					(int) e1.getY());
			if (AdapterView.INVALID_POSITION == lastDeletedPosition) {
				return false;
			}

			AlarmListItem item = (AlarmListItem) alarmList
					.getChildAt(lastDeletedPosition);

			if (null == item) {
				return false;
			}

			float dX = e2.getX() - e1.getX();
			float dY = e1.getY() - e2.getY();

			if (Math.abs(dY) < SWIPE_MAX_OFF_PATH
					&& Math.abs(velocityX) >= SWIPE_THRESHOLD_VELOCITY
					&& Math.abs(dX) >= SWIPE_MIN_DISTANCE) {
				Animation anim;
				if (dX > 0) {
					anim = AnimationUtils.makeOutAnimation(item.getContext(), true);
				} else {
					anim = AnimationUtils
							.makeOutAnimation(item.getContext(), false);
				}
				/*anim.setAnimationListener((AnimationListener) anim);
				item.startAnimation(anim);
				UndoBarController.showUndoBar(false,
						getString(R.string.undo_delete_alarm), null);*/
				((AlarmListAdapter) alarmList.getAdapter()).removeAlarm(lastDeletedPosition);
				return true;
			}
			return false;

		}
		public boolean onSingleTapUp(MotionEvent e){
			int position = alarmList
					.pointToPosition((int) e.getX(), (int) e.getY());
			if (AdapterView.INVALID_POSITION == position) {
				return false;
			}

			AlarmListItem item = (AlarmListItem) alarmList.getChildAt(position);

			if (null == item) {
				return false;
			}

			Intent intent = new Intent(alarmList.getContext(), AlarmAddActivity.class);
			intent.putExtra("ALARM", item.getAlarm());
			startActivityForResult(intent, REQUEST_ALARM_UPDATE);

			return true;
			
		}
		
	};

	@Override
	public void onUndo(Parcelable token) {
		// TODO Auto-generated method stub
	}
	
	
	}