package com.example.phonegetin;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {
	final static String BAT_TABLE_NAME = "Battery";
	final static String NET_TABLE_NAME = "Net";
	final static String GPS_TABLE_NAME = "Gps";
	final static String USE_ID = "_id";
	final static String LEVEL = "level";
	final static String Plugged = "plugged";
	final static String RX = "RX";
	final static String TX = "TX";
	final static String TYPE = "type";
	final static String TIME = "time";
	final static String LAT = "lat";
	final static String LONG = "long";
	final static String OPEN_GPS = "opengps";
	final static String FIND_GPS = "findgps";
	final static String GPS_STATUS = "gps_status";
	final static String GPS_STATUS1 = "gps_status1";
	final static String GPS_Percent = "gps_percent";
	final static String GPS_Plugged = "gps_plugged";
	final static String GPS_TYPE = "gps_type";
	final static int DATABASE_VERSION = 1;
	final static String DATABASE_NAME = "Data";

	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.d("DB!!", "DBoncreate");
		// TODO Auto-generated method stub
		String bat = "create table " + BAT_TABLE_NAME + "(" + USE_ID + " text,"
				+ LEVEL + " text, " + TIME + " text, " + Plugged + " text)";
		String net = "create table " + NET_TABLE_NAME + "(" + USE_ID + " text,"
				+ TIME + " text," + RX + " text," + TX + " text," + TYPE
				+ " text)";
		String gps = "create table " + GPS_TABLE_NAME + "(" + USE_ID + " text,"
				+ TIME + " text," + LAT + " text," + LONG + " text," + OPEN_GPS
				+ " text," + FIND_GPS + "  text," + GPS_STATUS + " text,"
				+ GPS_STATUS1 + " text," + GPS_Percent + " text," + GPS_Plugged
				+ " text," + GPS_TYPE + " text)";

		Log.d("DB~~~", "Create");
		db.execSQL(net);
		db.execSQL(bat);
		db.execSQL(gps);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		onCreate(db);
	}

	long bat_add(String uid, String time, String level, String plugged) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues value = new ContentValues();
		value.put(USE_ID, uid);
		value.put(LEVEL, level);
		value.put(TIME, time);
		value.put(Plugged, plugged);
		long result = db.insert(BAT_TABLE_NAME, null, value);
		db.close();
		return result;
	}

	long net_add(String uid, String time, String rx, String tx, String type) {

		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues value = new ContentValues();
		value.put(USE_ID, uid);
		value.put(RX, rx);
		value.put(TX, tx);
		value.put(TYPE, type);
		long result = db.insert(NET_TABLE_NAME, null, value);
		db.close();
		return result;
	}

	long gps_add(String uid, String time, String opengps, String findgps,
			String lat, String Long, String gps_status, String gps_status1,
			String percent, String plugged, String type) {

		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues value = new ContentValues();
		value.put(USE_ID, uid);
		value.put(TIME, time);
		value.put(LAT, lat);
		value.put(LONG, Long);
		value.put(OPEN_GPS, opengps);
		value.put(FIND_GPS, findgps);
		value.put(GPS_STATUS, gps_status);
		value.put(GPS_STATUS1, gps_status1);
		value.put(GPS_Percent, percent);
		value.put(GPS_Plugged, plugged);
		value.put(GPS_TYPE, type);

		long result = db.insert(GPS_TABLE_NAME, null, value);
		db.close();
		return result;
	}

	Cursor get_bat() {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.query(BAT_TABLE_NAME, new String[] { USE_ID, TIME, LEVEL,
				Plugged }, null, null, null, null, null); 
		return c;
	}

	Cursor get_net() {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.query(NET_TABLE_NAME, new String[] { USE_ID, TIME, RX,
				TX, TYPE }, null, null, null, null, null); 
		return c;
	}

	Cursor get_gps() {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.query(GPS_TABLE_NAME, new String[] { USE_ID, TIME, LAT,
				LONG, OPEN_GPS, FIND_GPS, GPS_STATUS, GPS_STATUS1, GPS_Percent,
				GPS_Plugged, GPS_TYPE }, null, null, null, null, null); 
		return c;
	}

	void bat_deleteAll() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(BAT_TABLE_NAME, null, null);
		db.close();

	}

	void net_deleteAll() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(NET_TABLE_NAME, null, null);
		db.close();

	}

	void gps_deleteAll() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(GPS_TABLE_NAME, null, null);
		db.close();

	}

}
