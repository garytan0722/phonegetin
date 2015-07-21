package com.example.phonegetin;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DialRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.DialRenderer.Type;

import com.example.phonegetin.R.id;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class net_fragment extends Fragment implements OnClickListener {
	private String TAG = "net_fragment";
	private View thispage;
	private LinearLayout netchart;
	private long data_rx = 0;
	private long data_tx = 0;
	private int conn;
	private static String down = "Receiver", up = "Translate";
	private long rxBytes, txBytes;
	private int max = 100, min = 0;
	private SharedPreferences data;
	private GraphicalView chart;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		data = getActivity().getSharedPreferences("Data",
				getActivity().MODE_PRIVATE);

	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView");
		thispage = inflater.inflate(R.layout.home_network, container, false);
		setnetlayout();
		return thispage;

	}

	public void setnetlayout() {
		Log.d(TAG, "Set net");
		netchart = (LinearLayout) thispage.findViewById(R.id.netchart);
		ImageView type_image = (ImageView) thispage
				.findViewById(R.id.type_image);
		TextView typetitle = (TextView) thispage.findViewById(R.id.typetitle);
		TextView typ = (TextView) thispage.findViewById(R.id.type);
		Typeface textstyle = Typeface.createFromAsset(
				getActivity().getAssets(),
				"Bachelor Pad Expanded JL Italic.ttf");
		typetitle.setTypeface(textstyle);
		typ.setTypeface(textstyle);
		Button info = (Button) thispage.findViewById(id.info);
		info.setOnClickListener(this);
		drawnetchart();
		conn = get_Traffic(getActivity().getApplicationContext());
		data_rx = rxBytes;
		data_tx = txBytes;
		Log.d(TAG, "RXXXX" + data_rx);
		Log.d(TAG, "TXXXXXX" + data_tx);
		if (data_rx < 1024 || data_tx < 1024) {
			Log.d(TAG, "Bytes");
			max = 100;
			down = "Receiver Bytes";
			up = "Translate Bytes";
			netchart.removeAllViews();
			drawnetchart();

		}
		if (data_rx >= 1048576 || data_tx >= 1048576) {
			Log.d(TAG, "MB");
			data_rx = data_rx / 1048576;
			data_tx = data_tx / 1048576;
			if (data_rx > 100) {
				max = 500;
			}
			down = "Receiver MB";
			up = "Translate MB";
			netchart.removeAllViews();
			drawnetchart();
		}
		if (data_rx >= 1024 || data_tx >= 1024) {
			Log.d(TAG, "KB");
			data_rx = data_rx / 1024;
			data_tx = data_tx / 1024;
			max = 100;
			down = "Receiver KB";
			up = "Translate KB";
			netchart.removeAllViews();
			drawnetchart();

		}

		if (conn == 1) {
			typ.setText("WiFi");
			type_image.setBackgroundResource(R.drawable.wifi_icon);
		} else if (conn == 2) {
			typ.setText("3G");
			type_image.setBackgroundResource(R.drawable.mobile);

		} else if (conn == 0) {
			typ.setText("NoNet");
			type_image.setBackgroundResource(R.drawable.nonet);
		} else if (conn == 3) {
			typ.setText("Searching");
			type_image.setBackgroundResource(R.drawable.searchnet);

		}

	}

	public int get_Traffic(Context context) {
		Log.d(TAG, " Conn_Traffic");
		long pastrx = data.getLong("homerx", 0);
		long pasttx = data.getLong("hometx", 0);
		SharedPreferences.Editor editor = data.edit();
		rxBytes = TrafficStats.getTotalRxBytes() - pastrx;
		txBytes = TrafficStats.getTotalTxBytes() - pasttx;
		editor.putLong("homerx", rxBytes);
		editor.putLong("hometx", txBytes);
		editor.commit();
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

	public void drawnetchart() {
		Log.d(TAG, "draw chart");
		CategorySeries category = new CategorySeries("Weight indic");
		Log.d(TAG, "Chart Rx" + data_rx);
		Log.d(TAG, "Chart Tx" + data_tx);
		category.add(down, data_rx);
		category.add(up, data_tx);
		DialRenderer renderer = new DialRenderer();
		renderer.setChartTitleTextSize(20);
		renderer.setLabelsTextSize(15);
		renderer.setLegendTextSize(15);
		renderer.setMargins(new int[] { 20, 10, 15, 0 });
		SimpleSeriesRenderer r = new SimpleSeriesRenderer();
		r.setColor(Color.RED);
		renderer.addSeriesRenderer(r);
		r = new SimpleSeriesRenderer();
		r = new SimpleSeriesRenderer();
		r.setColor(Color.GREEN);
		renderer.addSeriesRenderer(r);
		renderer.setLabelsTextSize(25);
		renderer.setLabelsColor(Color.BLACK);
		renderer.setShowLabels(true);
		renderer.setScale((float) 1.2);
		renderer.setInScroll(false);
		renderer.setPanEnabled(false);
		renderer.setZoomButtonsVisible(false);
		renderer.setVisualTypes(new DialRenderer.Type[] { Type.NEEDLE,
				Type.NEEDLE });
		renderer.setMinValue(min);
		renderer.setMaxValue(max);
		chart = ChartFactory.getDialChartView(getActivity()
				.getApplicationContext(), category, renderer);
		netchart.addView(chart, new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
	}

	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId() == R.id.info) {
			Log.d(TAG, "Click");
			Intent intent = new Intent(getActivity(), net_tab.class);
			startActivity(intent);
			getActivity().overridePendingTransition(R.anim.pull_in_right,
					R.anim.push_out_right);
		}
	}

}
