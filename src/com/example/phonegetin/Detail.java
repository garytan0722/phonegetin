package com.example.phonegetin;

import java.util.ArrayList;

import java.util.List;
import java.util.Locale;
import java.util.Vector;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.app.ListFragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.TabHost.TabSpec;

public class Detail extends FragmentActivity {
	private Intent setting_intent, fb_intent, google_intent, home_intent,
			detail_intent, annintent, about_intent;
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	private CharSequence mDrawerTitle, mTitle;
	private String[] mitemTitles;
	private TypedArray mMenuIcons;
	private ArrayList<NavDrawerItem> mDrawerItems;
	private NavDrawerListAdapter adapter;
	private static String TAG = "Detail";
	private ViewPager mViewPager;
	private MyViewPagerAdapter mPagerAdapter;
	private Typeface page_title;
	private SharedPreferences data;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail);
		data = getSharedPreferences("Data", MODE_PRIVATE);
		initialization();
		set_fragment();

	}

	public void set_fragment() {
		Log.d(TAG, "set_fragment");
		List<Fragment> fragments = new Vector<Fragment>();
		fragments.add(Fragment.instantiate(this, phoneinfo.class.getName()));
		fragments.add(ListFragment.instantiate(this, network.class.getName()));
		fragments.add(ListFragment.instantiate(this, Mem.class.getName()));
		fragments.add(ListFragment.instantiate(this, capacity.class.getName()));
		fragments.add(ListFragment.instantiate(this, apptime.class.getName()));

		this.mPagerAdapter = new MyViewPagerAdapter(
				super.getSupportFragmentManager(), fragments);
		ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
		viewPager.setAdapter(this.mPagerAdapter);
		PagerTabStrip pagerTabStrip = (PagerTabStrip) findViewById(R.id.pagerTabStrip);
		for (int i = 0; i < pagerTabStrip.getChildCount(); ++i) {
			View nextChild = pagerTabStrip.getChildAt(i);
			if (nextChild instanceof TextView) {
				TextView textViewToConvert = (TextView) nextChild;
				page_title = Typeface.createFromAsset(getAssets(),
						"Autumn Regular.ttf");
				textViewToConvert.setTypeface(page_title);
			}
		}

	}

	public class MyViewPagerAdapter extends FragmentPagerAdapter {
		private List<Fragment> fragments;

		public MyViewPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
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

		@Override
		public CharSequence getPageTitle(int position) {
			// CHANGE STARTS HERE
			if (position == 0) {

				return "Information";
			} else if (position == 1) {
				return "Network";
			} else if (position == 2) {
				return "Memory";
			} else if (position == 3) {
				return "Size";
			} else if (position == 4) {
				return "Uninstall";
			}
			return "No title";
		}

	}

	public void initialization() {
	
		value.setupdate(0);
		mTitle = mDrawerTitle = getTitle();
		mitemTitles = getResources().getStringArray(R.array.items_array);
		mMenuIcons = getResources().obtainTypedArray(R.array.icons_array);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);
		setTitle(mitemTitles[value.showposition()]);

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
		mDrawerList
				.setOnItemClickListener(new all_serviceDrawerItemClickListener());
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

	private class all_serviceDrawerItemClickListener implements
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
			Detail.this.finish();
			break;

		case 1:
			mDrawerList.setItemChecked(value.showposition(), true);
			mDrawerList.setSelection(value.showposition());
			mDrawerLayout.closeDrawer(mDrawerList);
			annintent = new Intent();
			annintent.setClass(this, Announcement.class);
			startActivity(annintent);
			overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_left);
			Detail.this.finish();
			break;
		case 2:
			mDrawerList.setItemChecked(value.showposition(), true);
			mDrawerList.setSelection(value.showposition());
			mDrawerLayout.closeDrawer(mDrawerList);
			detail_intent = new Intent();
			detail_intent.setClass(this, Detail.class);
			startActivity(detail_intent);
			overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_left);
			Detail.this.finish();
			break;
		case 3:
			mDrawerList.setItemChecked(value.showposition(), true);
			mDrawerList.setSelection(value.showposition());
			mDrawerLayout.closeDrawer(mDrawerList);
			about_intent = new Intent();
			about_intent.setClass(Detail.this, about.class);
			startActivity(about_intent);
			overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_left);
			Detail.this.finish();
			break;

		case 4:
			mDrawerList.setItemChecked(value.showposition(), true);
			mDrawerList.setSelection(value.showposition());
			mDrawerLayout.closeDrawer(mDrawerList);
			setting_intent = new Intent();
			setting_intent.setClass(this, setting.class);
			startActivity(setting_intent);
			overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_left);
			Detail.this.finish();
			break;
		case 5:
			mDrawerList.setItemChecked(value.showposition(), true);
			mDrawerList.setSelection(value.showposition());
			mDrawerLayout.closeDrawer(mDrawerList);
			setTitle(mitemTitles[value.showposition()]);
			if (data.getInt("loginway", 0) == 1) {

				value.setfblog("logout");
				value.setfbclick(0);
				fb_intent = new Intent(Detail.this, fblogin.class);
				startActivity(fb_intent);
				overridePendingTransition(R.anim.pull_in_left,
						R.anim.push_out_left);
				Detail.this.finish();
			} else if (data.getInt("loginway", 0) == 2) {
				value.setgoogleclick(0);
				google_intent = new Intent(this, googlelogin.class);
				startActivity(google_intent);
				overridePendingTransition(R.anim.pull_in_left,
						R.anim.push_out_left);
				Detail.this.finish();
			}
			Log.d(TAG, "chage page");
			break;

		}

		Log.d(TAG, "Position" + position);

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
