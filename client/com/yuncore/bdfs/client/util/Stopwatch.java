package com.yuncore.bdfs.client.util;


public class Stopwatch {

	static final String TAG = "Stopwatch";

	private long start;

	private long end;

	public synchronized void start() {
		start = System.currentTimeMillis();
	}

	public synchronized void stop() {
		stop(null);
	}

	public synchronized void stop(String tag) {
		end = System.currentTimeMillis();
		if (null == tag) {
			Log.i(TAG, "time:" + (end - start));
		} else {
			Log.i(TAG, tag + " time:" + (end - start));
		}
	}
}
