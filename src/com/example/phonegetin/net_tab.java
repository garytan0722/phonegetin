package com.example.phonegetin;

import java.util.List;
import java.util.Vector;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.phonegetin.bat_tab.batViewPagerAdapter;

public class net_tab extends FragmentActivity {
	private ViewPager mViewPager;
	private batViewPagerAdapter mPagerAdapter;
	private static String TAG = "bat_tab";
	private Typeface page_title;
	private ActionBar actionbar;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.net_tab);
		actionbar = getActionBar();
		actionbar.setDisplayHomeAsUpEnabled(true);
		set_fragment();

	}

	public void set_fragment() {
		List<Fragment> fragments = new Vector<Fragment>();
		fragments.add(Fragment.instantiate(this, net_chart.class.getName()));
		fragments.add(Fragment.instantiate(this, net_type.class.getName()));
		this.mPagerAdapter = new batViewPagerAdapter(
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

	public class batViewPagerAdapter extends FragmentPagerAdapter {
		private List<Fragment> fragments;

		public batViewPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
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
				return "NetWork Line Chart";
			} else if (position == 1) {
				return "Type Statistics";
			}

			return "No title";
		}

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
			net_tab.this.finish();
			return true;
		}
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
