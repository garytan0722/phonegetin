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
import java.util.Vector;

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

import com.example.phonegetin.call_tab.callViewPagerAdapter;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class homepager extends FragmentActivity {
	private ViewPager mViewPager;
	private ViewPagerAdapter mPagerAdapter;
	private String TAG = "homepager";
	private DrawerLayout mDrawerLayout;
	private TypedArray mMenuIcons;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	private CharSequence mDrawerTitle, mTitle;
	private String[] mitemTitles;
	private ArrayList<NavDrawerItem> mDrawerItems;
	private NavDrawerListAdapter adapter;
	private SharedPreferences data;
	private Intent fb_intent, google_intent, setting_intent, home_intent,
			detail_intent, annintent, about_intent;
	private String ret_id, acesstoken, phonemodelstring, imei, Version;
	private Session session;
	private static int google_error, fb_error;
	private Build PhoneModel;
	public static final String Service_ACTION = "Service";

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pager);
		data = getSharedPreferences("Data", MODE_PRIVATE);
		phone_imei();
		phone_model();
		if (data.getInt("login", 0) == 0 && value.showfbclick() == 1) {
			Log.d(TAG, "FB GET info....");
			SharedPreferences.Editor editor = data.edit();
			editor.putInt("loginway", 1);
			editor.commit();
			getfbinfo();
		}
		if (data.getInt("login", 0) == 0 && value.showgoogleclick() == 1) {
			Log.d(TAG, "Google GET info....");
			SharedPreferences.Editor editor = data.edit();
			editor.putInt("loginway", 2);
			editor.commit();
			getgoogleinfo();
		}
		set_fragment();
		initialization();
		PollingUtils.startService(getApplicationContext(), 600,
				My_service.class, Service_ACTION);
	}

	public void set_fragment() {
		List<Fragment> fragments = new Vector<Fragment>();
		fragments.add(Fragment.instantiate(this,
				battery_fragment.class.getName()));
		fragments.add(Fragment.instantiate(this, net_fragment.class.getName()));
		fragments
				.add(Fragment.instantiate(this, call_fragment.class.getName()));
		fragments.add(Fragment.instantiate(this, gps_fragment.class.getName()));
		this.mPagerAdapter = new ViewPagerAdapter(
				super.getSupportFragmentManager(), fragments);
		ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
		viewPager.setPageTransformer(true, new ZoomOutPageTransformer());
		viewPager.setAdapter(this.mPagerAdapter);

	}

	public void phone_imei() {
		TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		imei = telephonyManager.getDeviceId();
		value.setimei(imei);
		Log.d(TAG, "phoneimei!!!" + imei);
	}

	public void phone_model() {
		phonemodelstring = PhoneModel.MODEL;
		Log.d(TAG, "Phone Model:" + phonemodelstring);
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
			homepager.this.finish();
			break;
		case 1:
			mDrawerList.setItemChecked(value.showposition(), true);
			mDrawerList.setSelection(value.showposition());
			mDrawerLayout.closeDrawer(mDrawerList);
			annintent = new Intent();
			annintent.setClass(this, Announcement.class);
			startActivity(annintent);
			overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_left);
			homepager.this.finish();
			break;
		case 2:
			mDrawerList.setItemChecked(value.showposition(), true);
			mDrawerList.setSelection(value.showposition());
			mDrawerLayout.closeDrawer(mDrawerList);
			detail_intent = new Intent();
			detail_intent.setClass(this, Detail.class);
			startActivity(detail_intent);
			overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_left);
			homepager.this.finish();
			break;
		case 3:
			mDrawerList.setItemChecked(value.showposition(), true);
			mDrawerList.setSelection(value.showposition());
			mDrawerLayout.closeDrawer(mDrawerList);
			about_intent = new Intent();
			about_intent.setClass(this, about.class);
			startActivity(about_intent);
			overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_left);
			homepager.this.finish();
			break;
		case 4:
			mDrawerList.setItemChecked(value.showposition(), true);
			mDrawerList.setSelection(value.showposition());
			mDrawerLayout.closeDrawer(mDrawerList);
			setting_intent = new Intent();
			setting_intent.setClass(homepager.this, setting.class);
			startActivity(setting_intent);
			overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_left);
			homepager.this.finish();
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
				homepager.this.finish();
			} else if (data.getInt("loginway", 0) == 2) {
				value.setgoogleclick(0);
				google_intent = new Intent(this, googlelogin.class);
				startActivity(google_intent);
				overridePendingTransition(R.anim.pull_in_left,
						R.anim.push_out_left);
				homepager.this.finish();
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

	public class ViewPagerAdapter extends FragmentPagerAdapter {
		private List<Fragment> fragments;

		public ViewPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
			super(fm);
			this.fragments = fragments;
		}

		@Override
		public Fragment getItem(int position) {
			Log.d(TAG, "Position:......." + position);
			return this.fragments.get(position);
		}

		@Override
		public int getCount() {
			return this.fragments.size();
		}

		public CharSequence getPageTitle(int position) {
			if (position == 0) {
				return "BATTERY";
			} else if (position == 1) {
				return "NETWORK";
			} else if (position == 2) {
				return "PHONE";
			} else if (position == 3) {
				return "GPS";
			}
			return Version;

		}

	}

	public class ZoomOutPageTransformer implements ViewPager.PageTransformer {
		private static final float MIN_SCALE = 0.85f;
		private static final float MIN_ALPHA = 0.5f;

		public void transformPage(View view, float position) {
			int pageWidth = view.getWidth();
			int pageHeight = view.getHeight();

			if (position < -1) { // [-Infinity,-1)
				// This page is way off-screen to the left.
				view.setAlpha(0);

			} else if (position <= 1) { // [-1,1]
				// Modify the default slide transition to shrink the page as
				// well
				float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
				float vertMargin = pageHeight * (1 - scaleFactor) / 2;
				float horzMargin = pageWidth * (1 - scaleFactor) / 2;
				if (position < 0) {
					view.setTranslationX(horzMargin - vertMargin / 2);
				} else {
					view.setTranslationX(-horzMargin + vertMargin / 2);
				}

				// Scale the page down (between MIN_SCALE and 1)
				view.setScaleX(scaleFactor);
				view.setScaleY(scaleFactor);

				// Fade the page relative to its size.
				view.setAlpha(MIN_ALPHA + (scaleFactor - MIN_SCALE)
						/ (1 - MIN_SCALE) * (1 - MIN_ALPHA));

			} else { // (1,+Infinity]
				// This page is way off-screen to the right.
				view.setAlpha(0);
			}
		}
	}

	public void getfbinfo() {
		Log.d(TAG, "Accesstoken!!!!");
		session = Session.getActiveSession();
		Log.d(TAG, "getactiviSession!!!!!");
		if (session == null) {
			Log.d(TAG, "Session is null!!!!!");
			session = Session.openActiveSessionFromCache(this
					.getApplicationContext());
		}
		if (session != null && session.isOpened()) {
			Log.d(TAG, "SessionOpen!!!!");
			acesstoken = session.getAccessToken();
			Log.d(TAG, "" + acesstoken);
			Request.executeMeRequestAsync(session,
					new Request.GraphUserCallback() {
						@Override
						public void onCompleted(GraphUser user,
								Response response) {
							// TODO Auto-generated method stub
							if (user != null) {
								// Display the parsed user info
								// userInfoTextView.setText(buildUserInfoDisplay(user));
								Log.d(TAG, "get user data");
								value.setfbacesstoken(acesstoken);
								value.setfbid(user.getId().toString());
								value.setfblink(user.getLink().toString());
								Log.d(TAG, acesstoken);
								Log.d(TAG, value.showfbid());
								Log.d(TAG, value.showfblink());
								Log.d(TAG, "do post");
								postdataTask postdata = new postdataTask();
								postdata.execute();

							}

						}
					});
		}
	}

	public void getgoogleinfo() {
		postgoogledata postgoogle = new postgoogledata();
		postgoogle.execute();
	}

	class postgoogledata extends AsyncTask<Integer, Integer, String> {
		
		@Override
		protected String doInBackground(Integer... countTo) {
			// TODO Auto-generated method stub
			

			try {
				String googleuserid = value.showgooleid();
				String googleusermail = value.showgooglemail();
				google_function(googleuserid, googleusermail, phonemodelstring,
						imei, Version);
				Log.d(TAG, "Google Error");
				google_error = 0;

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
				google_error = 1;
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
			if (google_error == 1) {
				Context context = getApplicationContext();
				CharSequence text = "Login Fail,Please Check your Internet";
				int duration = Toast.LENGTH_SHORT;
				Toast toast = Toast.makeText(context, text, duration);
				toast.show();
				Intent intent = new Intent();
				intent.setClass(homepager.this, MainActivity.class);
				startActivity(intent);
			}
			if (ret_id != null && google_error == 0) {
				SharedPreferences.Editor editor = data.edit();
				editor.putString("uid", ret_id);
				Context context = getApplicationContext();
				CharSequence text = "Login Success";
				int duration = Toast.LENGTH_SHORT;
				Toast toast = Toast.makeText(context, text, duration);
				toast.show();
				editor.putInt("login", 1);
				editor.commit();
				Log.d(TAG, "Uid:" + ret_id);
			}
		}

		@Override
		protected void onCancelled() {
			// TODO Auto-generated method stub
			super.onCancelled();
		}
	}

	public String google_function(String input, String input1, String input2,
			String input3, String input4) throws KeyStoreException,
			IOException, KeyManagementException, NoSuchAlgorithmException,
			UnrecoverableKeyException {
		Log.d(TAG, "Fuction!!!!!");
		String url = "https://nrl.cce.mcu.edu.tw/pgi/clientlogin.php";
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
		params.add(new BasicNameValuePair("gplusid", input));
		params.add(new BasicNameValuePair("gmail", input1));
		params.add(new BasicNameValuePair("model", input2));
		params.add(new BasicNameValuePair("imei", input3));
		params.add(new BasicNameValuePair("version", input4));

		HttpClient client = new DefaultHttpClient();
		client.getConnectionManager().getSchemeRegistry().register(sch);
		HttpPost request = new HttpPost(url);

		request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));

		HttpResponse response = client.execute(request);
		HttpEntity resEntity = response.getEntity();
		ret_id = EntityUtils.toString(resEntity);
		// EntityUtils.toString(resEntity)
		return ret_id;
	}

	class postdataTask extends AsyncTask<Integer, Integer, String> {
		
		@Override
		protected String doInBackground(Integer... countTo) {
			// TODO Auto-generated method stub
			

			try {
				String userid = value.showfbid();
				String userlink = value.showfblink();
				https_function(userid, userlink, acesstoken, phonemodelstring,
						imei, Version);
				Log.d(TAG, "FB Error");
				fb_error = 0;
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
				fb_error = 1;
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
			if (fb_error == 1) {
				Context context = getApplicationContext();
				CharSequence text = "Login Fail,Please Check your Internet";
				int duration = Toast.LENGTH_SHORT;
				Toast toast = Toast.makeText(context, text, duration);
				toast.show();
				Intent intent = new Intent();
				intent.setClass(homepager.this, MainActivity.class);
				startActivity(intent);
			}
			if (ret_id != null && fb_error == 0) {
				SharedPreferences.Editor editor = data.edit();
				editor.putString("uid", ret_id);
				Log.d(TAG, "UID::::::::" + ret_id);
				Context context = getApplicationContext();
				CharSequence text = "Login Success";
				int duration = Toast.LENGTH_SHORT;
				Toast toast = Toast.makeText(context, text, duration);
				toast.show();
				editor.putInt("login", 1);
				editor.commit();
				Log.d(TAG, "Uid:" + ret_id);
			}
		}

		@Override
		protected void onCancelled() {
			// TODO Auto-generated method stub
			super.onCancelled();
			
		}
	}

	public String https_function(String input, String input1, String input2,
			String input3, String input4, String input5)
			throws KeyStoreException, IOException, KeyManagementException,
			NoSuchAlgorithmException, UnrecoverableKeyException {
		Log.d(TAG, "Fuction!!!!!");
		String url = "https://nrl.cce.mcu.edu.tw/pgi/clientlogin.php";
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
		params.add(new BasicNameValuePair("fbid", input));
		params.add(new BasicNameValuePair("fblink", input1));
		params.add(new BasicNameValuePair("fbaccesstoken", input2));
		params.add(new BasicNameValuePair("model", input3));
		params.add(new BasicNameValuePair("imei", input4));
		params.add(new BasicNameValuePair("version", input5));

		HttpClient client = new DefaultHttpClient();
		client.getConnectionManager().getSchemeRegistry().register(sch);
		HttpPost request = new HttpPost(url);

		request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));

		HttpResponse response = client.execute(request);
		HttpEntity resEntity = response.getEntity();
		ret_id = EntityUtils.toString(resEntity);
		
		return ret_id;
	}

}
