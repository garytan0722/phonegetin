package com.example.phonegetin;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.SeriesSelection;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;
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

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.support.v4.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewParent;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.phonegetin.CircularProgressBar.ProgressAnimationListener;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;

@TargetApi(16)
public class phoneinfo extends Fragment {
	private Handler mHandler = new Handler();
	private View thispage;
	private static final String TAG = "phoneinfo";
	private String RAMTotal, RAMFree, Memfree, Memtotal, Memfreembstring,
			Memtotalmbstring, Version, phonemodelstring;
	private Intent home_batteryIntent;
	private Build PhoneModel;
	private long mStartTX = 0, mStartRX = 0, longMemtotal, longMemfree,
			longMembusy, Memfreemb, Memtotalmb, Membusymb;
	private Typeface EC, Ti, all_title, Datatext;
	private TextView phone_model, phone_version, phone_versiontitle,
			phone_Memfree, phone_Memtotal, phone_totaldisk, phone_freedisk,
			phone_totaldisktitle, phone_freedisktitle;

	private static int[] COLORS = new int[] { Color.parseColor("#30ad36"),
			Color.parseColor("#9e0b2d") };
	private double Total, Free;
	public static double[] diskVALUES;
	public static double[] MemVALUES;
	private static String[] diskNAME_LIST = new String[] { "Free Disk",
			"Use Disk" };
	private static String[] MemNAME_LIST = new String[] { "Free Mem", "Use Mem" };
	private CircularProgressBar c2;
	private CategorySeries diskSeries;
	private CategorySeries MemSeries;
	private DefaultRenderer diskRenderer;
	private DefaultRenderer MemRenderer;
	private GraphicalView MemChartView;
	private GraphicalView diskChartView;
	private LinearLayout disklayout, memlayout;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "Oncreate");
		mStartRX = TrafficStats.getTotalRxBytes();
		mStartTX = TrafficStats.getTotalTxBytes();

	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView");
		thispage = inflater.inflate(R.layout.phoneinfo, container, false);
		memlayout = (LinearLayout) thispage.findViewById(R.id.Memchart);
		disklayout = (LinearLayout) thispage.findViewById(R.id.diskchart);
		c2 = null;
		diskSeries = new CategorySeries("");
		diskRenderer = new DefaultRenderer();
		MemSeries = new CategorySeries("");
		MemRenderer = new DefaultRenderer();
		MemChartView = null;
		diskChartView = null;
		disklayout.removeAllViews();
		memlayout.removeAllViews();
		textstyle();
		getphone_version();
		phone_model();
		getRAMInfo();
		drawMem();
		Memviewclick();
		getdisk();
		drawdisk();
		diskviewclick();
		return thispage;
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.d(TAG, "onActivityCreate");

	}

	public void phone_model() {
		phonemodelstring = PhoneModel.MODEL;
		Log.d(TAG, "Phone Model:" + phonemodelstring);
		phone_model.setText(phonemodelstring);
	}

	public void getphone_version() {
		Version = android.os.Build.VERSION.RELEASE;
		phone_version.setText("Android" + " " + Version);
	}

	public void drawMem() {
		Log.d(TAG, "draw~!!!!~~!Mem");
		MemRenderer.setPanEnabled(false);
		MemRenderer.setShowLabels(false);
		MemRenderer.setDisplayValues(false);
		MemRenderer.setShowLegend(true);
		MemRenderer.setScale((float) 1.2);
		MemRenderer.setLegendTextSize(25);
		MemRenderer.setMargins(new int[] { 10, 80, 15, 0 });
		MemRenderer.setLegendHeight(200);
		MemRenderer.setZoomEnabled(true);
		MemRenderer.setZoomButtonsVisible(false);
		MemRenderer.setStartAngle(90);
		MemRenderer.setInScroll(false);
		MemVALUES = new double[] { Memfreemb, Membusymb };
		for (int i = 0; i < MemVALUES.length; i++) {
			MemSeries.add(MemNAME_LIST[i] + " " + MemVALUES[i], MemVALUES[i]);
			SimpleSeriesRenderer renderer = new SimpleSeriesRenderer();
			renderer.setColor(COLORS[(MemSeries.getItemCount() - 1)
					% COLORS.length]);
			MemRenderer.addSeriesRenderer(renderer);
		}
		if (MemChartView != null) {

			MemChartView.repaint();
		}
	}

	public void Memviewclick() {
		if (MemChartView == null) {
			Log.d(TAG, "MemChartView null");
			MemChartView = ChartFactory.getPieChartView(getActivity()
					.getApplicationContext(), MemSeries, MemRenderer);
			MemRenderer.setClickEnabled(true);
			MemRenderer.setSelectableBuffer(10);
			MemChartView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					SeriesSelection seriesSelection = MemChartView
							.getCurrentSeriesAndPoint();

					if (seriesSelection == null) {
						
					} else {
						if (seriesSelection.getPointIndex() + 1 == 1) {
							Toast.makeText(
									getActivity(),
									"Free Mem:" + seriesSelection.getValue()
											+ "MB" + "\n" + "Proportion:"
											+ Memfreemb * 100 / Memtotalmb
											+ "%", Toast.LENGTH_SHORT).show();
						} else if (seriesSelection.getPointIndex() + 1 == 2) {
							Toast.makeText(
									getActivity(),
									"Use Mem:" + seriesSelection.getValue()
											+ "MB" + "\n" + "Proportion:"
											+ Membusymb * 100 / Memtotalmb
											+ "%", Toast.LENGTH_SHORT).show();
						}
					}
				}
			});

			memlayout.addView(MemChartView, new LayoutParams(
					LayoutParams.MATCH_PARENT, 500));

		}

		else {
			MemChartView.repaint();
		}

	}

	public void drawdisk() {
		Log.d(TAG, "diskDrawer");
		diskRenderer.setPanEnabled(false);
		diskRenderer.setShowLabels(false);
		diskRenderer.setDisplayValues(false);
		diskRenderer.setShowLegend(true);
		diskRenderer.setScale((float) 1.2);
		diskRenderer.setLegendTextSize(25);
		diskRenderer.setMargins(new int[] { 10, 80, 15, 0 });
		diskRenderer.setLegendHeight(200);
		diskRenderer.setZoomEnabled(true);
		diskRenderer.setZoomButtonsVisible(false);
		diskRenderer.setStartAngle(90);
		diskRenderer.setInScroll(false);
		diskVALUES = new double[] { getFreeDisk(), getBusyDisk() };
		for (int i = 0; i < diskVALUES.length; i++) {
			diskSeries.add(diskNAME_LIST[i] + " " + diskVALUES[i],
					diskVALUES[i]);
			SimpleSeriesRenderer renderer = new SimpleSeriesRenderer();
			renderer.setColor(COLORS[(diskSeries.getItemCount() - 1)
					% COLORS.length]);
			diskRenderer.addSeriesRenderer(renderer);
		}
		if (diskChartView != null) {
			Log.d(TAG, "chartview not null");
			diskChartView.invalidate();
			diskChartView.repaint();
		}
	}

	public void diskviewclick() {
		if (diskChartView == null) {

			diskChartView = ChartFactory.getPieChartView(getActivity(),
					diskSeries, diskRenderer);
			diskRenderer.setClickEnabled(true);
			diskRenderer.setSelectableBuffer(10);

			diskChartView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					SeriesSelection seriesSelection = diskChartView
							.getCurrentSeriesAndPoint();

					if (seriesSelection == null) {
						
					} else {
						if (seriesSelection.getPointIndex() + 1 == 1) {
							Toast.makeText(
									getActivity(),
									"Free Disk Space:"
											+ seriesSelection.getValue()
											+ "MB"
											+ "\n"
											+ "Proportion:"
											+ Math.round((getFreeDisk() * 100)
													/ getTotalDisk()) + "%",
									Toast.LENGTH_SHORT).show();
						} else if (seriesSelection.getPointIndex() + 1 == 2) {
							Toast.makeText(
									getActivity(),
									"Use Disk Space:"
											+ seriesSelection.getValue()
											+ "MB"
											+ "\n"
											+ "Proportion:"
											+ Math.round((getBusyDisk() * 100)
													/ getTotalDisk()) + "%",
									Toast.LENGTH_SHORT).show();
						}
					}
				}
			});

			disklayout.addView(diskChartView, new LayoutParams(
					LayoutParams.MATCH_PARENT, 500));
		} else {
			diskChartView.repaint();
		}

	}

	public void getRAMInfo() {
		RandomAccessFile reader = null;
		RAMTotal = null;
		RAMFree = null;
		try {
			reader = new RandomAccessFile("/proc/meminfo", "r");
			RAMTotal = reader.readLine();
			RAMFree = reader.readLine();
			reader.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {

		}
		String[] total = RAMTotal.split(":");
		String[] total1 = total[1].trim().split(" ");
		Memtotal = total1[0];
		longMemtotal = Long.valueOf(Memtotal);
		String[] free = RAMFree.split(":");
		String[] free1 = free[1].trim().split(" ");
		Memfree = free1[0];
		longMemfree = Long.valueOf(Memfree);
		longMembusy = longMemtotal - longMemfree;
		Log.d(TAG, "Free~~~~" + longMemfree);
		Log.d(TAG, "Total~~~~" + longMemtotal);
		Log.d(TAG, "Busy~~~~" + longMembusy);
		Memfreemb = longMemfree / 1024;
		Memtotalmb = longMemtotal / 1024;
		value.settotalMem(Memtotalmb);
		Membusymb = longMembusy / 1024;
		Memfreembstring = String.valueOf(Memfreemb);
		Memtotalmbstring = String.valueOf(Memtotalmb);
		phone_Memtotal.setText(Memtotalmbstring + " " + "MB");
		phone_Memfree.setText(Memfreembstring + " " + "MB");
	}

	public void getdisk() {
		phone_totaldisk.setText(Double.toString(getTotalDisk()) + " " + "MB");
		phone_freedisk.setText(Double.toString(getFreeDisk()) + " " + "MB");
	}

	public double getTotalDisk() {
		StatFs statFs = new StatFs(Environment.getDataDirectory()
				.getAbsolutePath());
		Total = ((long) statFs.getBlockCount() * (long) statFs.getBlockSize()) / 1048576;// mb
		Log.d(TAG, "Total Disk: " + Double.toString(Total));
		value.settotaldisk(Total);
		return Total;
	}

	public double getFreeDisk() {
		StatFs statFs = new StatFs(Environment.getDataDirectory()
				.getAbsolutePath());
		Free = ((long) statFs.getAvailableBlocks() * (long) statFs
				.getBlockSize()) / 1048576;// mb
		Log.d(TAG, "Free Disk: " + Double.toString(Free));
		return Free;
	}

	public double getBusyDisk() {
		StatFs statFs = new StatFs(Environment.getDataDirectory()
				.getAbsolutePath());

		double Busy = Total - Free;
		Log.d(TAG, "Used Disk: " + Double.toString(Busy));
		return Busy;
	}

	public void textstyle() {

		TextView memtitletotal = (TextView) thispage
				.findViewById(R.id.Mem_totaltitle);
		TextView memtitlefree = (TextView) thispage
				.findViewById(R.id.Mem_freetitle);
		phone_model = (TextView) thispage.findViewById(R.id.phone_model);
		phone_versiontitle = (TextView) thispage
				.findViewById(R.id.os_versiontitle);
		phone_version = (TextView) thispage.findViewById(R.id.os_version);
		phone_Memtotal = (TextView) thispage.findViewById(R.id.Mem_total);
		phone_Memfree = (TextView) thispage.findViewById(R.id.Mem_free);
		phone_totaldisktitle = (TextView) thispage
				.findViewById(R.id.total_disktitle);
		phone_totaldisk = (TextView) thispage.findViewById(R.id.total_disk);
		phone_freedisktitle = (TextView) thispage
				.findViewById(R.id.free_disktitle);
		phone_freedisk = (TextView) thispage.findViewById(R.id.free_disk);
		Datatext = Typeface.createFromAsset(getActivity().getAssets(),
				"Autumn Regular.ttf");
		EC = Typeface.createFromAsset(getActivity().getAssets(),
				"Autumn Regular.ttf");
		Ti = Typeface.createFromAsset(getActivity().getAssets(),
				"balonez fantasia.ttf");
		phone_model.setTypeface(Datatext);

		phone_version.setTypeface(Datatext);
		phone_totaldisk.setTypeface(Datatext);
		phone_freedisk.setTypeface(Datatext);
		phone_Memfree.setTypeface(Datatext);
		phone_Memtotal.setTypeface(Datatext);
		phone_versiontitle.setTypeface(Datatext);
		phone_freedisktitle.setTypeface(Datatext);
		phone_totaldisktitle.setTypeface(Datatext);
		memtitlefree.setTypeface(Datatext);
		memtitletotal.setTypeface(Datatext);
	}

}
