package com.example.phonegetin;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

public class PollingUtils {
	
	public static void startService(Context context, int seconds,Class<?> cls, String action) 
	{
		Log.d("Service", "Strat!!!!!");
		AlarmManager alarm_manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(context,cls);
		intent.setAction(action);
		PendingIntent pendingIntent = PendingIntent.getService(context,0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		long triggerAtTime = SystemClock.elapsedRealtime();
		alarm_manager.setRepeating(AlarmManager.ELAPSED_REALTIME, triggerAtTime,seconds*1000, pendingIntent);
		
	}
	public static void stopService(Context context, Class<?> cls,String action) {
		Log.d("Service:", "Stop!!!!!");
		AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(context, cls);
		intent.setAction(action);
		PendingIntent pendingIntent = PendingIntent.getService(context, 0,intent, PendingIntent.FLAG_UPDATE_CURRENT);
		manager.cancel(pendingIntent);
	}
	
	
}
