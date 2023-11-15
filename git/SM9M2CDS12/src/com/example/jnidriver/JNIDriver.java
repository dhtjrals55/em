package com.example.jnidriver;

public class JNIDriver {
	
	private boolean mConnectFlag;
	
	static {
		System.loadLibrary("JNIDriver");
	}
	
	private native static int openDriver(String path, String path2, String path3);
	private native static void closeDriver();
	private native static void writeDriver(byte[] data, int length);
	private native static void ssegment(byte[] data, int length);
	private native void setBuzzer(char data);
	
	public JNIDriver() {
		mConnectFlag = false;
	}
	
	public int open(String driver, String driver2, String driver3) {
		if(mConnectFlag) return -1;
		
		if(openDriver(driver, driver2, driver3)>0) {
			mConnectFlag = true;
			return 1;
		}else {
			return -1;
		}
	}
	
	
	public void close() {
		if(!mConnectFlag) return;
		mConnectFlag = false;
		closeDriver();
	}
	
	
	protected void finalize() throws Throwable {
		close();
		super.finalize();
	}
	
	public void write(byte[] data){
		if(!mConnectFlag) return;
		
		writeDriver(data, data.length);
	}
	
	public void ssegment(byte[] data){
		if(!mConnectFlag) return;
		
		ssegment(data, data.length);
	}
	public void setBuzzer(int val){
		if(!mConnectFlag)return;
		
		setBuzzer((char)val);
	}
}
