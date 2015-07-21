package com.example.phonegetin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.SimpleAdapter.ViewBinder;

public class netListAdapter extends BaseAdapter {
	private int[] mTo;
	private String[] mFrom;
	private ViewBinder mViewBinder;

	private List<? extends Map<String, ?>> mData;
	private static String TAG = "ListAdapter";
	private int mResource;
	private int mDropDownResource;
	private LayoutInflater mInflater;
	private ArrayList<Map<String, ?>> mUnfilteredData;

	private Typeface text_style;

	public netListAdapter(Context context, List<? extends Map<String, ?>> data,
			int resource, String[] from, int[] to) {
		text_style = Typeface.createFromAsset(context.getAssets(),
				"Autumn Regular.ttf");
		mData = data;
		mFrom = from;
		mResource = mDropDownResource = resource;
		mTo = to;
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	}

	public void setViewBinder(ViewBinder viewBinder) {
		mViewBinder = viewBinder;
	}

	public ViewBinder getViewBinder() {
		return mViewBinder;
	}

	public void setViewImage(ImageView v, int value) {
		Log.d(TAG, "Image~~~~" + value);
		v.setImageResource(value);
	}

	public void setViewImage(ImageView v, String value) {
		try {
			Log.d(TAG, "Image@@@@@@@@@" + value);
			v.setImageResource(Integer.parseInt(value));
		} catch (NumberFormatException nfe) {
			v.setImageURI(Uri.parse(value));
		}
	}

	public void setViewText(TextView v, String text) {
		Log.d(TAG, "Text!!!" + text);

		v.setTypeface(text_style);
		v.setText(text);
	}

	public void setProcessBar(ProgressBar v, Integer level) {
		Log.d(TAG, "Progress level" + level);
		v.setProgress(level);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mData.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		return createViewFromResource(position, convertView, parent, mResource);
	}

	private View createViewFromResource(int position, View convertView,
			ViewGroup parent, int resource) {

		if (convertView == null) {

			convertView = mInflater.inflate(R.layout.net_lsit_item, null);
		}

		bindView(position, convertView);

		return convertView;
	}

	private void bindView(int position, View view) {
		final Map<String, ?> dataSet = mData.get(position);
		if (dataSet == null) {
			return;
		}

		final ViewBinder binder = mViewBinder;
		final String[] from = mFrom;
		final int[] to = mTo;
		final int count = to.length;

		for (int i = 0; i < count; i++) {
			final View v = view.findViewById(to[i]);
			if (v != null) {
				final Object data = dataSet.get(from[i]);
				Log.d(TAG, "Key!!!!!!!!" + data);
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
					} else if (v instanceof ImageView) {
						if (data instanceof Integer) {
							setViewImage((ImageView) v, (Integer) data);
						} else {
							setViewImage((ImageView) v, text);
						}
					} else if (v instanceof ProgressBar) {
						setProcessBar((ProgressBar) v,
								(Integer) dataSet.get(from[5]));
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
