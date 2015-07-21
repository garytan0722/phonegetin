package com.example.phonegetin;

import java.util.ArrayList;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

public class about extends Activity {
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
	private TextView teacher, stu1, stu2, stu3, stu4;
	private SharedPreferences data;

	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		Log.d(TAG, "onCreate");
		teacher = (TextView) findViewById(R.id.teacherid);
		stu1 = (TextView) findViewById(R.id.stu1id);
		stu2 = (TextView) findViewById(R.id.stu2id);
		stu3 = (TextView) findViewById(R.id.stu3id);
		stu4 = (TextView) findViewById(R.id.stu4id);
		settext();
		initialization();
	}

	public void settext() {
		String teacherid = "Teacher: CINGCYUAN JIAN  Professor"
				+ "\nEmail:   ccchiang@mail.mcu.edu.tw";
		teacher.setText(teacherid);
		String stu1id = "Student: Genglun Tan student"
				+ "\nEmail:  genglun17@gmail.com";
		stu1.setText(stu1id);
		String stu2id = "Student: DUNCYUN HUANG student"
				+ "\nEmail: jinnif80419@yahoo.com.tw";
		stu2.setText(stu2id);
		String stu3id = "Student: JIANSYUN CHEN student"
				+ "\nEmail: et520w@yahoo.com.tw";
		stu3.setText(stu3id);
		String stu4id = "Student: SYUECIN PENG student"
				+ "\nEmail: lordzpipo@gmail.com";
		stu4.setText(stu4id);
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
			about.this.finish();

			break;

		case 1:
			mDrawerList.setItemChecked(value.showposition(), true);
			mDrawerList.setSelection(value.showposition());
			mDrawerLayout.closeDrawer(mDrawerList);
			annintent = new Intent();
			annintent.setClass(this, Announcement.class);
			startActivity(annintent);
			overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_left);
			about.this.finish();
			break;

		case 2:
			mDrawerList.setItemChecked(value.showposition(), true);
			mDrawerList.setSelection(value.showposition());
			mDrawerLayout.closeDrawer(mDrawerList);
			detail_intent = new Intent();
			detail_intent.setClass(this, Detail.class);
			startActivity(detail_intent);
			overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_left);
			about.this.finish();
			break;
		case 3:
			mDrawerList.setItemChecked(value.showposition(), true);
			mDrawerList.setSelection(value.showposition());
			mDrawerLayout.closeDrawer(mDrawerList);
			about_intent = new Intent();
			about_intent.setClass(about.this, about.class);
			startActivity(about_intent);
			overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_left);
			about.this.finish();
			break;

		case 4:
			mDrawerList.setItemChecked(value.showposition(), true);
			mDrawerList.setSelection(value.showposition());
			mDrawerLayout.closeDrawer(mDrawerList);
			setting_intent = new Intent();
			setting_intent.setClass(about.this, setting.class);
			startActivity(setting_intent);
			overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_left);
			about.this.finish();

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
				about.this.finish();
			} else if (data.getInt("loginway", 0) == 2) {
				value.setgoogleclick(0);
				google_intent = new Intent(this, googlelogin.class);
				startActivity(google_intent);
				overridePendingTransition(R.anim.pull_in_left,
						R.anim.push_out_left);
				about.this.finish();
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

}
