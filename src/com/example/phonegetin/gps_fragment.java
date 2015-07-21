package com.example.phonegetin;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.example.phonegetin.R.id;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.SupportMapFragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class gps_fragment extends Fragment implements OnClickListener {
	private View thispage;
	private SharedPreferences data;
	private String TAG = "gps_fragment";
	private MapView mMapView;
	private GoogleMap googleMap;
	String formattedDate;
	private int conn;
	private int gmp;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		data = getActivity().getSharedPreferences("Data",
				getActivity().MODE_PRIVATE);
		conn = getConnectivityStatus(getActivity());

	}

	public static int getConnectivityStatus(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		if (null != activeNetwork) {
			if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
				return 1;
			if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
				return 2;
		}
		return 0;
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView");
		thispage = inflater.inflate(R.layout.home_gps, container, false);
		TextView longitude = (TextView) thispage.findViewById(R.id.Longitude);
		TextView latitude = (TextView) thispage.findViewById(R.id.Latitude);
		TextView lattitle = (TextView) thispage.findViewById(R.id.lattitle);
		TextView longtitle = (TextView) thispage.findViewById(R.id.longtitle);
		TextView gpstime = (TextView) thispage.findViewById(R.id.gpstime);
		Button info = (Button) thispage.findViewById(id.info);
		Typeface textstyle = Typeface.createFromAsset(
				getActivity().getAssets(), "DigitaldreamFat.ttf");
		longitude.setTypeface(textstyle);
		latitude.setTypeface(textstyle);
		gpstime.setTypeface(textstyle);
		info.setOnClickListener(this);
		int gpsopen = data.getInt("opengps", 0);
		double Long = Double.valueOf(data.getString("longitude", "0"));
		double lat = Double.valueOf(data.getString("latitude", "0"));
		long gpstim = data.getLong("gpstime", 0);
		int level = data.getInt("bat", 0);
		mMapView = (MapView) thispage.findViewById(R.id.mapView);
		if (conn == 0) {
			Log.d(TAG, "No NET");
			gmp = 0;
			longitude.setVisibility(View.VISIBLE);
			latitude.setVisibility(View.VISIBLE);
			lattitle.setVisibility(View.VISIBLE);
			longtitle.setVisibility(View.VISIBLE);
			mMapView.setVisibility(View.GONE);
			gpstime.setVisibility(View.VISIBLE);
			longitude.setText(String.valueOf(Long));
			latitude.setText(String.valueOf(lat));
			if (gpstim != 0) {
				gpstim = System.currentTimeMillis() * 1000;
				Log.d(TAG, "GPSTime" + gpstim);
				Date date = new Date(gpstim);
				SimpleDateFormat sdf = new SimpleDateFormat("MM/d H:mm "); // the
																			// format
																			// of
																			// your
																			// date
				formattedDate = sdf.format(date);
				gpstime.setText(formattedDate);
			} else {
				gpstime.setText("No Time");
			}
		} else {
			Log.d(TAG, "NET");
			gmp = 1;
			longitude.setVisibility(View.GONE);
			latitude.setVisibility(View.GONE);
			lattitle.setVisibility(View.GONE);
			longtitle.setVisibility(View.GONE);
			mMapView.setVisibility(View.VISIBLE);
			gpstime.setVisibility(View.INVISIBLE);
			if (gpstim != 0) {
				gpstim = gpstim;
				Log.d(TAG, "GPSTime" + gpstim);
				Date date = new Date(gpstim);
				SimpleDateFormat sdf = new SimpleDateFormat("MM/d H:mm "); // the
																			// format
																			// of
																			// your
																			// date
				formattedDate = sdf.format(date);

			} else {
				formattedDate = "No Time";
			}
			mMapView.onCreate(savedInstanceState);
			mMapView.onResume();
			try {
				MapsInitializer.initialize(getActivity()
						.getApplicationContext());
			} catch (Exception e) {
				e.printStackTrace();
			}
			googleMap = mMapView.getMap();
			LatLng location = new LatLng(lat, Long);
			if (conn == 1) {
				MarkerOptions marker = new MarkerOptions()
						.position(location)
						.title("Arrival time")
						.snippet("Battery:" + level + " " + formattedDate)
						.icon(BitmapDescriptorFactory
								.fromResource(R.drawable.ww));
				googleMap.addMarker(marker);
			}
			if (conn == 2) {
				MarkerOptions marker = new MarkerOptions()
						.position(location)
						.title("Arrival time")
						.snippet("Battery:" + level + " " + formattedDate)
						.icon(BitmapDescriptorFactory
								.fromResource(R.drawable.gg));
				googleMap.addMarker(marker);
			}
			CameraUpdate update = CameraUpdateFactory.newLatLngZoom(location,
					16);
			googleMap.animateCamera(update);
		}
		return thispage;
	}

	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId() == R.id.info) {
			Log.d(TAG, "Click");
			Intent intent = new Intent(getActivity(), google_map.class);
			startActivity(intent);
			getActivity().overridePendingTransition(R.anim.pull_in_right,
					R.anim.push_out_right);
		}

	}

	@Override
	public void onResume() {
		super.onResume();
		if (gmp == 1) {
			mMapView.onResume();
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		if (gmp == 1) {
			mMapView.onPause();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (gmp == 1) {
			mMapView.onDestroy();
		}
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		if (gmp == 1) {
			mMapView.onLowMemory();
		}
	}

}
