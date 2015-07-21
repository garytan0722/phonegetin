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
import java.util.HashMap;
import java.util.Iterator;
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

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.appcompat.R.string;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class setting extends Activity implements OnItemSelectedListener,
		OnClickListener {
	private static String TAG = "setting";
	private Intent fb_intent, google_intent, setting_intent, home_intent,
			home_batteryIntent, detail_intent, annintent, about_intent;
	private DrawerLayout mDrawerLayout;
	private TypedArray mMenuIcons;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	private CharSequence mDrawerTitle, mTitle;
	private String[] mitemTitles;
	private ArrayList<NavDrawerItem> mDrawerItems;
	private NavDrawerListAdapter adapter;
	private Spinner netspinner_min, sizespinner_min, timespinner_min, batsp,
			netsp, callsp, gpsp;
	private List netlistmin, sizelistmin, timelistmin, batlist, netlist,
			callist, gpslist;
	private ArrayAdapter<string> netAdaptermin, sizeAdaptermin, timeAdaptermin,
			batap, netap, callap, gpsap;
	private int netmin, sizemin, timemin, bat, net, call, gps;
	private Typeface title, subtitle;
	private TextView maintitle, nettitle, sizetitle, timetitle;
	private Button netupload, sizeupload, memupload;
	private SharedPreferences data;
	public static ArrayList<HashMap<String, Object>> netdata, sizedata,
			memdata;
	private String ret_net, ret_size, ret_mem, uid;
	public static final String Battery_ACTION = "Battery";
	public static final String Net_ACTION = "Net";
	public static final String Call_ACTION = "Call";
	public static final String Gps_ACTION = "Gps";

	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting);
		data = getSharedPreferences("Data", MODE_PRIVATE);
		uid = data.getString("uid", null);
		textstyle();
		timeadapter();
		netadapter();
		sizeadapter();
		initialization();
		upload();

	}

	public void upload() {
		netupload = (Button) findViewById(R.id.netupload);
		sizeupload = (Button) findViewById(R.id.sizeupload);
		memupload = (Button) findViewById(R.id.memupload);
		memupload.setOnClickListener(this);
		sizeupload.setOnClickListener(this);
		netupload.setOnClickListener(this);
	}

	public void textstyle() {

		subtitle = Typeface.createFromAsset(getAssets(),
				"Bachelor Pad Expanded JL Italic.ttf");
		maintitle = (TextView) findViewById(R.id.title);
		nettitle = (TextView) findViewById(R.id.nettitle);
		sizetitle = (TextView) findViewById(R.id.sizetitle);
		timetitle = (TextView) findViewById(R.id.timetitle);
		nettitle.setTypeface(subtitle);
		sizetitle.setTypeface(subtitle);
		timetitle.setTypeface(subtitle);
	}

	public void timeadapter() {
		timespinner_min = (Spinner) findViewById(R.id.timespinner_min);
		timeaddlist();
		timeAdaptermin = new ArrayAdapter<string>(this,
				android.R.layout.simple_spinner_item, timelistmin);
		timeAdaptermin
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		timespinner_min.setAdapter(timeAdaptermin);
		timespinner_min.setSelection(value.showtimeposition());
		timespinner_min.setOnItemSelectedListener(this);

	}

	public void sizeadapter() {
		sizespinner_min = (Spinner) findViewById(R.id.sizespinner_min);
		sizeaddlist();
		sizeAdaptermin = new ArrayAdapter<string>(this,
				android.R.layout.simple_spinner_item, sizelistmin);
		sizeAdaptermin
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sizespinner_min.setAdapter(sizeAdaptermin);
		sizespinner_min.setSelection(value.showsizeposition());
		sizespinner_min.setOnItemSelectedListener(this);
	}

	public void netadapter() {

		netspinner_min = (Spinner) findViewById(R.id.netspinner_min);
		netaddlist();
		netAdaptermin = new ArrayAdapter<string>(this,
				android.R.layout.simple_spinner_item, netlistmin);
		netAdaptermin
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		netspinner_min.setAdapter(netAdaptermin);
		netspinner_min.setSelection(value.shownetposition());
		netspinner_min.setOnItemSelectedListener(this);

	}

	public void timeaddlist() {
		timelistmin = new ArrayList();
		timelistmin.add("Show All");
		timelistmin.add("one month ago");
		timelistmin.add("Three months ago");
		timelistmin.add("half-year ago");
		timelistmin.add("One year ago");
	}

	public void sizeaddlist() {

		sizelistmin = new ArrayList();
		sizelistmin.add("Show All");
		sizelistmin.add("500KB");
		sizelistmin.add("800KB");
		sizelistmin.add("1MB");
		sizelistmin.add("5MB");
		sizelistmin.add("10MB");

		Log.d(TAG, "MinList");
	}

	public void netaddlist() {

		netlistmin = new ArrayList();
		netlistmin.add("Show All");
		netlistmin.add("500KB");
		netlistmin.add("800KB");
		netlistmin.add("1MB");
		netlistmin.add("5MB");
		netlistmin.add("10MB");

		Log.d(TAG, "MinList");
	}

	public void initialization() {
		mTitle = mDrawerTitle = getTitle();
		mMenuIcons = getResources().obtainTypedArray(R.array.icons_array);
		mitemTitles = getResources().getStringArray(R.array.items_array);
		setTitle(mitemTitles[value.showposition()]);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);

		mDrawerItems = new ArrayList<NavDrawerItem>();
		mDrawerItems.add(new NavDrawerItem(mitemTitles[0], mMenuIcons
				.getResourceId(0, -1)));

		mDrawerItems.add(new NavDrawerItem(mitemTitles[1], mMenuIcons
				.getResourceId(1, -1)));

		mDrawerItems.add(new NavDrawerItem(mitemTitles[2], mMenuIcons
				.getResourceId(2, -1)));

		mDrawerItems.add(new NavDrawerItem(mitemTitles[3], mMenuIcons
				.getResourceId(3, -1)));

		mDrawerItems.add(new NavDrawerItem(mitemTitles[4], mMenuIcons
				.getResourceId(4, -1)));

		mDrawerItems.add(new NavDrawerItem(mitemTitles[5], mMenuIcons
				.getResourceId(5, -1)));

		mMenuIcons.recycle();
		adapter = new NavDrawerListAdapter(getApplicationContext(),
				mDrawerItems);

		mDrawerList.setAdapter(adapter);
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
		mDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
		mDrawerLayout, /* DrawerLayout object */
		R.drawable.ic_drawer /* nav drawer image to replace 'Up' caret */, /*
																		 * nav
																		 * drawer
																		 * image
																		 * to
																		 * replace
																		 * 'Up'
																		 * caret
																		 */
		R.string.drawer_open, /* "open drawer" description for accessibility */
		R.string.drawer_close /* "close drawer" description for accessibility */
		) {

			public void onDrawerClosed(View view) {
				getActionBar().setTitle(mTitle);
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(mDrawerTitle);
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);
	}

	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			selectItem(position);
		}
	}

	private void selectItem(int position) {
		Log.d(TAG, "Postion:" + position);
		value.setPosition(position);

		switch (position)

		{
		case 0:
			mDrawerList.setItemChecked(value.showposition(), true);
			mDrawerList.setSelection(value.showposition());
			mDrawerLayout.closeDrawer(mDrawerList);
			home_intent = new Intent();
			home_intent.setClass(this, homepager.class);
			startActivity(home_intent);
			overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_left);
			finish();
			break;

		case 1:
			mDrawerList.setItemChecked(value.showposition(), true);
			mDrawerList.setSelection(value.showposition());
			mDrawerLayout.closeDrawer(mDrawerList);
			annintent = new Intent();
			annintent.setClass(this, Announcement.class);
			startActivity(annintent);
			overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_left);
			finish();
			break;

		case 2:
			mDrawerList.setItemChecked(value.showposition(), true);
			mDrawerList.setSelection(value.showposition());
			mDrawerLayout.closeDrawer(mDrawerList);
			detail_intent = new Intent();
			detail_intent.setClass(this, Detail.class);
			startActivity(detail_intent);
			overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_left);
			finish();
			break;
		case 3:
			mDrawerList.setItemChecked(value.showposition(), true);
			mDrawerList.setSelection(value.showposition());
			mDrawerLayout.closeDrawer(mDrawerList);
			about_intent = new Intent();
			about_intent.setClass(setting.this, about.class);
			startActivity(about_intent);
			overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_left);
			finish();
			break;
		case 4:
			mDrawerList.setItemChecked(value.showposition(), true);
			mDrawerList.setSelection(value.showposition());
			mDrawerLayout.closeDrawer(mDrawerList);
			setting_intent = new Intent();
			setting_intent.setClass(setting.this, setting.class);
			startActivity(setting_intent);
			overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_left);
			finish();
			break;
		case 5:
			mDrawerList.setItemChecked(value.showposition(), true);
			mDrawerList.setSelection(value.showposition());
			mDrawerLayout.closeDrawer(mDrawerList);
			setTitle(mitemTitles[value.showposition()]);
			if (data.getInt("loginway", 0) == 1) {
				value.setfblog("logout");

				value.setfbclick(0);
				fb_intent = new Intent(this, fblogin.class);
				startActivity(fb_intent);
				overridePendingTransition(R.anim.pull_in_left,
						R.anim.push_out_left);
				setting.this.finish();
			} else if (data.getInt("loginway", 0) == 2) {
				value.setgoogleclick(0);
				google_intent = new Intent(this, googlelogin.class);
				startActivity(google_intent);
				overridePendingTransition(R.anim.pull_in_left,
						R.anim.push_out_left);
				finish();
			}
			Log.d(TAG, "chage page");
			break;

		}

	}

	@TargetApi(14)
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (mDrawerToggle.onOptionsItemSelected((android.view.MenuItem) item)) {

			return true;
		}
		return super.onOptionsItemSelected(item);

	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub

		switch (parent.getId()) {
		case R.id.netspinner_min:
			netmin = netspinner_min.getSelectedItemPosition();
			value.setnetposition(netmin);
			if (netmin == 0) {
				value.setnetmin(0);
				Log.d(TAG, "Min Value" + value.shownetmin());

			}
			if (netmin == 1) {
				value.setnetmin(500);
				Log.d(TAG, "NET Min Value" + value.shownetmin());
			}
			if (netmin == 2) {
				value.setnetmin(800);
				Log.d(TAG, "NET Min Value" + value.shownetmin());
			}
			if (netmin == 3) {
				value.setnetmin(1000);
				Log.d(TAG, "NET Min Value" + value.shownetmin());
			}
			if (netmin == 4) {
				value.setnetmin(5000);
				Log.d(TAG, "NET Min Value" + value.shownetmin());
			}
			if (netmin == 5) {
				value.setnetmin(10000);
				Log.d(TAG, "NET Min Value" + value.shownetmin());
			}
			break;

		case R.id.sizespinner_min:
			sizemin = sizespinner_min.getSelectedItemPosition();
			value.setsizeposition(sizemin);
			if (sizespinner_min.isClickable() && sizemin == 0) {
				Builder HelpDialog = new AlertDialog.Builder(this);
				HelpDialog.setTitle("Warning");
				HelpDialog.setMessage("If show all application size" + "\n"
						+ "the Detial in phonegetin will be slower");
				DialogInterface.OnClickListener OkClick = new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

					}
				};
				HelpDialog.setNeutralButton("OK", OkClick);
				HelpDialog.show();
			}
			if (sizemin == 0) {
				value.setsizemin(0);
				Log.d(TAG, "Size Min Value" + value.showsizemin());
			}
			if (sizemin == 1) {
				value.setsizemin(500);
				Log.d(TAG, "Size Min Value" + value.showsizemin());
			}
			if (sizemin == 2) {
				value.setsizemin(800);
				Log.d(TAG, "Size Min Value" + value.showsizemin());
			}
			if (sizemin == 3) {
				value.setsizemin(1000);
				Log.d(TAG, "Size Min Value" + value.showsizemin());
			}
			if (sizemin == 4) {
				value.setsizemin(5000);
				Log.d(TAG, "Size Min Value" + value.showsizemin());
			}
			if (sizemin == 5) {
				value.setsizemin(10000);
				Log.d(TAG, "Size Min Value" + value.showsizemin());
			}
			break;
		case R.id.timespinner_min:
			timemin = timespinner_min.getSelectedItemPosition();
			value.settimeposition(timemin);

			break;

		}

	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.netupload:
			if (value.shownetapp() != null) {
				Log.d(TAG, "Net app Not Null" + value.shownetapp());
				postnetappdata postnetapp = new postnetappdata();
				postnetapp.execute();

			} else {
				Builder HelpDialog = new AlertDialog.Builder(this);
				HelpDialog.setTitle("Warning");
				HelpDialog
						.setMessage("We have to get your phone Net flow Information"
								+ "\n"
								+ "Please go to Detail and check Network page!");

				DialogInterface.OnClickListener OkClick = new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						value.setPosition(3);
						detail_intent = new Intent();
						detail_intent.setClass(setting.this, Detail.class);
						startActivity(detail_intent);

					}
				};
				DialogInterface.OnClickListener NoClick = new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

					}
				};
				HelpDialog.setPositiveButton("Later", NoClick);
				HelpDialog.setNeutralButton("Go to Detail", OkClick);
				HelpDialog.show();
			}

			break;
		case R.id.sizeupload:
			if (value.showsizeapp() != null) {
				Log.d(TAG, "Size app Not Null" + value.showsizeapp());
				postsizeappdata postsizeapp = new postsizeappdata();
				postsizeapp.execute();

			} else {
				Builder HelpDialog = new AlertDialog.Builder(this);
				HelpDialog.setTitle("Warning");
				HelpDialog
						.setMessage("We have to get your phone App size Information"
								+ "\n"
								+ "Please go to Detail and view Size page!");

				DialogInterface.OnClickListener OkClick = new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						value.setPosition(3);
						detail_intent = new Intent();
						detail_intent.setClass(setting.this, Detail.class);
						startActivity(detail_intent);

					}
				};
				DialogInterface.OnClickListener NoClick = new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

					}
				};
				HelpDialog.setPositiveButton("Later", NoClick);
				HelpDialog.setNeutralButton("Go to Detail", OkClick);
				HelpDialog.show();
			}

			break;
		case R.id.memupload:
			if (value.showmemapp() != null) {
				Log.d(TAG, "Size app Not Null" + value.showmemapp());
				postmemappdata postmemapp = new postmemappdata();
				postmemapp.execute();

			} else {
				Builder HelpDialog = new AlertDialog.Builder(this);
				HelpDialog.setTitle("Warning");
				HelpDialog
						.setMessage("We have to get your phone App Mem Information"
								+ "\n"
								+ "Please go to Detail and view Mem page!");

				DialogInterface.OnClickListener OkClick = new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						value.setPosition(3);
						detail_intent = new Intent();
						detail_intent.setClass(setting.this, Detail.class);
						startActivity(detail_intent);

					}
				};
				DialogInterface.OnClickListener NoClick = new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

					}
				};
				HelpDialog.setPositiveButton("Later", NoClick);
				HelpDialog.setNeutralButton("Go to Detail", OkClick);
				HelpDialog.show();
			}

			break;

		}

	}

	class postnetappdata extends AsyncTask<Integer, Integer, String> {

		@Override
		protected String doInBackground(Integer... countTo) {
			// TODO Auto-generated method stub

			try {
				netdata = value.shownetapp();
				netapp_function(uid, value.showimei(), netdata);

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
			Log.d(TAG, "Return:" + ret_net);
			value.setnetapp(null);
			Log.d(TAG, "Set!!!!!!!!!!" + value.shownetapp());
		}

		@Override
		protected void onCancelled() {
			// TODO Auto-generated method stub
			super.onCancelled();
		}
	}

	public String netapp_function(String id, String imei,
			ArrayList<HashMap<String, Object>> data) throws KeyStoreException,
			IOException, KeyManagementException, NoSuchAlgorithmException,
			UnrecoverableKeyException {
		Log.d(TAG, "Fuction!!!!!");
		String url = "https://nrl.cce.mcu.edu.tw/pgi/appflow.php";
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
		Log.d(TAG, "DATA.........");
		List<NameValuePair> params = new ArrayList<NameValuePair>();

		for (int i = 0; i < data.size(); i++) {
			params.add(new BasicNameValuePair("uid[]", id));
			params.add(new BasicNameValuePair("imei[]", imei));
			Iterator<String> item = data.get(i).keySet().iterator();
			while (item.hasNext()) {
				String key = item.next();
				if (key.equals("appname") || key.equals("apptotalbyte")
						|| key.equals("intpercent") || key.equals("time")) {
					Log.d(TAG, "Key!!!!!" + key);
					params.add(new BasicNameValuePair(key + "[]", data.get(i)
							.get(key).toString()));
					Log.d(TAG, "Data~~~~~" + data.get(i).get(key).toString());
				}
			}

		}
		Log.d(TAG, "Fainl Data!!!!!" + params);

		HttpClient client = new DefaultHttpClient();
		client.getConnectionManager().getSchemeRegistry().register(sch);
		HttpPost request = new HttpPost(url);

		request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));

		HttpResponse response = client.execute(request);
		HttpEntity resEntity = response.getEntity();
		ret_net = EntityUtils.toString(resEntity);
		return ret_net;
	}

	class postsizeappdata extends AsyncTask<Integer, Integer, String> {

		@Override
		protected String doInBackground(Integer... countTo) {
			// TODO Auto-generated method stub

			try {
				sizedata = value.showsizeapp();
				sizeapp_function(uid, value.showimei(), sizedata);

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
			Log.d(TAG, "Return:" + ret_size);
			value.setsizeapp(null);
			Log.d(TAG, "Set!!!!!!!!!!" + value.showsizeapp());
		}

		@Override
		protected void onCancelled() {
			// TODO Auto-generated method stub
			super.onCancelled();

		}
	}

	public String sizeapp_function(String id, String imei,
			ArrayList<HashMap<String, Object>> data) throws KeyStoreException,
			IOException, KeyManagementException, NoSuchAlgorithmException,
			UnrecoverableKeyException {
		Log.d(TAG, "Fuction!!!!!");
		String url = "https://nrl.cce.mcu.edu.tw/pgi/appsize.php";
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
		Log.d(TAG, "DATA.........");
		List<NameValuePair> params = new ArrayList<NameValuePair>();

		for (int i = 0; i < data.size(); i++) {
			params.add(new BasicNameValuePair("uid[]", id));
			params.add(new BasicNameValuePair("imei[]", imei));
			Iterator<String> item = data.get(i).keySet().iterator();
			while (item.hasNext()) {
				String key = item.next();
				if (key.equals("appname") || key.equals("totalsize")
						|| key.equals("codesize") || key.equals("cachesize")
						|| key.equals("datasize") || key.equals("time")) {
					Log.d(TAG, "Key!!!!!" + key);
					params.add(new BasicNameValuePair(key + "[]", data.get(i)
							.get(key).toString()));
					Log.d(TAG, "Data~~~~~" + data.get(i).get(key).toString());
				}
			}

		}
		Log.d(TAG, "Fainl Data!!!!!" + params);

		HttpClient client = new DefaultHttpClient();
		client.getConnectionManager().getSchemeRegistry().register(sch);
		HttpPost request = new HttpPost(url);

		request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));

		HttpResponse response = client.execute(request);
		HttpEntity resEntity = response.getEntity();
		ret_size = EntityUtils.toString(resEntity);
		return ret_size;
	}

	class postmemappdata extends AsyncTask<Integer, Integer, String> {

		@Override
		protected String doInBackground(Integer... countTo) {
			// TODO Auto-generated method stub

			try {
				memdata = value.showmemapp();
				memapp_function(uid, value.showimei(), memdata);

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
			Log.d(TAG, "Return:" + ret_mem);
			value.setmemapp(null);
			Log.d(TAG, "Set!!!!!!!!!!" + value.showmemapp());
		}

		@Override
		protected void onCancelled() {
			// TODO Auto-generated method stub
			super.onCancelled();

		}
	}

	public String memapp_function(String id, String imei,
			ArrayList<HashMap<String, Object>> data) throws KeyStoreException,
			IOException, KeyManagementException, NoSuchAlgorithmException,
			UnrecoverableKeyException {
		Log.d(TAG, "Fuction!!!!!");
		String url = "https://nrl.cce.mcu.edu.tw/pgi/appmem.php";
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
		Log.d(TAG, "DATA.........");
		List<NameValuePair> params = new ArrayList<NameValuePair>();

		for (int i = 0; i < data.size(); i++) {
			params.add(new BasicNameValuePair("uid[]", id));
			params.add(new BasicNameValuePair("imei[]", imei));
			Iterator<String> item = data.get(i).keySet().iterator();
			while (item.hasNext()) {
				String key = item.next();
				if (key.equals("name") || key.equals("totaldirty")
						|| key.equals("totalshare") || key.equals("Mempercent")
						|| key.equals("time")) {
					Log.d(TAG, "Key!!!!!" + key);
					params.add(new BasicNameValuePair(key + "[]", data.get(i)
							.get(key).toString()));
					Log.d(TAG, "Data~~~~~" + data.get(i).get(key).toString());
				}
			}

		}
		Log.d(TAG, "Fainl Data!!!!!" + params);

		HttpClient client = new DefaultHttpClient();
		client.getConnectionManager().getSchemeRegistry().register(sch);
		HttpPost request = new HttpPost(url);

		request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));

		HttpResponse response = client.execute(request);
		HttpEntity resEntity = response.getEntity();
		ret_mem = EntityUtils.toString(resEntity);
		return ret_mem;
	}

}
