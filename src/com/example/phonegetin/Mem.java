package com.example.phonegetin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class Mem extends ListFragment implements OnItemClickListener {

	private static String TAG = "Mem";
	private ActivityManager activityManager;
	private Context context;
	private Map<Integer, String> pidMap = new HashMap<Integer, String>();
	private View thispage;
	private ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
	private MemListAdapter adapter;
	private ListView thislist;
	private android.os.Debug.MemoryInfo[] memoryInfoArray;
	private double Mempercent;

	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		context = getActivity().getApplicationContext();

		activityManager = (ActivityManager) context
				.getSystemService(context.ACTIVITY_SERVICE);
		PackageManager pm = context.getPackageManager();
		List<RunningAppProcessInfo> runningAppProcesses = activityManager
				.getRunningAppProcesses();
		for (int i = 0; i < runningAppProcesses.size(); i++) {
			HashMap<String, Object> Map = new HashMap<String, Object>();
			CharSequence c = null;
			try {
				c = pm.getApplicationLabel(pm.getApplicationInfo(
						runningAppProcesses.get(i).processName,
						PackageManager.GET_META_DATA));
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (c != null) {
				Map.put("name", c);
				Log.d(TAG, "ProcessName" + c);
				int pids[] = new int[1];
				pidMap.put(runningAppProcesses.get(i).pid,
						runningAppProcesses.get(i).processName);
				Map.put("pid",
						"PID:" + String.valueOf(runningAppProcesses.get(i).pid));
				Log.d(TAG, "PID" + runningAppProcesses.get(i).pid);
				Collection<Integer> keys = pidMap.keySet();
				pids[0] = runningAppProcesses.get(i).pid;
				Log.d(TAG, "Key Array~~~~" + pids.length);
				memoryInfoArray = activityManager.getProcessMemoryInfo(pids);
				for (int a = 0; a < memoryInfoArray.length; a++) {
					double privatememb = memoryInfoArray[a]
							.getTotalPrivateDirty() / 1024;
					Mempercent = (privatememb * 100) / value.showtotlMem();
					String Memstring = String.valueOf(Mempercent);
					String Mempercentstring = Memstring.substring(0,
							Memstring.indexOf(".") + 2);
					Log.d(TAG, "TotalMem$$$" + value.showtotlMem());
					Log.d(TAG, "Privatememb###" + privatememb);
					Log.d(TAG, "Mempercent!!" + Mempercent);
					Map.put("totaldirty",
							"Priavte Memory"
									+ "    "
									+ String.valueOf(memoryInfoArray[a]
											.getTotalPrivateDirty() / 1024)
									+ "MB");
					Map.put("totalpss", String.valueOf(memoryInfoArray[a]
							.getTotalPss() / 1024));
					Map.put("totalshare", String.valueOf(memoryInfoArray[a]
							.getTotalSharedDirty() / 1024));
					Map.put("Mempercent", Mempercentstring + "%");
					Map.put("progress", (int) Mempercent);
					Map.put("time", System.currentTimeMillis());
				}

				list.add(Map);
				value.setmemapp(list);
				System.out.println(list);

			}
		}
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		adapter = new MemListAdapter(getActivity().getBaseContext(), list,
				R.layout.mem, new String[] { "pid", "name", "totaldirty",
						"Mempercent", "progress" }, new int[] { R.id.pid,
						R.id.name, R.id.totaldirty, R.id.percent,
						R.id.progressBar1 });
		thislist = (ListView) thispage.findViewById(android.R.id.list);
		thislist.setAdapter(adapter);
		thislist.setOnItemClickListener(this);
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView");
		thispage = inflater.inflate(R.layout.mem, container, false);
		thislist = (ListView) thispage.findViewById(android.R.id.list);
		return thispage;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		Log.d(TAG, "Onclick!!!!!!");
		HashMap<String, Object> obj = list.get(position);
		String name = (String) obj.get("name");
		String share = (String) obj.get("totalshare");
		String pss = (String) obj.get("totalpss");
		Toast.makeText(
				getActivity().getApplicationContext(),
				"ProcessName:" + name + " \n ShareMemory: " + share + "MB"
						+ " \n Pss: " + pss + "MB", Toast.LENGTH_SHORT).show();

	}

}
