package com.example.firstalarm;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AlarmsSQLiteOpenHelper extends SQLiteOpenHelper {

	public static final String DB_NAME = "alarmsDB.db";
	public static final int VERSION = 1;
    public static final String ALARMS_TABLE = "alarms";
    public static final String ALARM_ID = "id";
    public static final String ALARM_ENABLED = "enabled";
    public static final String ALARM_MESSAGE = "message";
    public static final String ALARM_TIME = "time";
    public static final String ALARM_RECURRING= "recurring";
    public static final String ALARM_RINGTONE = "ringtone";

	public AlarmsSQLiteOpenHelper(Context context) {
		super(context,DB_NAME, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		createTable(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Do nothing for now

	}

	private void createTable(SQLiteDatabase db) {
		
		
		String sql = "CREATE TABLE " + ALARMS_TABLE + " ( " + 
				ALARM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
				ALARM_ENABLED + " INTEGER, " +
				ALARM_MESSAGE + " TEXT, " + 
				ALARM_TIME + " TEXT, " +
				ALARM_RECURRING + " LONG, " +
				ALARM_RINGTONE + " TEXT " + 		
				");";		
		db.execSQL(sql);
	}

}