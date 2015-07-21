package com.example.phonegetin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Checkable;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.SimpleAdapter.ViewBinder;

public class MemListAdapter extends BaseAdapter {
	private static String TAG = "MemListAdapter";
	private List<? extends Map<String, ?>> mData;
	private int[] mTo;
	private String[] mFrom;
	private ViewBinder mViewBinder;
	private Typeface text_style;

	public MemListAdapter(Context baseContext,
			List<? extends Map<String, ?>> list, int mem, String[] from,
			int[] to) {
		// TODO Auto-generated constructor stub
		text_style = Typeface.createFromAsset(baseContext.getAssets(),
				"Baar Zeitgeist Regular.ttf");
		mData = list;
		mFrom = from;
		mTo = to;

	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mData.size();
	}

	@Override
	public Object getItem(int position) {
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	public void setViewBinder(ViewBinder viewBinder) {
		mViewBinder = viewBinder;
	}

	public ViewBinder getViewBinder() {
		return mViewBinder;
	}

	public void setViewText(TextView v, String text) {
		Log.d(TAG, "Text!!!" + text);
		v.setTypeface(text_style);
		v.setText(text);

	}

	public void setProcessBar(ProgressBar v, Integer level) {
		v.setProgress(level);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub

		if (convertView == null) {
			convertView = LayoutInflater.from(parent.getContext()).inflate(
					R.layout.mem_list_item, parent, false);
		}
		bindView(position, convertView);
		// TODO replace findViewById by ViewHolder
		return convertView;
	}

	public void bindView(int position, View view) {
		final Map<String, ?> item = mData.get(position);
		if (item == null) {
			return;
		}
		final ViewBinder binder = mViewBinder;
		final String[] from = mFrom;
		final int[] to = mTo;
		final int count = to.length;

		for (int i = 0; i < count; i++) {
			final View v = view.findViewById(to[i]);
			if (v != null) {
				final Object data = item.get(from[i]);
				Log.d(TAG, "KEY" + data);
				String text = data == null ? "" : data.toString();
				if (text == null) {
					text = "";
				}
				boolean bound = false;
				if (binder != null) {
					bound = binder.setViewValue(v, data, text);
				}
				if (!bound) {
					if (v instanceof Checkable) {
						if (data instanceof Boolean) {
							((Checkable) v).setChecked((Boolean) data);
						} else if (v instanceof TextView) {
							setViewText((TextView) v, text);
						} else {
							throw new IllegalStateException(v.getClass()
									.getName()
									+ " should be bound to a Boolean, not a "
									+ (data == null ? "<unknown type>"
											: data.getClass()));
						}
					} else if (v instanceof TextView) {
						setViewText((TextView) v, text);
					} else if (v instanceof ProgressBar) {
						setProcessBar((ProgressBar) v,
								(Integer) item.get(from[4]));
					} else {
						throw new IllegalStateException(
								v.getClass().getName()
										+ " is not a "
										+ " view that can be bounds by this SimpleAdapter");
					}
				}
			}
		}

	}
}
