package com.example.phonegetin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PackageStats;
import android.content.pm.IPackageStatsObserver;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SimpleAdapter.ViewBinder;

public class capacity extends ListFragment implements OnItemClickListener {
	private PackageManager pm;
	public static String TAG = "capacity";
	double codesize, cachesize, datasize, totalsize;
	public static ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
	private capacityListAdapter adapter;
	private ListView thislist;
	private View thispage;

	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		pm = getActivity().getPackageManager();
		List<ApplicationInfo> installedApplications = pm
				.getInstalledApplications(PackageManager.GET_META_DATA);
		for (int i = 0; i < installedApplications.size(); i++) {
			try {
				if (installedApplications.get(i).sourceDir
						.startsWith("/data/app/")) {
					Method getPackageSizeInfo = pm.getClass().getMethod(
							"getPackageSizeInfo", String.class,
							IPackageStatsObserver.class);
					getPackageSizeInfo.invoke(pm,
							installedApplications.get(i).packageName,
							new PkgSizeObserver());
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		adapter = new capacityListAdapter(getActivity().getBaseContext(), list,
				R.layout.capacity, new String[] { "appicon", "appname",
						"totalsize" }, new int[] { R.id.icon, R.id.name,
						R.id.codesize });
		adapter.notifyDataSetChanged();
		adapter.setViewBinder(new ViewBinder() {
			public boolean setViewValue(View view, Object data,
					String textRepresentation) {
				if (view instanceof ImageView && data instanceof Drawable) {
					ImageView iv = (ImageView) view;
					iv.setImageDrawable((Drawable) data);
					return true;
				} else
					return false;
			}
		});
		thislist = (ListView) thispage.findViewById(android.R.id.list);
		thislist.setAdapter(adapter);
		thislist.setOnItemClickListener(this);
	}

	class PkgSizeObserver extends IPackageStatsObserver.Stub {
		HashMap<String, Object> item = new HashMap<String, Object>();
		private String name;

		@Override
		public void onGetStatsCompleted(PackageStats pStats, boolean succeeded)
				throws RemoteException {
			// TODO Auto-generated method stub

			codesize = pStats.codeSize / 1024;
			cachesize = pStats.cacheSize / 1024;
			datasize = pStats.dataSize / 1024;
			name = pStats.packageName;
			Log.d(TAG, "Name!!!!!!!" + name);
			Log.d(TAG, "Codesize:.." + codesize);
			Log.d(TAG, "cachesize......." + cachesize);
			Log.d(TAG, "Datasize....." + datasize);
			Log.d(TAG, "1111111111111111");
			totalsize = codesize + cachesize + datasize;
			Log.d(TAG, "TotalSize!!!" + totalsize);
			if (totalsize >= value.showsizemin()) {

				if (totalsize >= 1000) {
					String b = String.valueOf(totalsize / 1024);
					String mb = b.substring(0, b.indexOf(".") + 2);
					item.put("totalsize", "App Size " + mb + "MB");
				} else if (totalsize < 1000) {
					item.put("totalsize", "App Size " + totalsize + "KB");
				}
				item.put("cachesize", cachesize);
				item.put("datasize", datasize);
				item.put("codesize", codesize);
				item.put("time", System.currentTimeMillis());
				Log.d(TAG, "Name" + name);
				Log.d(TAG, "Put map");
				Log.d(TAG, "22222222222222222");
				try {
					Log.d(TAG, "333333333333333333");
					item.put("appname", pm.getApplicationLabel(pm
							.getApplicationInfo(name,
									PackageManager.GET_META_DATA)));
					item.put("appicon",
							pm.getApplicationIcon(pStats.packageName));
					Log.d(TAG, "Map" + item);

				} catch (NameNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					item.put("appicon", R.drawable.ic_launcher);
					item.put("appname", "download manger");
					Log.d(TAG, "Name" + name);
					item.put("codesize", codesize);
					item.put("totalsize", "App Size: " + totalsize + "KB");
					item.put("cachesize", cachesize);
					item.put("datasize", datasize);
					item.put("time", System.currentTimeMillis());
					Log.d(TAG, "Map" + item);
					capacity.list.add(item);
					Log.d(TAG, "444444444444444444444");
				}
				capacity.list.add(item);
				value.setsizeapp(list);
			}

		}
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView");
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		thispage = inflater.inflate(R.layout.capacity, container, false);
		thislist = (ListView) thispage.findViewById(android.R.id.list);
		return thispage;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		Log.d(TAG, "Onclick!!!!!!");
		HashMap<String, Object> obj = list.get(position);
		String name = (String) obj.get("appname");
		String code = String.valueOf(obj.get("codesize"));
		String cache = String.valueOf(obj.get("cachesize"));
		String data = String.valueOf(obj.get("datasize"));
		Toast.makeText(
				getActivity().getApplicationContext(),
				"Name:" + name + " \n Apk Size: " + code + "KB"
						+ " \n Cache Size: " + cache + "KB" + "\n Data Size:"
						+ data + "KB", Toast.LENGTH_SHORT).show();

	}

}
