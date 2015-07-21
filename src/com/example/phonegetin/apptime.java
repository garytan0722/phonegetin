package com.example.phonegetin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SimpleAdapter.ViewBinder;

public class apptime extends ListFragment implements OnItemClickListener {
	private PackageManager pm;
	public static String TAG = "apptime";
	double codesize, cachesize, datasize, totalsize;
	public static ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
	private apptimeListAdapter adapter;
	private ListView thislist;
	private View thispage;
	private long firsttime, updatetime, unixtime, timedff;
	private String formattedDate, packagename;
	private long checktime = 0;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		pm = getActivity().getPackageManager();
		List<PackageInfo> installedApplications = pm
				.getInstalledPackages(PackageManager.GET_META_DATA);
		if (value.showtimeposition() == 0) {
			checktime = 0;
		}
		if (value.showtimeposition() == 1) {
			checktime = 30 * 24 * 60 * 60 * 1000L;
		}
		if (value.showtimeposition() == 2) {
			checktime = 3 * 30 * 24 * 60 * 60 * 1000L;
		}
		if (value.showtimeposition() == 3) {
			checktime = 6 * 30 * 24 * 60 * 60 * 1000L;
		}
		if (value.showtimeposition() == 4) {
			checktime = 12 * 30 * 24 * 60 * 60 * 1000L;
		}
		for (int i = 0; i < installedApplications.size(); i++) {
			ApplicationInfo app = null;
			try {
				app = pm.getApplicationInfo(
						installedApplications.get(i).packageName, 0);

			} catch (NameNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if (app.sourceDir.startsWith("/data/app/")) {
				long day;
				String daystring;
				HashMap<String, Object> item = new HashMap<String, Object>();
				firsttime = installedApplications.get(i).firstInstallTime;
				Log.d(TAG, "Firsttime~~" + firsttime);
				updatetime = installedApplications.get(i).lastUpdateTime;
				Log.d(TAG, "Updatetime##" + updatetime);
				unixtime = System.currentTimeMillis();
				long a = unixtime - updatetime;
				Log.d(TAG, "Cehck time:!!!!!" + checktime);
				Log.d(TAG, "Time diff::!!!!!" + a);
				if (unixtime - updatetime >= checktime) {
					Date date = new Date(updatetime);
					SimpleDateFormat sdf = new SimpleDateFormat(
							"yyyy MMMM d a h:mm "); // the format of your date
					formattedDate = sdf.format(date);
					item.put("updatetime", "Update Time:" + "\n"
							+ formattedDate);
					unixtime = System.currentTimeMillis();
					timedff = (unixtime - firsttime) / 1000;
					Log.d(TAG, "Time Different:" + timedff);
					long sec = timedff % 60;
					String secstring = String.valueOf(sec);
					Log.d(TAG, "Sec:" + sec);
					timedff /= 60;
					long min = timedff % 60;
					String minstring = String.valueOf(min);
					Log.d(TAG, "Min:" + min);
					timedff /= 60;
					long hour = timedff % 24;
					Log.d(TAG, "Hours:" + hour);
					String hourstring = String.valueOf(hour);
					timedff /= 24;

					Log.d(TAG, "Day:" + timedff);
					if (timedff >= 365) {
						day = timedff % 365;
						daystring = String.valueOf(day);
						timedff /= 365;
						long year = timedff;
						String yearstring = String.valueOf(year);
						item.put("apptime", "Installed Time:" + "\n" + "\n"
								+ yearstring + " " + "Years" + " " + daystring
								+ " " + "Days" + " " + hourstring + " "
								+ "Hours" + "\n" + minstring + " " + "Mins"
								+ " " + secstring + " " + "Secs");
					} else {
						day = timedff;
						daystring = String.valueOf(day);
						item.put("apptime", "Installed Time:" + "\n" + "\n"
								+ daystring + " " + "Days" + " " + hourstring
								+ " " + "Hours" + "\n" + minstring + " "
								+ "Mins" + " " + secstring + " " + "Secs");
					}
					try {
						packagename = installedApplications.get(i).packageName;
						CharSequence name = pm
								.getApplicationLabel(pm.getApplicationInfo(
										installedApplications.get(i).packageName,
										PackageManager.GET_META_DATA));
						item.put("appicon", pm
								.getApplicationIcon(installedApplications
										.get(i).packageName));
						item.put("appname", name.toString());
						item.put("packagename", packagename);

					} catch (NameNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					list.add(item);

				}
			}
		}

	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		adapter = new apptimeListAdapter(getActivity().getBaseContext(), list,
				R.layout.apptime_list_item, new String[] { "appicon",
						"appname", "updatetime", "apptime" }, new int[] {
						R.id.icon, R.id.name, R.id.update, R.id.apptime });

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

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView");
		thispage = inflater.inflate(R.layout.apptime, container, false);
		thislist = (ListView) thispage.findViewById(android.R.id.list);
		return thispage;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		Log.d(TAG, "Onclick!!!!!!");
		HashMap<String, Object> obj = list.get(position);
		String name = (String) obj.get("packagename");
		Uri packageUri = Uri.parse("package:" + name);
		Intent uninstallIntent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE,
				packageUri);
		startActivity(uninstallIntent);

	}

}
