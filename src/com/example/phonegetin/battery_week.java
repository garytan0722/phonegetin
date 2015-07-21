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
import org.achartengine.chart.BarChart.Type;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer.Orientation;
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

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.graphics.Typeface;
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

public class battery_week extends Fragment{
	private static String TAG="battery_forecast";
	private View thispage;
	String[] titles = new String[] { "average" };
	 private View barChart;
	 private ArrayList<String[][]> average = new ArrayList<String[][]>();
	private LinearLayout chart;
	private SharedPreferences share;
	private static int error;
	private String ret_data;
	private ProgressDialog dialog;
	XYSeries Series;
	XYMultipleSeriesDataset Dataset;
	XYMultipleSeriesRenderer Renderer ;
	XYSeriesRenderer yRenderer ;
	View view;
	 public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		share= getActivity().getSharedPreferences("Data",getActivity().MODE_PRIVATE);
		
		
	}
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		Log.d(TAG,"onCreateView");	
		new postrequest().execute();
		thispage = inflater.inflate(R.layout.battery_week, container, false);
		chart=(LinearLayout)thispage.findViewById(R.id.chart);
		Series = new XYSeries( "Charge Battery times");
		 Dataset = new XYMultipleSeriesDataset(); 
		 Renderer = new XYMultipleSeriesRenderer();
		 yRenderer = new XYSeriesRenderer();
		 chart.removeAllViews();
		 view=null;
		return thispage;
	}
	public void drawbarchart()
	{
		barChart = getBarChart("Battery Charge times in Week", average);
		chart.addView(barChart, new LayoutParams(
				LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
	}
	private View getBarChart(String chartTitle, ArrayList<String[][]> xy)
	{
					
		 
		Dataset.addSeries(Series);
		 Renderer.addSeriesRenderer(yRenderer);
		Renderer.setMarginsColor(Color.WHITE);             
		Renderer.setTextTypeface(null, Typeface.BOLD);      
		Renderer.setShowGrid(true);                         
		Renderer.setGridColor(Color.GRAY);                  
		Renderer.setChartTitle(chartTitle);                
		Renderer.setLabelsColor(Color.BLACK);              
		Renderer.setChartTitleTextSize(20);                 
		Renderer.setAxesColor(Color.BLACK);                 
		Renderer.setBarSpacing(1);							
		Renderer.setDisplayValues(true);
		Renderer.setXTitle("week");                       
		Renderer.setYTitle("times");     					
		yRenderer.setChartValuesTextSize(15);
		Renderer.setAxisTitleTextSize(40);
		Renderer.setBarWidth(18);
		Renderer.setLabelsTextSize(15);
		Renderer.setXLabelsColor(Color.BLACK);             
		Renderer.setYLabelsColor(0, Color.BLACK);           
		Renderer.setXLabelsAlign(Align.CENTER);             
		Renderer.setYLabelsAlign(Align.CENTER);             
		Renderer.setXLabelsAngle(-25);                     
		Renderer.setXLabels(0);                            
		Renderer.setYAxisMin(0);                            
		yRenderer.setColor(Color.GREEN);                      
		yRenderer.setDisplayChartValues(true);            
		Series.add(0, 0);
		Renderer.addXTextLabel(0, "");
		for(int r=0; r<xy.size(); r++) {
			Log.d(TAG,"Label:::"+xy.get(r)[0][0]);
			Log.d(TAG,"Value:::"+xy.get(r)[0][1]);
			Renderer.addXTextLabel(r+1, xy.get(r)[0][1]);
			Series.add(r+1, Integer.parseInt(xy.get(r)[0][0]));
			}
		
		            Series.add(11, 0);
		            Renderer.addXTextLabel(xy.size()+1, "");
		             view = ChartFactory.getBarChartView(getActivity(), Dataset, Renderer, Type.DEFAULT);                           
		            return view;
		
		    }
	class postrequest extends AsyncTask<Integer, Integer, String> {
		
		@Override
		protected String doInBackground(Integer... countTo) {
			// TODO Auto-generated method stub
			

			try {
				String req=share.getString("uid", null);
				request_function(req);
				error=0;
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
				error=1;
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
			Log.d(TAG, "Return::::::"+ret_data);
			if(ret_data!=null)
			{
			try {
				
				JSONObject js=new JSONObject(ret_data);
				JSONArray jsonArray=js.getJSONArray("avg");
				JSONArray jsonArray1=js.getJSONArray("time");
				Log.d(TAG,"AVG array:"+jsonArray);
				Log.d(TAG,"Time array:"+jsonArray1);
				int size=jsonArray.length();
				Log.d(TAG,"Array Size:"+size);
				for (int i=0; i<jsonArray.length(); i++)
				{
				String avge=jsonArray.get(i).toString();
				String time=jsonArray1.get(i).toString();
				average.add(new String[][] {{avge,time}});
				}
				
				drawbarchart();
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

	public String request_function(String req) throws KeyStoreException, IOException,
			KeyManagementException, NoSuchAlgorithmException,
			UnrecoverableKeyException {
		Log.d(TAG, "Fuction!!!!!");
		String url = "https://nrl.cce.mcu.edu.tw/pgi/admin/batteryweek.php";
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
		params.add(new BasicNameValuePair("uid",req));
		Log.d(TAG, "DATA........."+req);
		HttpClient client = new DefaultHttpClient();
		client.getConnectionManager().getSchemeRegistry().register(sch);
		HttpPost request = new HttpPost(url);
		request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
		HttpResponse response = client.execute(request);
		HttpEntity resEntity = response.getEntity();
		ret_data=EntityUtils.toString(resEntity);
		return ret_data;
	}


	
	
}
