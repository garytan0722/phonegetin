package com.example.phonegetin;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.chart.BarChart;
import org.achartengine.chart.LineChart;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
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

import com.example.phonegetin.battery_chart.postrequest;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class net_chart extends Fragment {
	private static String TAG = "net_chart";
	private String ret_data;
	private List<double[]> yvalues = new ArrayList<double[]>();
	private List<Date[]> xvalues = new ArrayList<Date[]>();;
	private SharedPreferences share;
	private int netsize, timesize;
	private XYSeriesRenderer txBarRenderer, rxBarRenderer, rxLineRenderer;
	private XYMultipleSeriesRenderer multiRenderer;
	private double[] rx;
	private double[] tx;
	private Date[] utime;
	private String day[];
	private int margins[] = { 20, 30, 15, 20 };
	private static int error;
	private ProgressDialog dialog;
	private View thispage;
	private LinearLayout chart_layout;
	View chart;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		share = getActivity().getSharedPreferences("Data",
				getActivity().MODE_PRIVATE);
		dialog = new ProgressDialog(getActivity());
		dialog.setTitle("Download");
		dialog.setMessage("loading...");
		dialog.setCancelable(true);
		dialog.show();
		new postrequest().execute();
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView");
		new postrequest().execute();
		thispage = inflater.inflate(R.layout.net_chart, container, false);
		chart_layout = (LinearLayout) thispage.findViewById(R.id.chart);
		chart = null;
		chart_layout.removeAllViews();
		return thispage;
	}

	public void drawchart() {
		String[] titles = new String[] { "Receiver", "Translate" };
		int[] colors = new int[] { Color.GREEN, Color.RED };
		PointStyle[] styles = new PointStyle[] { PointStyle.SQUARE,
				PointStyle.SQUARE };
		XYMultipleSeriesRenderer renderer = buildRenderer(colors, styles);
		int length = renderer.getSeriesRendererCount();
		for (int i = 0; i < length; i++) {
			((XYSeriesRenderer) renderer.getSeriesRendererAt(i))
					.setFillPoints(true);
		}

		setChartSettings(renderer, "Net Traffic Chart", "Time",
				"Net traffic in KB", xvalues.get(0)[0].getTime(),
				xvalues.get(0)[timesize - 1].getTime(), 0, 1000, Color.RED,
				Color.WHITE);
		chart = ChartFactory.getTimeChartView(getActivity(),
				buildDateDataset(titles, xvalues, yvalues), renderer,
				"MMMMd HH:mm ");
		chart_layout.addView(chart);
	}

	protected XYMultipleSeriesDataset buildDateDataset(String[] titles,
			List<Date[]> xValues, List<double[]> yvalue) {
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		int length = titles.length;
		Log.d(TAG, "size" + length);
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
		renderer.setLegendTextSize(15);
		renderer.setPointSize(5f);
		renderer.setYTitle(yTitle);
		renderer.setXAxisMin(xMin);
		renderer.setXAxisMax(xMax);
		renderer.setYAxisMin(yMin);
		renderer.setYAxisMax(yMax);
		renderer.setAxesColor(axesColor);
		renderer.setLabelsColor(labelsColor);
	}

	protected XYMultipleSeriesRenderer buildRenderer(int[] colors,
			PointStyle[] styles) {
		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
		setRenderer(renderer, colors, styles);
		return renderer;
	}

	protected void setRenderer(XYMultipleSeriesRenderer renderer, int[] colors,
			PointStyle[] styles) {

		renderer.setMargins(new int[] { 20, 30, 15, 20 });
		int length = colors.length;
		for (int i = 0; i < length; i++) {
			XYSeriesRenderer r = new XYSeriesRenderer();
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
					}
				};
				DialogInterface.OnClickListener OnClick1 = new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent();
						intent.setClass(getActivity(), homepager.class);
						startActivity(intent);
					}
				};
				HelpDialog.setNeutralButton("Go to Check", OnClick);
				HelpDialog.setPositiveButton("Later", OnClick1);
				HelpDialog.show();
			}

			if (ret_data != null) {
				try {

					JSONObject js = new JSONObject(ret_data);
					JSONArray jsonArray = js.getJSONArray("rx").getJSONArray(0);
					JSONArray jsonArray1 = js.getJSONArray("tx")
							.getJSONArray(0);
					JSONArray jsonArray2 = js.getJSONArray("time")
							.getJSONArray(0);
					Log.d(TAG, "RX array:" + jsonArray);
					Log.d(TAG, "TX Array:" + jsonArray1);
					netsize = jsonArray.length();
					Log.d(TAG, "Netsize:" + netsize);
					timesize = jsonArray2.length();
					Log.d(TAG, "Timesize:" + timesize);
					rx = new double[netsize];
					tx = new double[netsize];
					utime = new Date[timesize];
					for (int i = 0; i < jsonArray.length(); i++) {

						String rex = jsonArray.get(i).toString();
						String trx = jsonArray1.get(i).toString();
						String time = jsonArray2.get(i).toString();
						if (Double.valueOf(rex) != 0) {
							rx[i] = Double.valueOf(rex) / 1024;
							tx[i] = Double.valueOf(trx) / 1024;
						} else {
							rx[i] = Double.valueOf(rex);
							tx[i] = Double.valueOf(trx);
						}
						long ti = Long.valueOf(time);
						Log.d(TAG, "Time" + ti);
						utime[i] = new Date(ti * 1000);
						Log.d(TAG, "Time:" + utime[i]);
					}
					yvalues.add(rx);
					yvalues.add(tx);
					Log.d(TAG, "Error...." + yvalues.size());
					xvalues.add(utime);
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
		String url = "https://nrl.cce.mcu.edu.tw/pgi/admin/netchart.php";
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
