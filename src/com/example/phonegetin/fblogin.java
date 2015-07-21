package com.example.phonegetin;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;

public class fblogin extends FragmentActivity {
	private MainFragment mainFragment;
	private String TAG = "MainActivity";

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.d(TAG, "oncreate");
		try {
			Log.d(TAG, "Get packageinfo");
			PackageInfo info = getPackageManager().getPackageInfo(
					"com.example.phonegetin", PackageManager.GET_SIGNATURES);
			for (Signature signature : info.signatures) {
				MessageDigest md = MessageDigest.getInstance("SHA");
				md.update(signature.toByteArray());
				Log.d("KeyHash:",
						Base64.encodeToString(md.digest(), Base64.DEFAULT));
			}
		} catch (NameNotFoundException e) {

		} catch (NoSuchAlgorithmException e) {

		}
		if (savedInstanceState == null) {
			// Add the fragment on initial activity setup
			mainFragment = new MainFragment();
			getSupportFragmentManager().beginTransaction()
					.add(android.R.id.content, mainFragment).commit();
		} else {
			Log.d(TAG, "Not null");
			// Or set the fragment from restored state info
			mainFragment = (MainFragment) getSupportFragmentManager()
					.findFragmentById(android.R.id.content);
		}

	}

	protected void onStart() {
		Log.d(TAG, "onStart");

		if (value.showfbid() != null && value.showfblog() == "logout") {
			Log.d(TAG, value.showfblog());

			value.setfblog("logout");

		}

		super.onStart();
	}

	protected void onStop() {
		Log.d(TAG, "OnStop");

		if (value.showfblog() == "logout") {
			Log.d(TAG, "do logout");
			value.setfblog("login");
			// MainActivity.this.finish();

		}

		super.onStop();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
