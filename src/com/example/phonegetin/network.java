package com.example.phonegetin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.net.TrafficStats;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class network extends ListFragment implements OnItemClickListener {
	private static String TAG = "network";
	private View thispage;
	private PackageManager pm;
	private long totalByte;
	private ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
	private netListAdapter adapter;
	private ListView thislist;
	public static int progeress_percent;

	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate");
		super.onCreate(savedInstanceState);
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		pm = getActivity().getPackageManager();
		List<ApplicationInfo> installProcesses = pm
				.getInstalledApplications(PackageManager.GET_META_DATA);
		getBytes();
		for (int i = 0; i < installProcesses.size(); i++) { // add app to list
			HashMap<String, Object> item = new HashMap<String, Object>();
			long send = 0, recived = 0;
			send = TrafficStats.getUidRxBytes(installProcesses.get(i).uid);
			recived = TrafficStats.getUidTxBytes(installProcesses.get(i).uid);
			if (TrafficStats.getUidRxBytes(installProcesses.get(i).uid) > 0
					|| TrafficStats.getUidTxBytes(installProcesses.get(i).uid) > 0) {
				int check = 0;
				for (int a = 0; a < list.size(); a++) { // check same UID app
					HashMap<String, Object> obj = list.get(a);
					int uid = (Integer) obj.get("appuid");
					if (uid == installProcesses.get(i).uid) {
						check = 1;
					}
				}

				double total = ((send + recived) / 1024);
				if (value.shownetmin() < total) {
					try {
						CharSequence c = pm.getApplicationLabel(pm
								.getApplicationInfo(
										installProcesses.get(i).processName,
										PackageManager.GET_META_DATA));
						item.put(
								"appicon",
								pm.getApplicationIcon(installProcesses.get(i).processName));
						item.put("appname", c.toString());
						item.put("appuid", installProcesses.get(i).uid);
						item.put("time", System.currentTimeMillis());
						long percent = (long) (total * 100) / totalByte;
						String percentstring = String.valueOf(percent);
						progeress_percent = Integer.parseInt(percentstring);
						item.put("intpercent", progeress_percent);
						item.put("percent", percent + " %");
						if (total / 1024 <= 1) {
							long shownum = (long) total;
							item.put("apptotalbyte", shownum + " KB");
						} else {
							total /= 1024;
							long shownum = (long) total;
							item.put("apptotalbyte", shownum + " MB");
						}
						item.put("compare", send + recived);
						item.put("processbar",
								(int) ((total * 100) / totalByte));
						if (check != 1) {
							list.add(item);
						}
					}

					catch (NameNotFoundException e) {
						if (check != 1
								&& installProcesses.get(i).processName
										.equals("android.process.media")) {
							item.put("appicon", R.drawable.ic_launcher);
							item.put("appname", "DownloadManger");
							item.put("appuid", installProcesses.get(i).uid);
							item.put("time", System.currentTimeMillis());
							total = ((send + recived) / 1024);
							long percent = (long) (total * 100) / totalByte;
							String percentstring = String.valueOf(percent);
							progeress_percent = Integer.parseInt(percentstring);
							item.put("percent", percent + " %");
							item.put("intpercent", progeress_percent);
							if (total / 1024 <= 1) {
								long shownum = (long) total;
								item.put("apptotalbyte", shownum + " KB");
							} else {
								total /= 1024;
								long shownum = (long) total;
								item.put("apptotalbyte", shownum + " MB");
							}
							item.put("compare", send + recived);
							item.put("processbar",
									(int) ((total * 100) / totalByte));
							list.add(item);
							value.setnetapp(list);
							System.out.println(list);
						}
					}
				}

			}
		}
		sort(list);
		adapter = new netListAdapter(getActivity().getBaseContext(), list,
				R.layout.network,
				new String[] { "appicon", "appname", "apptotalbyte", "percent",
						"processbar", "intpercent" }, new int[] {
						R.id.imageicon, R.id.appname, R.id.totalM,
						R.id.percent, R.id.progressBar1 });
		thislist.setOnItemClickListener(this);
		adapter.setViewBinder(new MyViewBinder());
		thislist = getListView();
		thislist.setAdapter(adapter);
	}

	public void getBytes()

	{
		List<ApplicationInfo> installProcesses = pm
				.getInstalledApplications(PackageManager.GET_META_DATA);
		ArrayList<Integer> obj = new ArrayList<Integer>();
		for (int i = 0; i < installProcesses.size(); i++) {
			long send = 0, received = 0;
			received = TrafficStats.getUidRxBytes(installProcesses.get(i).uid);
			send = TrafficStats.getUidTxBytes(installProcesses.get(i).uid);
			if (TrafficStats.getUidRxBytes(installProcesses.get(i).uid) > 0
					|| TrafficStats.getUidTxBytes(installProcesses.get(i).uid) > 0) {
				int check = 0;
				for (int a = 0; a < obj.size(); a++) {
					int uid = obj.get(a);
					if (uid == installProcesses.get(i).uid) {
						check = 1;
					}
				}
				if (check != 1) {
					obj.add(installProcesses.get(i).uid);
					totalByte += ((send + received) / 1000);
				}
			}
		}
	}

	public static ArrayList<HashMap<String, Object>> sort(
			ArrayList<HashMap<String, Object>> list) {
		for (int i = 0; i < list.size() - 1; i++) {
			for (int j = 0; j < list.size() - i - 1; j++) {
				HashMap<String, Object> objb = (HashMap<String, Object>) list
						.get(j);
				HashMap<String, Object> obja = (HashMap<String, Object>) list
						.get(j + 1);
				long a = (Long) obja.get("compare");
				long b = (Long) objb.get("compare");
				if (b < a) {
					HashMap<String, Object> temp = list.get(j);
					list.set(j, list.get(j + 1));
					list.set(j + 1, temp);
				}
			}
		}
		return list;
	}

	public class MyViewBinder implements ViewBinder {// show Image View with
		// Drawable
		@Override
		public boolean setViewValue(View view, Object data,
				String textRepresentation) {
			if (view instanceof ImageView && data instanceof Drawable) {
				ImageView iv = (ImageView) view;
				iv.setImageDrawable((Drawable) data);
				return true;
			} else
				return false;
		}
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView");
		thispage = inflater.inflate(R.layout.network, container, false);
		thislist = (ListView) thispage.findViewById(android.R.id.list);
		return thispage;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub

		Log.d(TAG, "Onclick!!!!!!");
		long send = 0;
		long recived = 0;
		HashMap<String, Object> obj = list.get(position);
		String name = (String) obj.get("appname");
		int uid = (Integer) obj.get("appuid");
		recived = TrafficStats.getUidRxBytes(uid);

		send = TrafficStats.getUidTxBytes(uid);
		Toast.makeText(
				getActivity().getApplicationContext(),
				"AppName:" + name + " \n send: " + send / 1024 + "kB"
						+ " \n recived: " + recived / 1024 + "kB",
				Toast.LENGTH_SHORT).show();
	}

}
