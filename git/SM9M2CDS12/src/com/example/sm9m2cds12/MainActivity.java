package com.example.sm9m2cds12;

import com.example.jnidriver.JNIDriver;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;

public class MainActivity extends Activity {
	
	ReceiveThread mSegThread;
	boolean mThreadRun = true;
	
	JNIDriver mDriver = new JNIDriver();
	
	byte[] data1 = {1,0,0,0,0,0,0,0};
	byte[] data2 = {0,1,0,0,0,0,0,0};
	byte[] data3 = {0,0,0,0,0,0,0,0};
	
	
	
	boolean stop_flg = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Button btn1 = (Button) findViewById(R.id.button1);
		btn1.setOnClickListener(new Button.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				mSegThread = new ReceiveThread();
				mSegThread.start();
				mThreadRun =true;
				stop_flg =false;
			}
			
		});
		
		Button btn2 = (Button) findViewById(R.id.button2);
		btn2.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				stop_flg = true;
			}
		});
	}
	private class ReceiveThread extends Thread {
		@Override
		public void run() {
			super.run();
			while(mThreadRun){
				
				Message text = Message.obtain();
				
				handler.sendMessage(text);
				try{
					Thread.sleep(1000);
				}catch(InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	public Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			TextView tv;
			FileReader in;
			int in_cda;
			try {
				in = new FileReader("/sys/devices/12d10000.adc/iio:device0/in_voltage3_raw");
				BufferedReader br = new BufferedReader(in);
				
				String data = br.readLine();
				
				tv= (TextView) findViewById(R.id.textView1);
				in_cda = Integer.parseInt(data);
				byte[] seg ={0,0,0,0,0,0};
				seg[0]=(byte) (in_cda%1000000/100000);
				seg[1]=(byte) (in_cda%100000/10000);
				seg[2]=(byte) (in_cda%10000/1000);
				seg[3]=(byte) (in_cda%1000/100);
				seg[4]=(byte) (in_cda%100/10);
				seg[5]=(byte) (in_cda%10/1);
				
				if(stop_flg == true){
					tv.setText("CDS :");
					mDriver.write(data3);
					mThreadRun  = false;
				}
				else if(in_cda < 3000) {
					tv.setText("CDS :" + data + "( Street0 - ON )");
					mDriver.write(data1);
					mDriver.ssegment(seg);
				}
				else if(in_cda >= 3000 && in_cda <3400) {
					tv.setText("CDS :" + data + "( Street1 - ON )");
					mDriver.write(data2);
					mDriver.ssegment(seg);
				}
				else if(in_cda >= 3500) {
					mDriver.setBuzzer((byte)0x01);
					tv.setText("CDS :" + data + "(스프링쿨러 작동 시작)");
					mDriver.write(data2);
					mDriver.ssegment(seg);
				}
				
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
	};
	
	@Override
	protected void onPause() {
		mDriver.close();
		super.onPause();
	}
	
	@Override
	protected void onResume() {
		if(mDriver.open("/dev/sm9s5422_led", "/dev/sm9s5422_piezo", "/dev/sm9s5422_segment")<0){
			Toast.makeText(MainActivity.this, "LED_Drive Open Failed", Toast.LENGTH_SHORT).show();
		}
		
		super.onResume();
	}

	
}
