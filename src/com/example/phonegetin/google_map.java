package com.example.phonegetin;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpEntity;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public class google_map extends Activity {
	private GoogleMap map;
	private static String TAG = "google_map";
	private SharedPreferences share;
	private String ret_data;
	private double[] lat, Longy;
	private int[] bat;
	private int[] type;
	private long[] time;
	private int arraysize;
	private static int error;
	private ProgressDialog dialog;
	private ActionBar actionbar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.google_map);
		actionbar = getActionBar();
		actionbar.setDisplayHomeAsUpEnabled(true);
		share = getSharedPreferences("Data", MODE_PRIVATE);
		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
				.getMap();
		dialog = new ProgressDialog(this);
		dialog.setTitle("Download");
		dialog.setMessage("loading...");
		dialog.show();
		postrequest req = new postrequest();
		req.execute();

	}

	public void mark() {

		LatLng lastlocation = null;
		for (int i = 0; i < arraysize; i++) {
			LatLng location = new LatLng(lat[i], Longy[i]);
			Date date = new Date(time[i]);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy MMMM d a h:mm ");
			String formattedDate = sdf.format(date);
			int t = type[i];
			if (t == 1) {
				Marker mark = map.addMarker(new MarkerOptions()
						.position(location)
						.title("Arrival time")
						.snippet("Battery:" + bat[i] + " " + formattedDate)
						.icon(BitmapDescriptorFactory
								.fromResource(R.drawable.ww)));
			}
			if (t == 2) {
				Marker mark = map.addMarker(new MarkerOptions()
						.position(location)
						.title("Arrival time")
						.snippet("Battery:" + bat[i] + " " + formattedDate)
						.icon(BitmapDescriptorFactory
								.fromResource(R.drawable.gg)));
			}
		}
		lastlocation = new LatLng(lat[0], Longy[0]);
		CameraUpdate update = CameraUpdateFactory.newLatLngZoom(lastlocation,
				12);
		map.animateCamera(update);

	}

	class postrequest extends AsyncTask<Integer, Integer, String> {
		@Override
		protected String doInBackground(Integer... countTo) {
			// TODO Auto-generated method stub

			try {
				String req = share.getString("uid", null);
				gpsreq_function(req);
				error = 0;
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
				error = 1;
			} finally {

			}
			return "";
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			Log.d(TAG, "before post");
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
			Log.d(TAG, "after post");
			Log.d(TAG, "Return::" + ret_data);
			dialog.cancel();
			if (error == 1) {
				Builder HelpDialog = new AlertDialog.Builder(google_map.this);
				HelpDialog.setTitle("Message");
				HelpDialog.setMessage("Please Check your Internet");
				DialogInterface.OnClickListener OnClick = new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
					}
				};
				DialogInterface.OnClickListener OnClick1 = new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent();
						intent.setClass(google_map.this, homepager.class);
						startActivity(intent);
					}
				};
				HelpDialog.setNeutralButton("Go to Check", OnClick);
				HelpDialog.setPositiveButton("Later", OnClick1);
				HelpDialog.show();
			}
			if (ret_data != null) {
				try {
					JSONObject js = new JSONObject(ret_data);
					JSONArray latArray = js.getJSONArray("lat");
					JSONArray longArray = js.getJSONArray("long");
					JSONArray timeArray = js.getJSONArray("time");
					JSONArray batArray = js.getJSONArray("level");
					JSONArray typeArray = js.getJSONArray("type");
					arraysize = timeArray.length();
					lat = new double[arraysize];
					Longy = new double[arraysize];
					time = new long[arraysize];
					bat = new int[arraysize];
					type = new int[arraysize];
					for (int i = 0; i < arraysize; i++) {
						String latx = String.valueOf(latArray.get(i));
						String longy = String.valueOf(longArray.get(i));
						String gtime = timeArray.get(i).toString();
						int battery = Integer.valueOf(batArray.get(i)
								.toString());
						int typ = Integer.valueOf(typeArray.get(i).toString());
						;
						long gptime = Long.valueOf(gtime);
						gptime = gptime * 1000;
						lat[i] = Double.valueOf(latx);
						Longy[i] = Double.valueOf(longy);
						time[i] = gptime;
						bat[i] = battery;
						type[i] = typ;
					}
					Log.d(TAG, "latarray:" + lat);
					Log.d(TAG, "longarray:" + Longy);
					Log.d(TAG, "timearray:" + time);
					int la = lat.length;
					int lon = Longy.length;
					int tim = time.length;
					if (la > 0 && lon > 0 && tim > 0) {
						mark();
					} else {
						Builder HelpDialog = new AlertDialog.Builder(
								google_map.this);
						HelpDialog.setTitle("Message");
						HelpDialog.setMessage("NO Data");
						DialogInterface.OnClickListener OnClick = new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								Intent intent = new Intent();
								intent.setClass(google_map.this,
										homepager.class);
								startActivity(intent);
							}
						};
						HelpDialog.setNeutralButton("OK", OnClick);
						HelpDialog.show();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}

		@Override
		protected void onCancelled() {
			// TODO Auto-generated method stub
			super.onCancelled();
		}
	}

	public String gpsreq_function(String req) throws KeyStoreException,
			IOException, KeyManagementException, NoSuchAlgorithmException,
			UnrecoverableKeyException {
		Log.d(TAG, "Fuction!!!!!");
		String url = "https://nrl.cce.mcu.edu.tw/pgi/admin/mapchart.php";
		InputStream instream = this.getResources().openRawResource(R.raw.syl);// ������

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
		Log.d(TAG, "DATA.........");
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("uid", req));

		HttpClient client = new DefaultHttpClient();
		client.getConnectionManager().getSchemeRegistry().register(sch);
		HttpPost request = new HttpPost(url);
		request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));

		HttpResponse response = client.execute(request);
		HttpEntity resEntity = response.getEntity();
		ret_data = EntityUtils.toString(resEntity);
		return ret_data;
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		Log.d(TAG, "Action Bar" + item.getItemId());
		Log.d(TAG, "Action Bar Back" + R.id.arrow);
		switch (item.getItemId()) {
		case 16908332: {
			Log.d(TAG, "Actionbar back");
			Intent intent = new Intent();
			intent.setClass(this, homepager.class);
			startActivity(intent);
			overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_left);
			google_map.this.finish();
			return true;
		}
		default:
			return super.onOptionsItemSelected(item);
		}
	}

}
