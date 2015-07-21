package com.example.phonegetin;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Criteria;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.provider.CallLog;
import android.telephony.TelephonyManager;
import android.util.Log;

public class My_service extends Service {
	private final static String TAG = "My_service";
	private SharedPreferences data;
	private Context context;
	public static int TYPE_WIFI = 1;
	public static int TYPE_MOBILE = 2;
	public static int TYPE_NOT_CONNECTED = 0;
	private DBHelper db;
	private int conn, netsize, callsize, gps_status, gps_status1;
	private String uid, batunixTimestring, percentstring, ret_battery,
			netrxstring, nettxstring, netunixTimestring, nettype, ret_network;
	private String phNumber, callDuration, dir, ret_call, way, ret_gps;
	private static WakeLock stay_on;
	private int percent, level, scale, percentpast, bat_error, net_error,
			call_error, opengps, findgps, gps_error;
	private int battery_check, net_check, call_check, gps_check, batsize,
			gpssize;
	private static int out;
	private static int get;
	private static int miss;
	private int plugged;
	private long batunixTime, pastrxByte, pasttxByte, netunixTime,
			locationtime, pastlocationtime;
	public double locationx, locationy, pastx, pasty;;
	public List<String[]> batterydataList, networkdataList, calldataList,
			gpsdataList;
	private long rxBytes, txBytes, mStartRX, mStartTX, pastCallDate;
	LocationManager lmanager;
	private String[] projection = { CallLog.Calls.NUMBER, CallLog.Calls.DATE,
			CallLog.Calls.TYPE, CallLog.Calls.DURATION };
	private Handler handler;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		Log.d(TAG, "onCreate");
		context = getApplicationContext();
		data = getSharedPreferences("Data", MODE_PRIVATE);
		acquireWakeLock();

	}

	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG, "onStart");
		SharedPreferences.Editor editor = data.edit();
		editor.putInt("Conn", getConnectivityStatus(context));
		editor.commit();

		new thread().start();
		return START_STICKY;

	}

	class thread extends Thread {
		public void run() {
			service();
		}
	}

	public void service() {
		Log.d(TAG, "service");
		SharedPreferences.Editor editor = data.edit();
		percentstring = getbattery();
		editor.putInt("batlevel", Integer.valueOf(percentstring));
		editor.commit();
		db = new DBHelper(context);
		uid = data.getString("uid", null);
		Log.d(TAG, "Uid" + uid);
		conn = data.getInt("Conn", 0);
		percentpast = data.getInt("level", 0);
		if (percent != 0)
		{
			percentpast = percent;
			editor.putInt("level", percentpast);
			editor.commit();
			if (conn == 1 || conn == 2) {
				Log.d(TAG, "Postbattery");
				Log.d(TAG, "Conn......" + conn);
				post_battery();
				handler = new Handler(Looper.getMainLooper());
				handler.post(new Runnable() {
					public void run() {
						Log.d(TAG, "Error1");
						new postbattery().execute();
					}
				});
			} else if (conn == 0) {
				set_batterydata();
			}
		}

	}

	public void netpost() {
		Log.d(TAG, "NET GO!");
		get_Traffic();
		netrxstring = String.valueOf(rxBytes);
		nettxstring = String.valueOf(txBytes);
		pastrxByte = data.getLong("pastrx", 0);
		pasttxByte = data.getLong("pasttx", 0);
		SharedPreferences.Editor editor = data.edit();
		if (rxBytes != pastrxByte || txBytes != pasttxByte) {

			editor.putLong("pastrx", rxBytes);
			editor.putLong("pasttx", txBytes);

			editor.putLong("rx", rxBytes);
			editor.putLong("tx", txBytes);
			long r = data.getLong("rx", 0);
			long t = data.getLong("tx", 0);
			Log.d(TAG, "RX~~~" + r);
			Log.d(TAG, "TX~~~~" + t);
			editor.commit();

			if (conn == 1 || conn == 2) {
				Log.d(TAG, "Postnetwork");
				Log.d(TAG, "Error2");
				postnetwork postnetwork = new postnetwork();
				postnetwork.execute();

			} else if (conn == 0) {
				set_networkdata();
			}
		}
	}

	public void callpost() {
		Log.d(TAG, "Call GO!");
		get_phonecall();
		if (conn == 1 || conn == 2) {
			Log.d(TAG, "Post call!!!!!");
			new postcall().execute();
			Log.d(TAG, "after post do run");

		}
	}

	public void gpspost() {
		Log.d(TAG, "GPS GO!");
		locationfind();
		getbattery();
		conn = getConnectivityStatus(context);
		if (conn == 1 || conn == 2) {
			Log.d(TAG, "Gps Battery::" + percent);
			Log.d(TAG, "Gps Plugged::" + plugged);
			Log.d(TAG, "Gps Net::" + conn);
			Postgps();
			new postgps().execute();

		} else {
			setgps();
		}
	}

	public static int getConnectivityStatus(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		if (null != activeNetwork) {
			if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
				return TYPE_WIFI;
			if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
				return TYPE_MOBILE;
		}
		return TYPE_NOT_CONNECTED;
	}

	private void acquireWakeLock() {
		Log.d(TAG, "WakeLock");
		if (null == stay_on) {
			PowerManager pm = (PowerManager) context
					.getSystemService(Context.POWER_SERVICE);
			stay_on = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK
					| PowerManager.ON_AFTER_RELEASE, "Service");
			if (null != stay_on) {
				stay_on.acquire();
			}
		}
	}

	public String getbattery() {
		Log.d(TAG, "Get battery");
		Intent batteryIntent = context.getApplicationContext()
				.registerReceiver(null,
						new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
		plugged = batteryIntent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0);
		level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
		scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, 0);
		percent = (level * 100) / scale;
		return String.valueOf(percent);
	}

	public void post_battery() {
		batterydataList = new ArrayList<String[]>();
		batunixTime = System.currentTimeMillis() / 1000L;
		Log.d(TAG, "BatteryPost:NOW Time:" + batunixTime);
		batunixTimestring = String.valueOf(batunixTime);
		SharedPreferences.Editor editor = data.edit();
		editor.putLong("batterytime", batunixTime);
		editor.putInt("plugged", plugged);
		editor.commit();
		if (data.getBoolean("bat_sql", false)) {
			Cursor c = db.get_bat();
			int time = c.getColumnIndex(DBHelper.TIME);
			int level = c.getColumnIndex(DBHelper.LEVEL);
			int plugged = c.getColumnIndex(DBHelper.Plugged);
			batterydataList = new ArrayList<String[]>();
			while (c.moveToNext()) {
				String bat_level = c.getString(level);
				String bat_time = c.getString(time);
				String bat_plugged = c.getString(plugged);
				batterydataList.add(new String[] { uid, bat_time, bat_level,
						bat_plugged });

			}
			batsize = batterydataList.size();
			Log.d(TAG, "Battery Size::" + batsize);
			c.close();
		}
		batterydataList.add(new String[] { uid, batunixTimestring,
				percentstring, String.valueOf(plugged) });
		Log.d(TAG, "Conn Bat Size:::" + batterydataList.size());
		Log.d(TAG, "Connect:" + "post batterylevel");
		Log.d(TAG, "after post do run");
	}

	public void set_batterydata() {
		Log.d(TAG, "setbattery");

		batunixTime = System.currentTimeMillis() / 1000L;
		Log.d(TAG, "Battery NOW Time:" + batunixTime);
		batunixTimestring = String.valueOf(batunixTime);
		SharedPreferences.Editor editor = data.edit();
		editor.putLong("batterytime", batunixTime);
		editor.putInt("plugged", plugged);
		editor.commit();
		long result = db.bat_add(uid, batunixTimestring, percentstring,
				String.valueOf(plugged));
		if (result >= 0) {
			Log.d(TAG, "Battery SQL success!!");
			editor.putBoolean("bat_sql", true);
			editor.commit();
		}

	}

	class postbattery extends AsyncTask<Integer, Integer, String> {

		@Override
		protected String doInBackground(Integer... countTo) {
			// TODO Auto-generated method stub
		

			try {
				Log.d(TAG, "Try Post battery");
				postbattery_function(batterydataList);
				bat_error = 0;
				
			}

			catch (KeyManagementException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (KeyStoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnrecoverableKeyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.d(TAG, "Battery Expection");
				bat_error = 1;
			} finally {

			}
			return "";
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			
			Log.d(TAG, "Battery before post");
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
			
			Log.d(TAG, " posting");
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			if (bat_error == 1) {
				set_batterydata();
				batterydataList.clear();
				Log.d(TAG, "Battery ClearList");
				Log.d(TAG, "Return:" + ret_battery);
				netpost();
			} else {
				if (data.getBoolean("bat_sql", false) && bat_error == 0) {
					db.bat_deleteAll();
					SharedPreferences.Editor editor = data.edit();
					editor.putBoolean("bat_sql", false);
					editor.commit();
					batterydataList.clear();
					Log.d(TAG, "Battery ClearList");
					Log.d(TAG, "Return:" + ret_battery);
				}
				netpost();
			}

		}

		@Override
		protected void onCancelled() {
			// TODO Auto-generated method stub
			super.onCancelled();

		}
	}

	public String postbattery_function(List<String[]> battery)
			throws KeyStoreException, IOException, KeyManagementException,
			NoSuchAlgorithmException, UnrecoverableKeyException {

		Log.d(TAG, "Fuction......");
		String url = "https://nrl.cce.mcu.edu.tw/pgi/getbattery.php";

		InputStream instream = this.getResources().openRawResource(R.raw.syl);
		KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());

		try {
			trustStore.load(instream, null);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			instream.close();
		}

		SSLSocketFactory socketFactory = new SSLSocketFactory(trustStore);
		Scheme sch = new Scheme("https", socketFactory, 443);

		List<NameValuePair> params = new ArrayList<NameValuePair>();

		for (int i = 0; i < battery.size(); i++) {
			Log.d(TAG, "Bayttery!!!!!!!!!");
			params.add(new BasicNameValuePair("uid[]", battery.get(i)[0]));
			params.add(new BasicNameValuePair("batterytime[]",
					battery.get(i)[1]));
			params.add(new BasicNameValuePair("batterylevel[]",
					battery.get(i)[2]));
			params.add(new BasicNameValuePair("batteryplugged[]", battery
					.get(i)[3]));
			Log.d(TAG, "BatteryDATA....." + battery.get(i)[0]
					+ battery.get(i)[1] + battery.get(i)[2] + battery.get(i)[3]);
		}

		HttpClient client = new DefaultHttpClient();
		client.getConnectionManager().getSchemeRegistry().register(sch);
		HttpPost request = new HttpPost(url);

		request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));

		HttpResponse response = client.execute(request);

		ret_battery = EntityUtils.toString(response.getEntity());
		return ret_battery;
	}

	public void get_Traffic() {
		Log.d(TAG, " Conn_Traffic");
		mStartRX = data.getLong("mStartRX", 0);
		mStartTX = data.getLong("mStartTX", 0);
		rxBytes = TrafficStats.getTotalRxBytes() - mStartRX;
		txBytes = TrafficStats.getTotalTxBytes() - mStartTX;
		mStartRX = rxBytes;
		mStartTX = txBytes;
		SharedPreferences.Editor editor = data.edit();
		editor.putLong("mStartRX", mStartRX);
		editor.putLong("mStartTX", mStartTX);
		editor.commit();
		Log.d(TAG, "ServiceRX....." + rxBytes);
		Log.d(TAG, "ServiceTX......." + txBytes);
	}

	public void postnetwork() {
		Log.d(TAG, "postnetwork");
		networkdataList = new ArrayList<String[]>();
		netunixTime = System.currentTimeMillis() / 1000L;
		SharedPreferences.Editor editor = data.edit();
		editor.putLong("nettime", netunixTime);
		editor.commit();
		Log.d(TAG, "NetworkPost:NOW Time:" + netunixTime);
		netunixTimestring = String.valueOf(netunixTime);
		nettype = String.valueOf(conn);
		if (data.getBoolean("net_sql", false)) {
			Cursor c = db.get_net();
			int rx = c.getColumnIndex(DBHelper.RX);
			int tx = c.getColumnIndex(DBHelper.TX);
			int time = c.getColumnIndex(DBHelper.TIME);
			int type = c.getColumnIndex(DBHelper.TYPE);
			while (c.moveToNext()) {
				String net_rx = c.getString(rx);
				String net_tx = c.getString(tx);
				String net_time = c.getString(time);
				String net_type = c.getString(type);
				networkdataList.add(new String[] { uid, net_time, net_type,
						net_rx, net_tx });
			}
			c.close();

		}
		networkdataList.add(new String[] { uid, netunixTimestring, nettype,
				netrxstring, nettxstring });
		netsize = networkdataList.size();
		Log.d(TAG, "Net Size" + netsize);
		Log.d(TAG,
				"Net ListData:" + networkdataList.get(netsize - 1)[0]
						+ networkdataList.get(netsize - 1)[1]
						+ networkdataList.get(netsize - 1)[2]
						+ networkdataList.get(netsize - 1)[3]);
		Log.d(TAG, "Connect:" + "post Net");

		Log.d(TAG, "after post do run");
	}

	public void set_networkdata() {
		Log.d(TAG, "setnetwork");
		netunixTime = System.currentTimeMillis() / 1000L;
		SharedPreferences.Editor editor = data.edit();
		editor.putLong("nettime", netunixTime);
		editor.commit();
		Log.d(TAG, "Network NOW Time:" + netunixTime);
		netunixTimestring = String.valueOf(netunixTime);
		nettype = String.valueOf(conn);
		long result = db.net_add(uid, netunixTimestring, netrxstring,
				nettxstring, nettype);
		if (result >= 0) {
			Log.d(TAG, "Net SQL success!!");
			editor.putBoolean("net_sql", true);
			editor.commit();
		}

	}

	class postnetwork extends AsyncTask<Integer, Integer, String> {
		
		@Override
		protected String doInBackground(Integer... countTo) {
			// TODO Auto-generated method stub
			
			postnetwork();

			try {

				Log.d(TAG, "Try Post NET");
				postnetwork_function(networkdataList);
				net_error = 0;

			}

			catch (KeyManagementException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (KeyStoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnrecoverableKeyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				net_error = 1;
			} finally {

			}
			return "";
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			
			Log.d(TAG, "Network before post");
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
			
			Log.d(TAG, " posting");
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			
			if (net_error == 1) {
				set_networkdata();
				networkdataList.clear();
				callpost();
			} else {
				if (data.getBoolean("net_sql", false) && net_error == 0) {
					db.net_deleteAll();
					SharedPreferences.Editor editor = data.edit();
					editor.putBoolean("net_sql", false);
					editor.commit();
					networkdataList.clear();
					Log.d(TAG, "Network ClearList");
					Log.d(TAG, "Return:" + ret_network);
				}
				callpost();
			}
		}

		@Override
		protected void onCancelled() {
			// TODO Auto-generated method stub
			super.onCancelled();
			
		}
	}

	public String postnetwork_function(List<String[]> network)
			throws KeyStoreException, IOException, KeyManagementException,
			NoSuchAlgorithmException, UnrecoverableKeyException {
		String url = "https://nrl.cce.mcu.edu.tw/pgi/getflow.php";
		InputStream instream = this.getResources().openRawResource(R.raw.syl);

		KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());//
		try {
			trustStore.load(instream, null);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			instream.close();
		}

		SSLSocketFactory socketFactory = new SSLSocketFactory(trustStore);
		Scheme sch = new Scheme("https", socketFactory, 443);

		List<NameValuePair> params1 = new ArrayList<NameValuePair>();

		for (int i = 0; i < network.size(); i++) {
			params1.add(new BasicNameValuePair("uid[]", network.get(i)[0]));
			params1.add(new BasicNameValuePair("nettime[]", network.get(i)[1]));
			params1.add(new BasicNameValuePair("nettype[]", network.get(i)[2]));
			params1.add(new BasicNameValuePair("netrx[]", network.get(i)[3]));
			params1.add(new BasicNameValuePair("nettx[]", network.get(i)[4]));
			Log.d(TAG, "NetDATA....." + network.get(i)[0] + network.get(i)[1]
					+ "!!!!!!!!" + network.get(i)[2] + "!!!!"
					+ network.get(i)[3] + network.get(i)[4]);
		}

		HttpClient client = new DefaultHttpClient();
		client.getConnectionManager().getSchemeRegistry().register(sch);
		HttpPost request = new HttpPost(url);

		request.setEntity(new UrlEncodedFormEntity(params1, HTTP.UTF_8));

		HttpResponse response = client.execute(request);
		ret_network = EntityUtils.toString(response.getEntity());
		return ret_network;
	}

	public void get_phonecall() {
		String subph = null;
		Log.d(TAG, "Get Call~~~~~~~");
		calldataList = new ArrayList<String[]>();
		TelephonyManager telManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String com = telManager.getNetworkOperatorName();
		SharedPreferences.Editor editor = data.edit();
		editor.putString("com", com);
		editor.commit();
		Cursor cursor = context.getContentResolver().query(
				CallLog.Calls.CONTENT_URI, projection, null, null,
				CallLog.Calls.DATE + " DESC");
		int number = cursor.getColumnIndex(CallLog.Calls.NUMBER);
		int type = cursor.getColumnIndex(CallLog.Calls.TYPE);
		int date = cursor.getColumnIndex(CallLog.Calls.DATE);
		int duration = cursor.getColumnIndex(CallLog.Calls.DURATION);
		if (cursor.moveToFirst()) {
			String callDate = cursor.getString(date);
			long firstcallDate = Long.valueOf(callDate);
			Log.d(TAG, "First Date!!!!!" + firstcallDate);
			for (int i = 0; i < 5; i++) {
				Log.d(TAG, "For~~~~~");
				if (cursor.moveToPosition(i)) {
					phNumber = cursor.getString(number);
					Log.d(TAG, "PhoneNumber:" + phNumber);
					subph = (String) phNumber.subSequence(0, 4);
					Log.d(TAG, "Subph:" + subph);
					String callType = cursor.getString(type);
					callDate = cursor.getString(date);
					long thiscalldate = Long.valueOf(callDate);
					Log.d(TAG, "CallDate" + callDate);
					callDuration = cursor.getString(duration);
					dir = null;
					int dircode = Integer.parseInt(callType);
					switch (dircode) {
					case CallLog.Calls.OUTGOING_TYPE:
						dir = "OUTGOING";

						break;
					case CallLog.Calls.INCOMING_TYPE:
						dir = "INCOMING";

						break;
					case CallLog.Calls.MISSED_TYPE:
						dir = "MISSED";

						break;
					}

					pastCallDate = data.getLong("pastcalltime", 0);
					if (thiscalldate != pastCallDate) {
						Log.d(TAG, "Not Same~~~~");
						if (dir.equals("OUTGOING")) {
							out += 1;

						} else if (dir.equals("INCOMING")) {
							get += 1;
						} else if (dir.equals("MISSED")) {
							miss += 1;
						}
						long calldate = Long.valueOf(callDate);
						calldate = calldate / 1000L;
						callDate = String.valueOf(calldate);
						calldataList.add(new String[] { uid, subph, dir,
								callDuration, callDate, com });
						callsize = calldataList.size();
						Log.d(TAG, "CallSize:" + callsize);
						Log.d(TAG,
								"call ListData:"
										+ calldataList.get(callsize - 1)[0]
										+ calldataList.get(callsize - 1)[1]
										+ calldataList.get(callsize - 1)[2]
										+ "!!!!"
										+ calldataList.get(callsize - 1)[3]
										+ calldataList.get(callsize - 1)[4]
										+ "Com~~~~~"
										+ calldataList.get(callsize - 1)[5]);

					} else {
						Log.d(TAG, "Same!!!!!!!!");
						break;
					}
					if (i == 0) {
						pastCallDate = firstcallDate;
						Log.d(TAG, "LastCall~~~~" + pastCallDate);
						editor.putLong("pastcalltime", pastCallDate);
						editor.commit();
					}

				}

			}
		}

		editor.putInt("miss", miss);
		editor.putInt("get", get);
		editor.putInt("out", out);
		Log.d(TAG, "miss~~~~" + miss + "get~~~~" + get + "out~~~" + out);
		editor.commit();
		cursor.close();
		;
	}

	class postcall extends AsyncTask<Integer, Integer, String> {

		@Override
		protected String doInBackground(Integer... countTo) {
			// TODO Auto-generated method stub

			try {

				Log.d(TAG, "Try Post call");
				postcall_function(calldataList);
			}

			catch (KeyManagementException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (KeyStoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnrecoverableKeyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				call_error = 1;
			} finally {

			}
			return "";
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			
			Log.d(TAG, "call before post");
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
			
			Log.d(TAG, " posting");
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			if (call_error == 1) {
				SharedPreferences.Editor editor = data.edit();
				editor.putLong("pastcalltime", 0);
				editor.commit();
				calldataList.clear();
				Log.d(TAG, "call Eorror");
				Log.d(TAG, "Call Return:" + ret_call);
				My_service.miss = 0;
				My_service.out = 0;
				My_service.get = 0;
				gpspost();
			} else {
				calldataList.clear();
				Log.d(TAG, "call ClearList");
				Log.d(TAG, "Call Return:" + ret_call);
				My_service.miss = 0;
				My_service.out = 0;
				My_service.get = 0;
				gpspost();
			}
		}

		@Override
		protected void onCancelled() {
			// TODO Auto-generated method stub
			super.onCancelled();

		}
	}

	public String postcall_function(List<String[]> call)
			throws KeyStoreException, IOException, KeyManagementException,
			NoSuchAlgorithmException, UnrecoverableKeyException {
		Log.d(TAG, "call Fuction......");
		String url = "https://nrl.cce.mcu.edu.tw/pgi/calltime.php";
		InputStream instream = this.getResources().openRawResource(R.raw.syl);
		KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());//
		try {
			trustStore.load(instream, null);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			instream.close();
		}

		SSLSocketFactory socketFactory = new SSLSocketFactory(trustStore);
		Scheme sch = new Scheme("https", socketFactory, 443);

		List<NameValuePair> params = new ArrayList<NameValuePair>();

		for (int i = 0; i < call.size(); i++) {
			Log.d(TAG, "Call!!!!!!!!!");
			params.add(new BasicNameValuePair("uid[]", call.get(i)[0]));
			params.add(new BasicNameValuePair("callnumber[]", call.get(i)[1]));
			params.add(new BasicNameValuePair("calltype[]", call.get(i)[2]));
			params.add(new BasicNameValuePair("calltime[]", call.get(i)[3]));
			params.add(new BasicNameValuePair("starttime[]", call.get(i)[4]));
			params.add(new BasicNameValuePair("com[]", call.get(i)[5]));
			Log.d(TAG,
					"CallDATA....." + call.get(i)[0] + call.get(i)[1]
							+ call.get(i)[2] + "~~" + call.get(i)[3] + "~~"
							+ call.get(i)[4] + call.get(i)[5]);
		}

		HttpClient client = new DefaultHttpClient();
		client.getConnectionManager().getSchemeRegistry().register(sch);
		HttpPost request = new HttpPost(url);
		request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));

		HttpResponse response = client.execute(request);

		ret_call = EntityUtils.toString(response.getEntity());
		return ret_call;
	}

	public void locationfind() {
		lmanager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		Boolean gps;
		int old = data.getInt("old", 1);
		int gpsconn = data.getInt("Conn", 0);
		int enablenetwork = 0;
		if (gpsconn == 1
				&& lmanager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
			enablenetwork = 1;
			Criteria c = new Criteria();
			c.setAccuracy(c.ACCURACY_FINE);
			c.setPowerRequirement(c.POWER_LOW);
			c.setAltitudeRequired(false);
			c.setBearingRequired(false);
			c.setSpeedRequired(false);
			way = LocationManager.NETWORK_PROVIDER;
			Log.d(TAG, "Provider::" + way);
			opengps = 1;
			Log.d(TAG, "Do Find Location");
			int once;
			once = data.getInt("listen", 0);
			if (once == 0) {
				Log.d(TAG, "Once");
				Location location = lmanager.getLastKnownLocation(way);
				updateWithNewLocation(location);
				lmanager.addGpsStatusListener(locationListener);
				lmanager.requestLocationUpdates(way, 0, 0, gpslocationListener);
				SharedPreferences.Editor editor = data.edit();
				editor.putInt("listen", 1);
				editor.putInt("old", 1);
				editor.commit();
			} else {
				if (old == 1) {
					Log.d(TAG, "old Location");
					Location location = lmanager.getLastKnownLocation(way);
					updateWithNewLocation(location);
				}
			}

		}

		if (enablenetwork == 0
				&& lmanager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			Log.d(TAG, "Have Provider");
			Criteria c = new Criteria();
			c.setAccuracy(c.ACCURACY_FINE);
			c.setPowerRequirement(c.POWER_LOW);
			c.setAltitudeRequired(false);
			c.setBearingRequired(false);
			c.setSpeedRequired(false);
			way = LocationManager.GPS_PROVIDER;
			Log.d(TAG, "Provider::" + way);
			opengps = 1;
			Log.d(TAG, "Do Find Location");
			int once;
			once = data.getInt("listen", 0);
			if (once == 0) {
				Log.d(TAG, "Once");
				Location location = lmanager.getLastKnownLocation(way);
				updateWithNewLocation(location);
				lmanager.addGpsStatusListener(locationListener);
				lmanager.requestLocationUpdates(way, 0, 0, gpslocationListener);
				SharedPreferences.Editor editor = data.edit();
				editor.putInt("listen", 1);
				editor.putInt("old", 1);
				editor.commit();
			} else {
				if (old == 1) {
					Log.d(TAG, "old Location");
					Location location = lmanager.getLastKnownLocation(way);
					updateWithNewLocation(location);
				}

			}

		} else if (!lmanager
				.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
				&& !lmanager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			Log.d(TAG, "No Provider");
			opengps = 0;
		}

	}

	public void setgps() {
		String x = data.getString("pastx", "0");
		String y = data.getString("pasty", "0");
		String xx = data.getString("latitude", "0");
		String yy = data.getString("longitude", "0");
		if (x.length() >= 6) {
			x = x.substring(0, x.indexOf(".") + 5);
			Log.d(TAG, "subx::" + x);
		}
		if (y.length() >= 6) {
			y = y.substring(0, y.indexOf(".") + 5);
			Log.d(TAG, "suby::" + y);
		}
		if (xx.length() >= 6) {
			xx = xx.substring(0, xx.indexOf(".") + 5);
			Log.d(TAG, "subxx::" + xx);
		}
		if (yy.length() >= 6) {
			yy = yy.substring(0, yy.indexOf(".") + 5);
			Log.d(TAG, "sunyy::" + yy);
		}
		if (!x.equals(xx) || !y.equals(yy)) {
			Log.d(TAG, "Gps Not Same");
			SharedPreferences.Editor editor = data.edit();
			editor.putInt("opengps", opengps);
			editor.putInt("gps_status", gps_status);
			locationx = Double.valueOf(data.getString("latitude", "0"));
			locationy = Double.valueOf(data.getString("longitude", "0"));
			locationtime = data.getLong("gpstime", 0);
			long result = db.gps_add(uid, String.valueOf(locationtime),
					String.valueOf(opengps), String.valueOf(findgps),
					String.valueOf(locationx), String.valueOf(locationy),
					String.valueOf(gps_status), String.valueOf(gps_status1),
					String.valueOf(percent), String.valueOf(plugged),
					String.valueOf(conn));
			if (result >= 0) {
				Log.d(TAG, "GPS SQL success!!");
				editor.putBoolean("gps_sql", true);
				editor.commit();
			}

			pastx = locationx;
			pasty = locationy;
			editor.putString("pastx", String.valueOf(pastx));
			editor.putString("pasty", String.valueOf(pasty));
			editor.commit();
		} else {
			Log.d(TAG, "Gps Same");
			SharedPreferences.Editor editor = data.edit();
			editor.putString("pastx", String.valueOf(x));
			editor.putString("pasty", String.valueOf(y));
			editor.commit();

		}
	}

	public void Postgps() {
		Log.d(TAG, "POST GPS");
		String x = data.getString("pastx", "0");
		String y = data.getString("pasty", "0");
		String xx = data.getString("latitude", "0");
		String yy = data.getString("longitude", "0");
		uid = data.getString("uid", null);
		Log.d(TAG, "Gps Uid:" + uid);
		gpsdataList = new ArrayList<String[]>();
		if (x.length() >= 6) {
			x = x.substring(0, x.indexOf(".") + 5);
			Log.d(TAG, "subx::" + x);
		}
		if (y.length() >= 6) {
			y = y.substring(0, y.indexOf(".") + 5);
			Log.d(TAG, "suby::" + y);
		}
		if (xx.length() >= 6) {
			xx = xx.substring(0, xx.indexOf(".") + 5);
			Log.d(TAG, "subxx::" + xx);
		}
		if (yy.length() >= 6) {
			yy = yy.substring(0, yy.indexOf(".") + 5);
			Log.d(TAG, "sunyy::" + yy);
		}
		SharedPreferences.Editor editor = data.edit();
		editor.putInt("opengps", opengps);
		editor.putInt("gps_status", gps_status);
		locationx = Double.valueOf(data.getString("latitude", "0"));
		locationy = Double.valueOf(data.getString("longitude", "0"));
		locationtime = data.getLong("gpstime", 0);
		if (data.getBoolean("gps_sql", false)) {
			Log.d(TAG, "Boolean" + data.getBoolean("gps_sql", false));
			Cursor c = db.get_gps();
			int time = c.getColumnIndex(DBHelper.TIME);
			int open = c.getColumnIndex(DBHelper.OPEN_GPS);
			int find = c.getColumnIndex(DBHelper.FIND_GPS);
			int gps_stat = c.getColumnIndex(DBHelper.GPS_STATUS);
			int gps_stat1 = c.getColumnIndex(DBHelper.GPS_STATUS1);
			int glat = c.getColumnIndex(DBHelper.LAT);
			Log.d(TAG, "LATint" + glat);
			int glong = c.getColumnIndex(DBHelper.LONG);
			Log.d(TAG, "LONGint" + glong);
			int glevel = c.getColumnIndex(DBHelper.GPS_Percent);
			int gplugged = c.getColumnIndex(DBHelper.GPS_Plugged);
			int gtype = c.getColumnIndex(DBHelper.GPS_TYPE);
			while (c.moveToNext()) {
				String gps_time = c.getString(time);
				String gps_find = c.getString(find);
				String gps_open = c.getString(open);
				String stat = c.getString(gps_stat);
				String stat1 = c.getString(gps_stat1);
				String gps_lat = c.getString(glat);
				String gps_long = c.getString(glong);
				String gps_level = c.getString(glevel);
				String gps_plugged = c.getString(gplugged);
				String gps_type = c.getString(gtype);

				gpsdataList.add(new String[] { uid, gps_open, gps_find,
						gps_lat, gps_long, gps_time, stat, stat1, gps_level,
						gps_plugged, gps_type });

			}
			c.close();
		}

		gpsdataList.add(new String[] { uid, String.valueOf(opengps),
				String.valueOf(findgps), String.valueOf(locationx),
				String.valueOf(locationy), String.valueOf(locationtime),
				String.valueOf(gps_status), String.valueOf(gps_status1),
				String.valueOf(percent), String.valueOf(plugged),
				String.valueOf(conn) });
		gpssize = gpsdataList.size();
		Log.d(TAG, "Size:" + gpssize);
		Log.d(TAG,
				"GPS ListData:" + gpsdataList.get(gpssize - 1)[0] + "~~"
						+ gpsdataList.get(gpssize - 1)[1] + "~~"
						+ gpsdataList.get(gpssize - 1)[2] + "~~"
						+ gpsdataList.get(gpssize - 1)[3] + "~~"
						+ gpsdataList.get(gpssize - 1)[4] + "~~"
						+ gpsdataList.get(gpssize - 1)[5] + "~~"
						+ gpsdataList.get(gpssize - 1)[6] + "~~"
						+ gpsdataList.get(gpssize - 1)[7] + "~~"
						+ gpsdataList.get(gpssize - 1)[8] + "~~"
						+ gpsdataList.get(gpssize - 1)[9] + "~~"
						+ gpsdataList.get(gpssize - 1)[10]);
		Log.d(TAG, "Connect:" + "post gps location");
		pastx = locationx;
		pasty = locationy;

	}

	private void updateWithNewLocation(Location Location) {
		if (Location != null) {
			Log.d(TAG, "location can find");
			findgps = 1;
			locationx = Location.getLatitude();
			locationy = Location.getLongitude();
			locationtime = Location.getTime() / 1000L;
			SharedPreferences.Editor editor = data.edit();
			editor.putString("longitude", String.valueOf(locationy));
			editor.putString("latitude", String.valueOf(locationx));
			editor.putLong("gpstime", locationtime);
			editor.commit();
			Log.d(TAG, "XX:" + locationx);
			Log.d(TAG, "YY::" + locationy);

		} else {
			Log.d(TAG, "location can not find");
			findgps = 0;
			SharedPreferences.Editor editor = data.edit();
			editor.putString("longitude", "0");
			editor.putString("latitude", "0");
			long now = System.currentTimeMillis();
			editor.putLong("gpstime", now);
			editor.commit();

		}

	}

	LocationListener gpslocationListener = new LocationListener() {

		public void onLocationChanged(Location location) {
			Log.d(TAG, "GPS Location update!!!");
			SharedPreferences.Editor editor = data.edit();
			editor.putInt("old", 0);
			editor.commit();
			updateWithNewLocation(location);
		}

		public void onProviderDisabled(String provider) {
			Log.d(TAG, "no find");
			updateWithNewLocation(null);
		}

		public void onProviderEnabled(String provider) {

		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
			switch (status) {
			case LocationProvider.OUT_OF_SERVICE:
				gps_status1 = 0;
				break;
			case LocationProvider.TEMPORARILY_UNAVAILABLE:
				gps_status1 = 1;
				break;
			case LocationProvider.AVAILABLE:
				gps_status1 = 2;
				break;
			}
		}
	};
	GpsStatus.Listener locationListener = new GpsStatus.Listener() {

		@Override
		public void onGpsStatusChanged(int event) {
			// TODO Auto-generated method stub
			switch (event) {
			case GpsStatus.GPS_EVENT_STARTED:
				gps_status = 1;
				break;
			case GpsStatus.GPS_EVENT_STOPPED:
				gps_status = 2;
				break;
			case GpsStatus.GPS_EVENT_FIRST_FIX:
				gps_status = 3;
				break;
			case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
				gps_status = 3;
				break;
			}
		}
	};

	class postgps extends AsyncTask<Integer, Integer, String> {

		@Override
		protected String doInBackground(Integer... countTo) {
			// TODO Auto-generated method stub

			try {

				Log.d(TAG, "Try Post GPS");
				Log.d(TAG, "DATASize::" + gpsdataList.size());
				postgps_function(gpsdataList);
				gps_error = 0;
			}

			catch (KeyManagementException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (KeyStoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnrecoverableKeyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				gps_error = 1;
			} finally {

			}
			return "";
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			
			Log.d(TAG, "gps before post");
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
			
			Log.d(TAG, " posting");
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if (gps_error == 1) {
				setgps();
			}
			if (data.getBoolean("gps_sql", false) && gps_error == 0) {
				db.gps_deleteAll();
				SharedPreferences.Editor editor = data.edit();
				editor.putBoolean("gps_sql", false);
				editor.commit();
			}
			gpsdataList.clear();
			SharedPreferences.Editor editor = data.edit();
			editor.putLong("pastlocationtime", pastlocationtime);
			editor.commit();
			Log.d(TAG, "gps ClearList");
			Log.d(TAG, "gps Return:" + ret_gps);

		}

		@Override
		protected void onCancelled() {
			// TODO Auto-generated method stub
			super.onCancelled();

		}
	}

	public String postgps_function(List<String[]> gps)
			throws KeyStoreException, IOException, KeyManagementException,
			NoSuchAlgorithmException, UnrecoverableKeyException {

		Log.d(TAG, "GPS Fuction......");
		String url = "https://nrl.cce.mcu.edu.tw/pgi/gps.php";
		InputStream instream = this.getResources().openRawResource(R.raw.syl);
		KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
		try {
			trustStore.load(instream, null);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			instream.close();
		}

		SSLSocketFactory socketFactory = new SSLSocketFactory(trustStore);
		Scheme sch = new Scheme("https", socketFactory, 443);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		Log.d(TAG, "Size::::" + gps.size());
		for (int i = 0; i < gps.size(); i++) {
			Log.d(TAG, "GPS!!!!!!!!!");
			long past = data.getLong("pastlocationtime", 0);
			Log.d(TAG, "Past::" + past);
			Log.d(TAG, "NOW::" + gps.get(i)[5]);
			if (!gps.get(i)[5].equals(String.valueOf(past))) {
				params.add(new BasicNameValuePair("uid[]", gps.get(i)[0]));
				params.add(new BasicNameValuePair("opengps[]", gps.get(i)[1]));
				params.add(new BasicNameValuePair("findgps[]", gps.get(i)[2]));
				params.add(new BasicNameValuePair("locationx[]", gps.get(i)[3]));
				params.add(new BasicNameValuePair("locationy[]", gps.get(i)[4]));
				params.add(new BasicNameValuePair("time[]", gps.get(i)[5]));
				params.add(new BasicNameValuePair("status[]", gps.get(i)[6]));
				params.add(new BasicNameValuePair("status1[]", gps.get(i)[7]));
				params.add(new BasicNameValuePair("level[]", gps.get(i)[8]));
				params.add(new BasicNameValuePair("plugged[]", gps.get(i)[9]));
				params.add(new BasicNameValuePair("type[]", gps.get(i)[10]));
				Log.d(TAG,
						"GPSDATA....." + gps.get(i)[0] + "~~" + gps.get(i)[1]
								+ "~~" + gps.get(i)[2] + "~~" + gps.get(i)[3]
								+ "~~" + gps.get(i)[4] + "~~" + gps.get(i)[5]
								+ "~~" + gps.get(i)[6] + "~~" + gps.get(i)[7]
								+ "~~" + gps.get(i)[8] + "~~" + gps.get(i)[9]
								+ "~~" + gps.get(i)[10]);
			}
			if (i == gps.size() - 1) {
				pastlocationtime = Long.valueOf(gps.get(i)[5]);
			}
		}

		HttpClient client = new DefaultHttpClient();

		client.getConnectionManager().getSchemeRegistry().register(sch);

		HttpPost request = new HttpPost(url);

		request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));

		HttpResponse response = client.execute(request);

		ret_gps = EntityUtils.toString(response.getEntity());
		return ret_gps;
	}
}
