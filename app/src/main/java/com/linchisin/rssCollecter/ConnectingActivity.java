package com.linchisin.rssCollecter;

import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class ConnectingActivity extends Activity {

	private WifiManager wifiManager;
	private TextView textView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_connecting);
		
		Intent intent = this.getIntent();
		String wifiInfo = intent.getStringExtra("wifiInfo");
		textView = (TextView) findViewById(R.id.connecttedInfo);
		textView.setText(wifiInfo);
		
		wifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
		
		
	}
	
	public void update(View view) {
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		textView.setText(wifiInfo.toString());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.connecting, menu);
		return true;
	}

}
