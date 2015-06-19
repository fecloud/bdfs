package com.yuncore.bdfs.tools;

import org.apache.log4j.Logger;

public class Stopwatch {

	Logger logger = Logger.getLogger(Stopwatch.class.getSimpleName());

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
			logger.info("time:" + (end - start));
		} else {
			logger.info(tag + " time:" + (end - start));
		}
	}
}
