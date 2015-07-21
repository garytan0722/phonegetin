package com.example.phonegetin;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.SimpleAdapter.ViewBinder;

public class apptimeListAdapter extends BaseAdapter {
	private static String TAG = "Capacity";
	private String[] mfrom;
	private int[] mto;
	private List<? extends Map<String, ?>> mData;
	private int mResource;
	private int mDropDownResource;
	private ViewBinder mViewBinder;
	private LayoutInflater mInflater;
	private Typeface text_style;

	public apptimeListAdapter(Context baseContext,
			List<? extends Map<String, ?>> list, int capacity, String[] from,
			int[] to) {
		// TODO Auto-generated constructor stub
		text_style = Typeface.createFromAsset(baseContext.getAssets(),
				"dirtyheadline.ttf");
		mData = list;
		mfrom = from;
		mto = to;
		mResource = mDropDownResource = capacity;
		mInflater = (LayoutInflater) baseContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

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
		return 0;
	}

	public void setViewBinder(ViewBinder viewBinder) {
		// TODO Auto-generated method stub
		mViewBinder = viewBinder;
	}

	public ViewBinder getViewBinder() {
		return mViewBinder;
	}

	public void setViewImage(ImageView v, int value) {
		v.setImageResource(value);
	}

	public void setViewText(TextView v, String text) {
		Log.d(TAG, "Text!!!" + text);
		v.setTypeface(text_style);

		v.setText(text);
	}

	public void setViewImage(ImageView v, String value) {
		try {
			Log.d(TAG, "Setimage" + value);

			v.setImageResource(Integer.parseInt(value));

		} catch (NumberFormatException nfe) {
			Log.d(TAG, "Expception image");
			v.setImageURI(Uri.parse(value));
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		return createViewFromResource(position, convertView, parent, mResource);
	}

	private View createViewFromResource(int position, View convertView,
			ViewGroup parent, int resource) {
		if (convertView == null) {
			convertView = LayoutInflater.from(parent.getContext()).inflate(
					R.layout.apptime_list_item, parent, false);
		}
		bindView(position, convertView);
		// TODO replace findViewById by ViewHolder
		return convertView;
	}

	private void bindView(int position, View view) {
		final Map<String, ?> item = mData.get(position);
		if (item == null) {
			return;
		}
		final ViewBinder binder = mViewBinder;
		final String[] from = mfrom;
		final int[] to = mto;
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
					} else if (v instanceof ImageView) {
						if (data instanceof Integer) {
							setViewImage((ImageView) v, (Integer) data);
						} else {
							setViewImage((ImageView) v, text);
						}
					}

				}
			}
		}

	}

}
