/*2015.5.8 修改
 * 点击下一位置的时候，初始化dataRSSI
 * 
 * 
 * 
 */

/*2015.5.26 修改
		* 去掉wifi，只采集其他各种传感器数据
		*/
/*2016.5..6 修改
 * 删除其他传感器数据，恢复wifi
 */

package com.linchisin.rssCollecter;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends Activity {

	private Button startButton = null;
	private Button stopButton = null;
	private Button checkButton = null;
	private Button scanButton = null;
	private Button connecttedButton = null;

	private EditText editText=null;

	private ToggleButton toggleButton;
	private TextView textView = null;
	private ListView listView = null;
	private WifiManager wifiManager = null;
	private List<ScanResult> scanResults = null;
	private WifiInfo wifiInfo;
	private boolean startCollect=false;
	private Thread timeThread;
	private boolean cancelThread=false;
	private long timeSinceStart=0;
	private int dataNum=0;
	private long timeOfStartScan=0;
	private boolean regBoolean=false;
	private float timeInterval=10;

	private File sdcard = Environment.getExternalStorageDirectory();
	private String wifiState=null;
	private AlertDialog.Builder builder;








	private int[][] dataRssi = new int[1000][10000];

	private String tempString = null;

	private String[] dataBssid = new String[] {	//长度600，最多存储600个热点的RSS
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""
	};
	private String[] dataWifiNameStrings = new String[] {	//长度600，最多存储600个热点的RSS
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
			"", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""
	};

	public String getWifiStateString(){

		if (wifiManager==null) {
			wifiManager = (WifiManager) MainActivity.this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
		}
		else {
			switch (wifiManager.getWifiState()) {
				case 0:
					wifiState = "正在关闭";
					break;
				case 1:
					wifiState = "已经关闭";
					break;
				case 2:
					wifiState = "正在打开";
					break;
				case 3:
					wifiState = "已经打开";
			}
		}
		return wifiState;
	}



//	private ArrayList<String> wifiNames = null;
//	private ArrayList<WifiConfiguration> wifiDevices = null;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

		startButton = (Button) findViewById(R.id.startButton);
		stopButton = (Button) findViewById(R.id.stopButton);
		checkButton = (Button) findViewById(R.id.checkButton);
		scanButton = (Button) findViewById(R.id.scanButton);
		editText= (EditText) findViewById(R.id.editText);
		toggleButton =(ToggleButton) findViewById(R.id.toggleButton1);
		connecttedButton = (Button) findViewById(R.id.connecttedButton);
		textView = (TextView) findViewById(R.id.textViewState);
		listView = (ListView) findViewById(R.id.listView1);


		if (wifiManager==null) {
			wifiManager = (WifiManager) MainActivity.this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
		}

		if (wifiManager.getWifiState()!=WifiManager.WIFI_STATE_ENABLED) {
			Toast.makeText(getApplicationContext(), "Wi-Fi未打开，请稍后按下扫描按钮...", Toast.LENGTH_SHORT).show();


			if (!wifiManager.isWifiEnabled()) {
				wifiManager.setWifiEnabled(true);
			}

		}

		wifiManager.startScan();
		scanResults=wifiManager.getScanResults();
		if (scanResults!=null) {
			MyAdapter adapter = new MyAdapter(MainActivity.this, scanResults);
			listView.setAdapter(adapter);
		}


		startButton.setOnClickListener(startWiFiListener);
		stopButton.setOnClickListener(stopWiFiListener);
		checkButton.setOnClickListener(checkWiFiListener);
		scanButton.setOnClickListener(scanWiFilClickListener);
		connecttedButton.setOnClickListener(connecttedClickListener);




		toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				startCollect=isChecked;
				//2015.5.26 删掉wifi相关的代码
				//2016.5.6 恢复wifi相关的代码

//				if (wifiManager.getWifiState()!=WifiManager.WIFI_STATE_ENABLED) {
//					Toast.makeText(getApplicationContext(), "正在开启wifi，请稍后...", Toast.LENGTH_SHORT).show();
//					if (wifiManager==null) {
//						wifiManager = (WifiManager) MainActivity.this.getSystemService(Context.WIFI_SERVICE);
//					}
//					if (!wifiManager.isWifiEnabled()) {
//						wifiManager.setWifiEnabled(true);
//					}
//
//					if (wifiManager.getWifiState()!=WifiManager.WIFI_STATE_ENABLED) {
//						textView.setText(getWifiStateString());
//					}
//					textView.setText(getWifiStateString());
//				}

//				if (wifiManager==null) {
//					wifiManager = (WifiManager) MainActivity.this.getSystemService(Context.WIFI_SERVICE);
//				}






				if (startCollect) {

					showSimpleDialog();

//					wifiManager.startScan();
//					Toast.makeText(getApplicationContext(),"正在采集数据...",Toast.LENGTH_SHORT).show();
//					timeOfStartScan=timeSinceStart;
//					for (int i = 0; i < dataRssi.length; i++) {
//						for (int j = 0; j < dataRssi[i].length; j++) {
//							dataRssi[i][j] = 0;
//						}
//					}
//					dataNum=0;
//
//					registerReceiver(cycleWifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));


				}else {
					if (regBoolean == true) {

						unregisterReceiver(cycleWifiReceiver);
						regBoolean=false;
						//然后存储数据到文件
						//log只列出前20个热点信息
//					Log.i("BSSID列表", dataBssid[0]+" "+dataBssid[1]+" "+dataBssid[2]+" "+dataBssid[3]+" "+dataBssid[4]+" "+dataBssid[5]+" "+dataBssid[6]+" "+dataBssid[7]+" "+dataBssid[8]+" "+dataBssid[9]+" "+dataBssid[10]+" "+dataBssid[11]+" "+dataBssid[12]+" "+dataBssid[13]+" "+dataBssid[14]+" "+dataBssid[15]+" "+dataBssid[16]+" "+dataBssid[17]+" "+dataBssid[18]+" "+dataBssid[19]);

						File myfile = new File(sdcard, "RSS_Data_at_" + editText.getText().toString() + ".txt");
						if (!sdcard.exists()) {
							Toast.makeText(getApplicationContext(), "当前系统不具备SDcard", Toast.LENGTH_SHORT).show();
							return;
						}

					/*
					* 此处存在内存卡
					* */
						try {
							myfile.createNewFile();
							FileOutputStream fos = new FileOutputStream(myfile);
							OutputStreamWriter osw = new OutputStreamWriter(fos);
							for (int j = 0; j < dataBssid.length; j++) {
								if (dataBssid[j].equals("")) break;
								tempString = j + 1 + "  MAC地址: ";
								osw.write(editText.getText().toString());
								osw.write("\n");
								osw.write(tempString);
								osw.write(dataBssid[j]);
								osw.write("  SSID:");
								osw.write(dataWifiNameStrings[j]);
								osw.write("\n");
								for (int i = 0; i < dataNum; i++) {
									String temp = String.valueOf(dataRssi[j][i]);
									osw.write(temp);


									osw.write("\n");
								}
								osw.write("\n");

							}
							osw.flush();
							fos.flush();
							osw.close();
							fos.close();
							Toast.makeText(getApplicationContext(), "文件已经写入完成", Toast.LENGTH_SHORT).show();
						} catch (IOException e) {
							e.printStackTrace();
						}

					/*
					* 此处存在程序内存中
					* */

						try {
							FileOutputStream fos = openFileOutput("RSSData_at_" + editText.getText().toString() + "_备份" + ".txt", Context.MODE_WORLD_READABLE + Context.MODE_WORLD_WRITEABLE);
							OutputStreamWriter osw = new OutputStreamWriter(fos);
							for (int j = 0; j < dataBssid.length; j++) {
								if (dataBssid[j].equals("")) break;
								tempString = j + 1 + "  MAC地址: ";
								osw.write(editText.getText().toString());
								osw.write("\n");
								osw.write(tempString);
								osw.write(dataBssid[j]);
								osw.write("  SSID:");
								osw.write(dataWifiNameStrings[j]);
								osw.write("\n");
								for (int i = 0; i < dataNum; i++) {
									String s = String.valueOf(dataRssi[j][i]);
									osw.write(s);


									osw.write("\n");
								}
								osw.write("\n");

							}
							osw.flush();
							fos.flush();
							osw.close();
							fos.close();


						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}

					}
				}






			}
		});



		listView.setOnItemClickListener(wifiClickListener);

		timeThread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					while (!cancelThread) {
						Thread.sleep(10);//睡10ms
						timeSinceStart++;
//						Log.i("timeofTheday", String.valueOf("timeOfTheDay"));
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		timeThread.start();
	}

	private OnClickListener startWiFiListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (wifiManager==null) {
				wifiManager = (WifiManager) MainActivity.this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
			}
			if (!wifiManager.isWifiEnabled()) {
				wifiManager.setWifiEnabled(true);
			}
			textView.setText(getWifiStateString());
		}
	};
	private OnClickListener stopWiFiListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (wifiManager==null) {
				wifiManager = (WifiManager) MainActivity.this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
			}
			if (wifiManager.isWifiEnabled()) {
				wifiManager.setWifiEnabled(false);
			}
			textView.setText(getWifiStateString());
		}
	};
	private OnClickListener checkWiFiListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (wifiManager==null) {
				wifiManager = (WifiManager) MainActivity.this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
			}
			textView.setText(getWifiStateString());

		}
	};

	private OnClickListener scanWiFilClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub

			//2015.2.26----注释掉了下边这段，不使用wifi
			//2015.5.6----恢复下边这段，使用wifi

			if (startCollect) {
				Toast.makeText(getApplicationContext(), "正在循环扫描！", Toast.LENGTH_SHORT).show();
			}else {

				if (wifiManager.getWifiState()!=WifiManager.WIFI_STATE_ENABLED) {
					Toast.makeText(getApplicationContext(), "WiFi未打开，请先打开WiFi，再进行扫描！", Toast.LENGTH_SHORT).show();

				} else {
					if (wifiManager==null) {
						wifiManager = (WifiManager) MainActivity.this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
					}
					wifiManager.startScan();
					scanResults=wifiManager.getScanResults();
					Toast.makeText(getApplicationContext(), "共扫描到 "+scanResults.size()+"个wifi热点", Toast.LENGTH_SHORT).show();
					if (scanResults!=null) {
						MyAdapter adapter = new MyAdapter(MainActivity.this, scanResults);
						listView.setAdapter(adapter);
					}
//					registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
				}
			}
		}
	};

	private OnClickListener connecttedClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			wifiInfo = wifiManager.getConnectionInfo();
			Intent intent = new Intent();
			intent.setClass(MainActivity.this, ConnectingActivity.class);
			intent.putExtra("wifiInfo", wifiInfo.toString());
			MainActivity.this.startActivity(intent);
		}
	};


	//两次返回退出
	private long exitTime = 0;
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
			if((System.currentTimeMillis()-exitTime) > 2000){
				Toast.makeText(getApplicationContext(), "再次点击“返回”退出", Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();
			} else {
				finish();
				System.exit(0);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}



//	private final BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
//
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			// TODO Auto-generated method stub
////			List<WifiConfiguration> wcList = wifiManager.getConfiguredNetworks();
////
////	        WifiConfiguration wc = null;
////	        wifiNames.clear();
////	        wifiDevices.clear();
////	        Toast.makeText(MainActivity.this, "len= "+ wcList.size(), Toast.LENGTH_SHORT).show();
////	        for (int i = 0, len = wcList.size(); i < len; i++) {
////
////	            wc = wcList.get(i);
////	            if (!wifiNames.contains(wc.SSID)) {
////	                wifiNames.add(wc.SSID);
////	                wifiDevices.add(wc);
////	            }
////	        }
//			scanResults=wifiManager.getScanResults();
//			Toast.makeText(getApplicationContext(), "共扫描到 "+scanResults.size()+"个wifi热点", Toast.LENGTH_SHORT).show();
//			if (scanResults!=null) {
//				MyAdapter adapter = new MyAdapter(MainActivity.this, scanResults);
//				listView.setAdapter(adapter);
//			}
//
//		}
//	};

	private OnItemClickListener wifiClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
								long arg3) {
			// TODO Auto-generated method stub
			ScanResult scanResult = scanResults.get(arg2);

			AlertDialog.Builder wifiBuilder = new AlertDialog.Builder(MainActivity.this);
			wifiBuilder.setTitle(scanResult.SSID);
			ViewGroup connectWiFiGroup =
					(ViewGroup) MainActivity.this.getLayoutInflater().inflate(R.layout.connect_wifi,null);
			TextView msgTextView = (TextView) connectWiFiGroup.getChildAt(0);
			EditText pswEditText = (EditText) connectWiFiGroup.getChildAt(2);

			msgTextView.setText(
					"  SSID:\n  "+ scanResult.SSID+"\n\n"+
							"  BSSID:\n  "+scanResult.BSSID+"\n\n"+
							"  frequency:\n  "+scanResult.frequency+" MHz\n\n"+
							"  capabilities:\n  "+scanResult.capabilities+"\n\n"+
							"  level:\n  "+scanResult.level+" dBm\n\n"+
							"  describeContents:\n  "+scanResult.describeContents()+"\n\n"
			);

			wifiBuilder.setView(connectWiFiGroup);

			wifiBuilder.setPositiveButton("不知道密码哦", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					Toast toast = Toast.makeText(getApplicationContext(), "呵呵！", Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
				}
			});
			wifiBuilder.setNegativeButton("连接", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					Toast toast = Toast.makeText(getApplicationContext(), "请自行前往系统页面连接，谢谢！", Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
				}
			});

			wifiBuilder.setNeutralButton("取消", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					Toast.makeText(getApplicationContext(), "-.-", Toast.LENGTH_SHORT).show();
				}
			});

			AlertDialog alertDialog = wifiBuilder.create();
			alertDialog.show();
		}
	};

	private final BroadcastReceiver cycleWifiReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub

			scanResults = wifiManager.getScanResults();

			if (scanResults != null) {
				MyAdapter adapter = new MyAdapter(MainActivity.this, scanResults);
				listView.setAdapter(adapter);
			}
			//更新热点列表，只增不减，顺序不变，同时将RSSI记录
			for (int i = 0; i < scanResults.size(); i++) {
				for (int j = 0; j < dataBssid.length; j++) {
					if (dataBssid[j].equals(scanResults.get(i).BSSID)) {
						dataRssi[j][dataNum] = scanResults.get(i).level;

						break;
					}
					if (dataBssid[j].equals("")) {
						dataBssid[j] = scanResults.get(i).BSSID;
						dataWifiNameStrings[j] = scanResults.get(i).SSID;
						dataRssi[j][dataNum] = scanResults.get(i).level;
						break;
					}
				}

			}
//			Log.i("BSSID列表", dataBssid[0]+" "+dataBssid[1]+" "+dataBssid[2]+" "+dataBssid[3]+" "+dataBssid[4]+" "+dataBssid[5] + " " + dataBssid[6] + " " + dataBssid[7] + " " + dataBssid[8] + " " + dataBssid[9] + " ");
//			Log.i("RSSI列表", dataRssi[0][dataNum] + " " + dataRssi[1][dataNum] + " " + dataRssi[2][dataNum] + " " + dataRssi[3][dataNum] + " " + dataRssi[4][dataNum] + " " + dataRssi[5][dataNum] + " " + dataRssi[6][dataNum] + " " + dataRssi[7][dataNum] + " " + dataRssi[8][dataNum] + " " + dataRssi[9][dataNum] + " " + dataRssi[10][dataNum] + " " + dataRssi[11][dataNum] + " " + dataRssi[12][dataNum] + " " + dataRssi[13][dataNum] + " " + dataRssi[14][dataNum] + " " + dataRssi[15][dataNum] + " " + dataRssi[16][dataNum]+" "+dataRssi[17][dataNum]+" "+dataRssi[18][dataNum]+" "+dataRssi[19][dataNum]);

			dataNum++;



			while (timeSinceStart - timeOfStartScan < timeInterval) {
				//等待
			}
			if (timeSinceStart - timeOfStartScan >= timeInterval) {//收到后开始下一次扫描，控制一下时间，每秒一次
				Log.i("ceshi", "onReceive: "+timeInterval);
				wifiManager.startScan();
				timeOfStartScan = timeSinceStart;
				toggleButton.setText("关闭RSS数据采集" + "(" + String.valueOf(dataNum) + ")");
				if(dataNum==100){
					toggleButton.setChecked(false);
				}
			}


		}
	};


	//显示基本Dialog
	private void showSimpleDialog() {
		builder=new AlertDialog.Builder(this);

		builder.setTitle("警告");
		builder.setMessage("请先确认坐标");

		//监听下方button点击事件
		builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int i) {
//				Toast.makeText(getApplicationContext(),R.string.toast_postive,Toast.LENGTH_SHORT).show();
				wifiManager.startScan();
				Toast.makeText(getApplicationContext(),"正在采集数据...",Toast.LENGTH_SHORT).show();
				timeOfStartScan=timeSinceStart;
				for (int m = 0; m < dataRssi.length; m++) {
					for (int j = 0; j < dataRssi[m].length; j++) {
						dataRssi[m][j] = 0;
					}
				}
				dataNum=0;

				registerReceiver(cycleWifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
				regBoolean=true;
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int i) {
//				Toast.makeText(getApplicationContext(), R.string.toast_negative, Toast.LENGTH_SHORT).show();
				toggleButton.setChecked(false);
			}
		});

		//设置对话框是可取消的
		builder.setCancelable(true);
		AlertDialog dialog=builder.create();
		dialog.show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	//	class AsyncMain extends AsyncTask<Void, Void, Void>{
//
//		protected Void doInBackground(Void... params) {
//			// TODO Auto-generated method stub
//			while (startCollect) {
//				publishProgress();
//			}
//			return null;
//
//		}
//
//
//		@Override
//		protected void onPostExecute(Void result) {
//			// TODO Auto-generated method stub
//			super.onPostExecute(result);
//		}
//
//		@Override
//		protected void onPreExecute() {
//			// TODO Auto-generated method stub
//			super.onPreExecute();
//		}
//
//
//		@Override
//		protected void onProgressUpdate(Void... values) {
//			// TODO Auto-generated method stub
//			
//			
//			super.onProgressUpdate(values);
//		}
//
//	}


	/**
	 * 判断字符串是否是数字
	 */
	public static boolean isNumber(String value) {
		Log.i("ceshi", "isNumber: ");
		return isInteger(value) || isFloat(value);
	}
	/**
	 * 判断字符串是否是整数
	 */
	public static boolean isInteger(String value) {
		try {
			Log.i("ceshi", "isInteger: ");
			Integer.parseInt(value);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	/**
	 * 判断字符串是否是浮点数
	 */
	public static boolean isFloat(String value) {
		try {
			Log.i("ceshi", "isFloat: ");
			Float.parseFloat(value);
			if (value.contains("."))
				return true;
			return false;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	protected void onStop() {
		// TODO Auto-generated method stub
		cancelThread=true;
		super.onStop();
	}


}
	

