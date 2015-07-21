package com.example.phonegetin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class battery_chart extends Fragment {

	private static String TAG = "battery_chart";
	private String ret_data;
	private View thispage;
	private List<double[]> yvalues = new ArrayList<double[]>();
	private List<Date[]> xvalues = new ArrayList<Date[]>();;
	private SharedPreferences share;
	private int batsize, timesize;
	private static int error;
	private ProgressDialog dialog;
	private int Max = 100;
	private int currentprogress = 0;
	private LinearLayout chart_layout;
	XYMultipleSeriesDataset dataset;
	XYSeriesRenderer r;
	XYMultipleSeriesRenderer renderer;
	View chart;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		share = getActivity().getSharedPreferences("Data",
				getActivity().MODE_PRIVATE);
		dialog = new ProgressDialog(getActivity());
		dialog.setTitle("Download");
		dialog.setMessage("loading...");
		dialog.setProgress(currentprogress);
		dialog.setCancelable(false);
		dialog.show();
		new postrequest().execute();

	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView");
		new postrequest().execute();
		thispage = inflater.inflate(R.layout.battery_chart, container, false);
		chart_layout = (LinearLayout) thispage.findViewById(R.id.chart);
		dataset = new XYMultipleSeriesDataset();
		r = new XYSeriesRenderer();
		renderer = new XYMultipleSeriesRenderer();
		chart = null;
		chart_layout.removeAllViews();
		return thispage;
	}

	public void drawchart() {

		String[] titles = new String[] { "Battery" };
		int[] colors = new int[] { Color.GREEN };
		PointStyle[] styles = new PointStyle[] { PointStyle.CIRCLE };
		XYMultipleSeriesRenderer renderer = buildRenderer(colors, styles);
		int length = renderer.getSeriesRendererCount();
		for (int i = 0; i < length; i++) {
			((XYSeriesRenderer) renderer.getSeriesRendererAt(i))
					.setFillPoints(true);
		}

		setChartSettings(renderer, "Battery Chart", "Time", "Battery level",
				xvalues.get(0)[0].getTime(),
				xvalues.get(0)[timesize - 1].getTime(), 0, 100, Color.RED,
				Color.WHITE);
		chart = ChartFactory.getTimeChartView(getActivity(),
				buildDateDataset(titles, xvalues, yvalues), renderer,
				"MMMMd HH:mm ");
		chart_layout.addView(chart);

	}

	protected void setChartSettings(XYMultipleSeriesRenderer renderer,
			String title, String xTitle, String yTitle, double xMin,
			double xMax, double yMin, double yMax, int axesColor,
			int labelsColor) {
		renderer.setChartTitle(title);
		renderer.setXTitle(xTitle);
		renderer.setXLabelsColor(Color.parseColor("#e3a724"));
		renderer.setYLabelsColor(0, Color.parseColor("#e324cd"));
		renderer.setGridColor(Color.WHITE);
		renderer.setApplyBackgroundColor(true);
		renderer.setLabelsTextSize(20);
		renderer.setYLabelsAlign(Align.LEFT);
		renderer.setXLabelsAlign(Align.CENTER);
		renderer.setAxisTitleTextSize(20);
		renderer.setChartTitleTextSize(30);
		renderer.setBackgroundColor(Color.BLACK);
		renderer.setShowGrid(true);
		renderer.setLegendTextSize(25);
		renderer.setPointSize(5f);
		renderer.setYTitle(yTitle);
		renderer.setXAxisMin(xMin);
		renderer.setXAxisMax(xMax);
		renderer.setYAxisMin(yMin);
		renderer.setYAxisMax(yMax);
		renderer.setAxesColor(axesColor);
		renderer.setLabelsColor(labelsColor);
	}

	protected XYMultipleSeriesDataset buildDateDataset(String[] titles,
			List<Date[]> xValues, List<double[]> yvalue) {

		int length = titles.length;
		for (int i = 0; i < length; i++) {
			TimeSeries series = new TimeSeries(titles[i]);
			Date[] xV = xValues.get(i);
			double[] yV = yvalue.get(i);
			int seriesLength = xV.length;
			for (int k = 0; k < seriesLength; k++) {
				series.add(xV[k], yV[k]);
			}
			dataset.addSeries(series);
		}
		return dataset;
	}

	protected XYMultipleSeriesRenderer buildRenderer(int[] colors,
			PointStyle[] styles) {

		setRenderer(renderer, colors, styles);
		return renderer;
	}

	protected void setRenderer(XYMultipleSeriesRenderer renderer, int[] colors,
			PointStyle[] styles) {

		renderer.setMargins(new int[] { 20, 30, 15, 20 });
		int length = colors.length;
		for (int i = 0; i < length; i++) {

			r.setColor(colors[i]);
			r.setPointStyle(styles[i]);
			renderer.addSeriesRenderer(r);
		}
	}

	class postrequest extends AsyncTask<Integer, Integer, String> {
		@Override
		protected String doInBackground(Integer... countTo) {
			// TODO Auto-generated method stub

			try {
				String req = share.getString("uid", null);
				request_function(req);
				error = 0;

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
				error = 1;
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
			dialog.setProgress(values[0]);
			super.onProgressUpdate(values);
			Log.d(TAG, " posting");
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			Log.d(TAG, "Return::::::" + ret_data);
			dialog.dismiss();
			if (error == 1) {
				Builder HelpDialog = new AlertDialog.Builder(getActivity());
				HelpDialog.setTitle("Message");
				HelpDialog.setMessage("Please Check your Internet");
				DialogInterface.OnClickListener OnClick = new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
						getActivity().finish();
					}
				};
				DialogInterface.OnClickListener OnClick1 = new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent();
						intent.setClass(getActivity(), homepager.class);
						startActivity(intent);
						getActivity().finish();

					}
				};
				HelpDialog.setNeutralButton("Go to Check", OnClick);
				HelpDialog.setPositiveButton("Later", OnClick1);
				HelpDialog.show();
			}
			if (ret_data != null) {
				try {

					JSONObject js = new JSONObject(ret_data);
					JSONArray jsonArray = js.getJSONArray("battery")
							.getJSONArray(0);
					JSONArray jsonArray1 = js.getJSONArray("time")
							.getJSONArray(0);
					Log.d(TAG, "Battery array:" + jsonArray);
					Log.d(TAG, "TimeArray:" + jsonArray1);
					batsize = jsonArray.length();
					Log.d(TAG, "Batsize:" + batsize);
					timesize = jsonArray1.length();
					Log.d(TAG, "Timesize:" + timesize);
					double[] data = new double[batsize];
					Log.d(TAG, "Error1");
					Date[] utime = new Date[timesize];
					for (int i = 0; i < jsonArray.length(); i++) {

						String level = jsonArray.get(i).toString();
						String time = jsonArray1.get(i).toString();
						data[i] = Double.valueOf(level);
						long ti = Long.valueOf(time);
						Log.d(TAG, "Time" + ti);
						utime[i] = new Date(ti * 1000);
						Log.d(TAG, "utime:" + utime[i]);
					}

					yvalues.add(data);
					xvalues.add(utime);
					drawchart();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}

		@Override
		protected void onCancelled() {
			// TODO Auto-generated method stub
			super.onCancelled();
		}
	}

	public String request_function(String req) throws KeyStoreException,
			IOException, KeyManagementException, NoSuchAlgorithmException,
			UnrecoverableKeyException {
		Log.d(TAG, "Fuction!!!!!");
		String url = "https://nrl.cce.mcu.edu.tw/pgi/admin/batchart.php";
		InputStream instream = getActivity().getResources().openRawResource(
				R.raw.syl);

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

		List<NameValuePair> params = new ArrayList<NameValuePair>();

		params.add(new BasicNameValuePair("uid", req));

		Log.d(TAG, "DATA........." + req);

		HttpClient client = new DefaultHttpClient();
		client.getConnectionManager().getSchemeRegistry().register(sch);
		HttpPost request = new HttpPost(url);

		request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));

		HttpResponse response = client.execute(request);
		HttpEntity resEntity = response.getEntity();
		ret_data = EntityUtils.toString(resEntity);
		return ret_data;
	}

}
