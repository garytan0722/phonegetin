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
import android.net.TrafficStats;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Announcement extends Activity implements OnClickListener {
	private static String TAG = "Announcement";
	private Intent google_intent, fb_intent, setting_intent, home_intent,
			home_batteryIntent, detail_intent, annintent, about_intent;
	private DrawerLayout mDrawerLayout;
	private TypedArray mMenuIcons;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	private CharSequence mDrawerTitle, mTitle;
	private String[] mitemTitles;
	private ArrayList<NavDrawerItem> mDrawerItems;
	private NavDrawerListAdapter adapter;
	private TextView ann, name;
	private String ret_ann;
	private static int request = 0;
	private Button next, now, pre;
	private int error;
	private SharedPreferences data;

	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.announcement);
		initialization();
		ann = (TextView) findViewById(R.id.ann);
		name = (TextView) findViewById(R.id.name);
		next = (Button) findViewById(R.id.nextpage);
		now = (Button) findViewById(R.id.now);
		pre = (Button) findViewById(R.id.frontpage);
		next.setOnClickListener(this);
		now.setOnClickListener(this);
		pre.setOnClickListener(this);
		postannTask postdata = new postannTask();
		postdata.execute();

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

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// If the nav drawer is open, hide action items related to the content
		// view
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		return super.onPrepareOptionsMenu(menu);
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
			Announcement.this.finish();
			break;

		case 1:
			mDrawerList.setItemChecked(value.showposition(), true);
			mDrawerList.setSelection(value.showposition());
			mDrawerLayout.closeDrawer(mDrawerList);
			annintent = new Intent();
			annintent.setClass(this, Announcement.class);
			startActivity(annintent);
			overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_left);
			Announcement.this.finish();
			break;

		case 2:
			mDrawerList.setItemChecked(value.showposition(), true);
			mDrawerList.setSelection(value.showposition());
			mDrawerLayout.closeDrawer(mDrawerList);
			detail_intent = new Intent();
			detail_intent.setClass(this, Detail.class);
			startActivity(detail_intent);
			overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_left);
			Announcement.this.finish();
			break;
		case 3:
			mDrawerList.setItemChecked(value.showposition(), true);
			mDrawerList.setSelection(value.showposition());
			mDrawerLayout.closeDrawer(mDrawerList);
			about_intent = new Intent();
			about_intent.setClass(this, about.class);
			startActivity(about_intent);
			overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_left);
			Announcement.this.finish();
			break;
		case 4:
			mDrawerList.setItemChecked(value.showposition(), true);
			mDrawerList.setSelection(value.showposition());
			mDrawerLayout.closeDrawer(mDrawerList);
			setting_intent = new Intent();
			setting_intent.setClass(Announcement.this, setting.class);
			startActivity(setting_intent);
			overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_left);
			Announcement.this.finish();

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
				Announcement.this.finish();
			} else if (data.getInt("loginway", 0) == 2) {
				value.setgoogleclick(0);
				google_intent = new Intent(this, googlelogin.class);
				overridePendingTransition(R.anim.pull_in_left,
						R.anim.push_out_left);
				startActivity(google_intent);
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

	class postannTask extends AsyncTask<Integer, Integer, String> {

		@Override
		protected String doInBackground(Integer... countTo) {
			// TODO Auto-generated method stub

			try {

				https_function(String.valueOf(request));
				error = 0;
			}

			catch (KeyManagementException e) {
				// TODO Auto-generated catch block
				Log.d(TAG, "KeyManagementException");
				e.printStackTrace();
			} catch (KeyStoreException e) {
				// TODO Auto-generated catch block
				Log.d(TAG, "KeyStoreException");
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				Log.d(TAG, "NoSuchAlgorithmException");
				e.printStackTrace();
			} catch (UnrecoverableKeyException e) {
				// TODO Auto-generated catch block
				Log.d(TAG, "UnrecoverableKeyException");
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Log.d(TAG, "IOException!!");
				error = 1;
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
			if (error == 1) {
				Log.d(TAG, "Error is 1");
				Builder HelpDialog = new AlertDialog.Builder(Announcement.this);
				HelpDialog.setTitle("Message");
				HelpDialog.setMessage("Please Check your Internet");
				DialogInterface.OnClickListener OnClick = new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
						finish();
					}
				};
				DialogInterface.OnClickListener OnClick1 = new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent();
						intent.setClass(Announcement.this, homepager.class);
						startActivity(intent);
						finish();
					}
				};
				HelpDialog.setNeutralButton("Go to Check", OnClick);
				HelpDialog.setPositiveButton("Later", OnClick1);
				HelpDialog.show();
			}
			if (ret_ann != null) {
				if (!ret_ann.equals("null") && error == 0) {
					String content = null, writer = null;
					String countstring = null;
					try {
						writer = new JSONObject(ret_ann).getString("name");
						content = new JSONObject(ret_ann).getString("news");
						countstring = new JSONObject(ret_ann)
								.getString("count");
						Log.d(TAG, "content:" + content);
						Log.d(TAG, "name:" + writer);
						Log.d(TAG, "count:" + countstring);

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					if (request <= Integer.valueOf(countstring)) {
						Typeface textstyle = Typeface.createFromAsset(
								getAssets(),
								"Bachelor Pad Expanded JL Italic.ttf");
						ann.setTypeface(textstyle);
						ann.setText(content);
						name.setText(writer);
						pre.setVisibility(View.VISIBLE);
						next.setVisibility(View.VISIBLE);
					}
					if (request == 0) {

						pre.setVisibility(View.INVISIBLE);
					}

				} else if (ret_ann.equals("null")) {
					Log.d(TAG, "Value is null");
					request -= 1;
					next.setVisibility(View.INVISIBLE);
					Builder HelpDialog = new AlertDialog.Builder(
							Announcement.this);
					HelpDialog.setTitle("Message");
					HelpDialog.setMessage("NO New Announcement");
					DialogInterface.OnClickListener OnClick = new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {

						}
					};

					HelpDialog.setNeutralButton("Ok", OnClick);
					HelpDialog.show();
				}
			}
		}

		@Override
		protected void onCancelled() {
			// TODO Auto-generated method stub
			super.onCancelled();

		}
	}

	public String https_function(String input) throws KeyStoreException,
			IOException, KeyManagementException, NoSuchAlgorithmException,
			UnrecoverableKeyException {
		Log.d(TAG, "Fuction!!!!!");
		String url = "https://nrl.cce.mcu.edu.tw/pgi/announcement.php";
		InputStream instream = this.getResources().openRawResource(R.raw.syl);// ������

		KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());//

		try {
			trustStore.load(instream, null);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			Log.d(TAG, "NoSuchAlgorithmException");
			e.printStackTrace();
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			Log.d(TAG, "CertificateException");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.d(TAG, "IOException");
			e.printStackTrace();
		} finally {
			instream.close();
		}

		SSLSocketFactory socketFactory = new SSLSocketFactory(trustStore);
		Scheme sch = new Scheme("https", socketFactory, 443);// �]�w�O��https�s�u�覡
		Log.d(TAG, "DATA........." + input);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("request", input));// �]�w�Ŷ����Ʃ�i�h

		HttpClient client = new DefaultHttpClient();
		client.getConnectionManager().getSchemeRegistry().register(sch);
		HttpPost request = new HttpPost(url);// �]�w���Ӥ覡�s�u�M���}

		request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));

		HttpResponse response = client.execute(request);
		HttpEntity resEntity = response.getEntity();
		ret_ann = EntityUtils.toString(resEntity);
		return ret_ann;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		postannTask ann = new postannTask();
		switch (v.getId()) {

		case R.id.nextpage:
			request += 1;
			ann.execute();
			break;
		case R.id.frontpage:
			if (request >= 1) {
				request -= 1;
				ann.execute();
			}

			pre.setVisibility(View.INVISIBLE);

			break;
		case R.id.now:
			request = 0;
			ann.execute();
			break;

		}

	}

}
