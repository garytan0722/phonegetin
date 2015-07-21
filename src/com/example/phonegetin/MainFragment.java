package com.example.phonegetin;

import java.util.ArrayList;
import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphObjectList;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;

public class MainFragment extends Fragment {
	private static final String TAG = "MainFragment";
	private UiLifecycleHelper uiHelper;
	private Session session;
	private TextView userInfoTextView;
	private Button goback;
	private SharedPreferences data;

	@Override
	public void onResume() {
		super.onResume();
		uiHelper.onResume();
		session = Session.getActiveSession();
		if (session != null && (session.isOpened() || session.isClosed())) {
			onSessionStateChange(session, session.getState(), null);
		}

		uiHelper.onResume();

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);
		uiHelper.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onPause() {
		super.onPause();
		uiHelper.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		uiHelper.onDestroy();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {

		super.onSaveInstanceState(outState);
		uiHelper.onSaveInstanceState(outState);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "oncreatestart");
		super.onCreate(savedInstanceState);
		uiHelper = new UiLifecycleHelper(getActivity(), callback);
		uiHelper.onCreate(savedInstanceState);

	}

	private Session.StatusCallback callback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state,
				Exception exception) {
			onSessionStateChange(session, state, exception);
		}
	};

	@SuppressWarnings("deprecation")
	private void onSessionStateChange(Session session, SessionState state,
			Exception exception) {
		if (state.isOpened()) {
			value.setfbclick(1);
			if (value.showfblog() == "login") {
				Intent intent = new Intent();
				intent.setClass(getActivity(), homepager.class);
				getActivity().startActivity(intent);
				Log.d(TAG, "login");
			}
			Log.i(TAG, "Logged in...");

		}

		else if (state.isClosed()) {
			Log.i(TAG, "Logged out...");
			goback.setVisibility(View.VISIBLE);
			data = this.getActivity().getSharedPreferences("Data",
					Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = data.edit();
			editor.putInt("login", 0);
			editor.commit();
		}

	}

	public OnClickListener back = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent back_intent = new Intent();
			back_intent.setClass(getActivity(), MainActivity.class);
			startActivity(back_intent);

		}

	};

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fblogin, container, false);
		LoginButton authButton = (LoginButton) view
				.findViewById(R.id.authButton);
		goback = (Button) view.findViewById(R.id.goback);
		goback.setOnClickListener(back);
		authButton.setFragment(this);
		if (value.showfbclick() == 0) {
			goback.setVisibility(View.GONE);
		}
		authButton.setReadPermissions(Arrays.asList("email", "user_location",
				"user_birthday", "user_likes"));
		return view;
	}
}
