package com.linchisin.rssCollecter;

import java.util.List;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MyAdapter extends BaseAdapter{
	
	private Context context;
	
	private List<ScanResult> scanResults;

	public MyAdapter(MainActivity context,  List<ScanResult> scanResults) {
		// TODO Auto-generated constructor stub
		super();
		this.context = context;
		this.scanResults = scanResults;
	}

	//getcount  获取数据的个数
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return scanResults.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	//getView  需要构建一个View对象来显示数据源中的数据
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		
			ScanResult scanResult = scanResults.get(position);
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		    View view = null;
		    view=inflater.inflate(R.layout.wifi_msg,null);
		   TextView textView1 = (TextView) view.findViewById(R.id.textView1);
		   TextView textView2 = (TextView) view.findViewById(R.id.textView2);
		   ImageView imageView = (ImageView) view.findViewById(R.id.imageView);



//			ViewGroup group = (ViewGroup) inflater.inflate(R.layout.wifi_msg, null);
//			TextView textView1 = (TextView) group.findViewById(R.id.textView1);
//			TextView textView2 = (TextView) group.findViewById(R.id.textView2);
//		    ImageView imageView = (ImageView) group.findViewById(R.id.imageView);
			
			textView1.setText(String.valueOf(position+1));


		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
			textView2.setText(
					"SSID: "+ scanResult.SSID+"\n"+
					"BSSID: "+scanResult.BSSID+"\n"+
					"frequency"+scanResult.frequency+"\n"+
					"capabilities: "+scanResult.capabilities+"\n"+
					"level: "+scanResult.level+"\n"+
					"timestamp"+scanResult.timestamp+"\n"+
					"describeContents: "+scanResult.describeContents()
					);
		}

		 if (Math.abs(scanResult.level) > 80) {
			imageView.setImageDrawable(imageView.getResources().getDrawable(R.drawable.ic_wifi_signal_11_tran));
		} else if (Math.abs(scanResult.level) > 70) {
			imageView.setImageDrawable(imageView.getResources().getDrawable(R.drawable.ic_wifi_signal_22_tran));
		} else if (Math.abs(scanResult.level) > 60) {
			imageView.setImageDrawable(imageView.getResources().getDrawable(R.drawable.ic_wifi_signal_22_tran));
		} else if (Math.abs(scanResult.level) > 50) {
			imageView.setImageDrawable(imageView.getResources().getDrawable(R.drawable.ic_wifi_signal_32_tran));
		} else {
			imageView.setImageDrawable(imageView.getResources().getDrawable(R.drawable.ic_wifi_signal_42_tran));
		}
		textView1.setText(scanResult.SSID);
		textView2.setText(scanResult.level + " dB");
			
		return view;
	}

	
	
}