package com.example.phonegetin;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;
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

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

public class call_chart extends Fragment {
	private View thispage;
	private static String TAG = "call_chart";
	private LinearLayout chart_layout;
	private CategorySeries callSeries;
	private GraphicalView callChartView;
	private String[] ComNAME_LIST = new String[] { "CHT", "TW Mobile", "APT",
			"FET" };
	private SharedPreferences share;
	private String ret_data;
	private int error;
	private DefaultRenderer Renderer;
	public double[] VALUES;
	private GraphicalView ChartView;
	private CategorySeries Series;
	private int APT = 0, CHT = 0, TWMobile = 0, FET = 0;
	private ProgressDialog dialog;
	private static int[] COLORS = new int[] { Color.parseColor("#00b0b8"),
			Color.parseColor("#c04000"), Color.parseColor("#e58f23"),
			Color.parseColor("#e5232d") };

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
		thispage = inflater.inflate(R.layout.call_chart, container, false);
		chart_layout = (LinearLayout) thispage.findViewById(R.id.chart);
		Series = new CategorySeries("");
		Renderer = new DefaultRenderer();
		ChartView = null;
		chart_layout.removeAllViews();
		return thispage;
	}

	public void drawcall() {
		Log.d(TAG, "draw~!!!!~~!call");
		Renderer.setPanEnabled(false);
		Renderer.setShowLabels(false);
		Renderer.setDisplayValues(false);
		Renderer.setShowLegend(true);
		Renderer.setScale((float) 1.2);
		Renderer.setLegendTextSize(25);
		Renderer.setMargins(new int[] { 10, 80, 15, 0 });
		Renderer.setLegendHeight(200);
		Renderer.setZoomEnabled(true);
		Renderer.setZoomButtonsVisible(false);
		Renderer.setStartAngle(90);
		Renderer.setInScroll(false);
		VALUES = new double[] { CHT, TWMobile, APT, FET };
		for (int i = 0; i < VALUES.length; i++) {
			Series.add(ComNAME_LIST[i] + " " + VALUES[i], VALUES[i]);
			SimpleSeriesRenderer renderer = new SimpleSeriesRenderer();
			renderer.setColor(COLORS[(Series.getItemCount() - 1)
					% COLORS.length]);
			Renderer.addSeriesRenderer(renderer);

		}
		if (ChartView != null) {

			ChartView.repaint();
		}
		ChartView = ChartFactory.getPieChartView(getActivity()
				.getApplicationContext(), Series, Renderer);
		chart_layout.addView(ChartView, new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
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
			dialog.dismiss();
			Log.d(TAG, "Return::::::" + ret_data);
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
			Log.d(TAG, "Return:::" + ret_data);
			if (ret_data != null) {

				try {

					JSONObject js = new JSONObject(ret_data);
					JSONArray jsonArray = js.getJSONArray("number");
					JSONArray jsonArray1 = js.getJSONArray("company");
					JSONArray jsonArray2 = js.getJSONArray("type");
					JSONArray jsonArray3 = js.getJSONArray("phdate");
					Log.d(TAG, "number array:" + jsonArray);
					Log.d(TAG, "company Array:" + jsonArray1);
					Log.d(TAG, "type Array:" + jsonArray2);
					Log.d(TAG, "phdate Array:" + jsonArray3);
					int comsize = jsonArray.length();
					Log.d(TAG, "comsize:" + comsize);
					int phdatesize = jsonArray3.length();
					Log.d(TAG, "phdatesize:" + phdatesize);
					for (int i = 0; i < jsonArray.length(); i++) {
						String com = jsonArray1.get(i).toString();
						String time = jsonArray3.get(i).toString();
						if (com.equals("CHT")) {
							CHT += Integer.valueOf(time);

						} else if (com.equals("TW Mobile")) {
							TWMobile += Integer.valueOf(time);
						} else if (com.equals("APT")) {
							APT += Integer.valueOf(time);
						} else if (com.equals("FET")) {
							FET += Integer.valueOf(time);
						}
					}

					drawcall();
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
		String url = "https://nrl.cce.mcu.edu.tw/pgi/admin/call_chart.php";
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
