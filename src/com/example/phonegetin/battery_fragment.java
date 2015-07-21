package com.example.phonegetin;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;

import com.example.phonegetin.CircularProgressBar.ProgressAnimationListener;
import com.example.phonegetin.R.id;
import com.example.phonegetin.call_chart.postrequest;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class battery_fragment extends Fragment implements OnClickListener {
	private String TAG = "battery_fragment";
	private View thispage;
	private SharedPreferences data;
	private String imei, phonemodelstring;
	private CircularProgressBar c2;
	private int level, plugged, scale, percent;
	static Typeface cirprogress;
	private SharedPreferences share;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		share = getActivity().getSharedPreferences("Data",
				getActivity().MODE_PRIVATE);
		cirprogress = Typeface.createFromAsset(getActivity().getAssets(),
				"DigitaldreamFat.ttf");
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView");
		thispage = inflater.inflate(R.layout.home_battery, container, false);
		setbatlayout();
		return thispage;
	}

	public void setbatlayout() {
		Log.d(TAG, "Set bat");
		SharedPreferences.Editor editor = share.edit();
		c2 = (CircularProgressBar) thispage
				.findViewById(R.id.circularprogressbar2);
		Button info = (Button) thispage.findViewById(id.info);
		ImageView bat_image = (ImageView) thispage
				.findViewById(R.id.type_image);
		TextView typetitle = (TextView) thispage.findViewById(R.id.typetitle);
		TextView typ = (TextView) thispage.findViewById(R.id.type);
		Typeface textstyle = Typeface.createFromAsset(
				getActivity().getAssets(),
				"Bachelor Pad Expanded JL Italic.ttf");
		typetitle.setTypeface(textstyle);
		typ.setTypeface(textstyle);
		info.setOnClickListener(this);
		level = getbattery();
		editor.putInt("bat", level);
		editor.commit();
		int type = plugged;
		if (level != 0) {
			drawbattery();
		}

		Log.d(TAG, "Type::" + type);
		if (type == 0) {
			typ.setText("Battery");
			bat_image.setBackgroundResource(R.drawable.discharge);

		} else if (type == 1) {
			typ.setText("AC");
			bat_image.setBackgroundResource(R.drawable.ac_icon);

		} else if (type == 2) {
			typ.setText("USB");
			bat_image.setBackgroundResource(R.drawable.usb_icon);

		} else if (type == 3) {
			typetitle.setText("Searching");
			bat_image.setBackgroundResource(R.drawable.searchnet);
		}
	}

	public int getbattery() {
		Log.d(TAG, "Get battery");
		Intent batteryIntent = getActivity().getApplicationContext()
				.registerReceiver(null,
						new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
		plugged = batteryIntent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0);
		level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
		scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, 0);
		percent = (level * 100) / scale;
		return percent;
	}

	public void drawbattery() {

		c2.animateProgressTo(0, level, new ProgressAnimationListener() {

			@Override
			public void onAnimationStart() {

			}

			@Override
			public void onAnimationProgress(int progress) {
				c2.setTitle(progress + "%");
			}

			@Override
			public void onAnimationFinish() {

			}
		});
	}

	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId() == R.id.info) {
			Log.d(TAG, "Click");
			Intent intent = new Intent(getActivity(), bat_tab.class);
			startActivity(intent);
			getActivity().overridePendingTransition(R.anim.pull_in_right,
					R.anim.push_out_right);
		}
	}

}
