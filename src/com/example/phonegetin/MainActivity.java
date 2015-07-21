package com.example.phonegetin;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.facebook.widget.LoginButton;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.IntentSender.SendIntentException;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {
	private static String TAG = "MainActivty";
	private TextView title;
	private SharedPreferences data;
	private Button icon_butn;
	private AlertDialog mutiItemDialog;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mutiItemDialog = getMutiItemDialog(new String[] { "FB login",
				"Google login" });
		icon_butn = (Button) findViewById(R.id.icon_butn);
		title = (TextView) findViewById(R.id.title);
		icon_butn.setOnClickListener(this);
	}

	protected void onStart() {
		Log.d(TAG, "onStart");

		super.onStart();
	}

	protected void onStop() {
		Log.d(TAG, "OnStop");

		super.onStop();

	}

	public AlertDialog getMutiItemDialog(final String[] items) {
		Builder builder = new Builder(this);
		builder.setItems(items, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int item) {
				
				switch (item) {
				case 0:
					Log.d(TAG, "FB intent!!!");
					value.setfbclick(1);
					value.setgoogleclick(0);
					Intent fbintent = new Intent();
					fbintent.setClass(MainActivity.this, fblogin.class);
					startActivity(fbintent);
					break;
				case 1:
					Log.d(TAG, "google intent~~~");
					value.setgoogleclick(1);
					value.setfbclick(0);
					Intent googleintent = new Intent();
					googleintent.setClass(MainActivity.this, googlelogin.class);
					startActivity(googleintent);
					break;

				}
			}
		});
		return builder.create();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.icon_butn:
			mutiItemDialog.show();
			break;
		}

	}

}
