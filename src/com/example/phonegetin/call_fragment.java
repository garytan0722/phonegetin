package com.example.phonegetin;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.SeriesSelection;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import com.example.phonegetin.R.id;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
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
import android.widget.Toast;

public class call_fragment extends Fragment implements OnClickListener {
	private String TAG = "call_fragment";
	private View thispage;
	private LinearLayout callchart;
	private CategorySeries Series;
	private static int[] COLORS = new int[] { Color.parseColor("#23b216"),
			Color.parseColor("#b22e16"), Color.parseColor("#166fb2") };
	private String[] ComNAME_LIST = new String[] { "Get Call", "Miss Call",
			"Out Call" };
	private int get, miss, out;
	private DefaultRenderer Renderer;
	public double[] VALUES;
	private GraphicalView ChartView;
	private SharedPreferences data;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		data = getActivity().getSharedPreferences("Data",
				getActivity().MODE_PRIVATE);

	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView");
		thispage = inflater.inflate(R.layout.home_call, container, false);
		callchart = (LinearLayout) thispage.findViewById(R.id.callchart);
		Button info = (Button) thispage.findViewById(id.info);
		info.setOnClickListener(this);
		Series = new CategorySeries("");
		Renderer = new DefaultRenderer();
		ChartView = null;
		callchart.removeAllViews();
		setcall_layout();
		return thispage;
	}

	public void setcall_layout() {
		Log.d(TAG, "Set call");
		TextView callcom = (TextView) thispage.findViewById(R.id.callcom);
		// get=data.getInt("get", 0);
		// miss=data.getInt("miss", 0);
		// out=data.getInt("out", 0);
		Log.d(TAG, "miss!!!!!" + miss + "get!!!!" + get + "out!!!!!" + out);
		TextView typrtitle = (TextView) thispage.findViewById(R.id.type);
		Typeface textstyle = Typeface.createFromAsset(
				getActivity().getAssets(),
				"Bachelor Pad Expanded JL Italic.ttf");
		typrtitle.setTypeface(textstyle);
		callcom.setTypeface(textstyle);
		TelephonyManager telManager = (TelephonyManager) getActivity()
				.getSystemService(Context.TELEPHONY_SERVICE);
		int sim = telManager.getSimState();
		String com = telManager.getNetworkOperatorName();
		if (sim == 0) {
			callcom.setText("UnKnown SIM");
		}
		if (sim == 1) {
			callcom.setText("NO SIM");
		} else {
			callcom.setText(com);
		}
		get = 1;
		miss = 2;
		out = 2;

		drawcall();

	}

	public void drawcall() {
		Log.d(TAG, "draw~!!!!~~!call");

		Renderer.setPanEnabled(false);
		Renderer.setShowLabels(false);
		Renderer.setDisplayValues(false);
		Renderer.setShowLegend(true);
		Renderer.setScale((float) 1.3);
		Renderer.setLegendTextSize(25);
		Renderer.setPanEnabled(false);
		Renderer.setZoomEnabled(true);
		Renderer.setZoomButtonsVisible(false);
		Renderer.setStartAngle(90);
		Renderer.setInScroll(false);
		Renderer.setClickEnabled(true);
		Renderer.setSelectableBuffer(10);
		VALUES = new double[] { get, miss, out };
		for (int i = 0; i < VALUES.length; i++) {
			Series.add(ComNAME_LIST[i] + " " + VALUES[i], VALUES[i]);
			SimpleSeriesRenderer renderer = new SimpleSeriesRenderer();
			renderer.setColor(COLORS[(Series.getItemCount() - 1)
					% COLORS.length]);
			Renderer.addSeriesRenderer(renderer);

		}
		if (ChartView == null) {
			ChartView = ChartFactory.getPieChartView(getActivity()
					.getApplicationContext(), Series, Renderer);
			ChartView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					SeriesSelection seriesSelection = ChartView
							.getCurrentSeriesAndPoint();
					Log.d(TAG, "Set Listener");
					
					if (seriesSelection == null) {
						
					} else {
						if (seriesSelection.getPointIndex() == 0) {
							Toast.makeText(
									getActivity(),
									"Get Call :"
											+ (int) seriesSelection.getValue()
											+ " times", Toast.LENGTH_SHORT)
									.show();
						} else if (seriesSelection.getPointIndex() == 1) {
							Toast.makeText(
									getActivity(),
									"Miss Call:"
											+ (int) seriesSelection.getValue()
											+ " times", Toast.LENGTH_SHORT)
									.show();
						} else if (seriesSelection.getPointIndex() == 2) {
							Toast.makeText(
									getActivity(),
									"Out Call:"
											+ (int) seriesSelection.getValue()
											+ " times", Toast.LENGTH_SHORT)
									.show();
						}
					}
				}
			});
			callchart.addView(ChartView, new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

		} else {
			ChartView.repaint();
		}

	}

	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId() == R.id.info) {
			Log.d(TAG, "Click");
			Intent intent = new Intent(getActivity(), call_tab.class);
			startActivity(intent);
			getActivity().overridePendingTransition(R.anim.pull_in_right,
					R.anim.push_out_right);
		}
	}

}
