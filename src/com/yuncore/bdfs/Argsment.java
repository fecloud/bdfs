/**
 * @(#) Argsment.java Created on 2015年9月8日
 *
 * 
 */
package com.yuncore.bdfs;

/**
 * The class <code>Argsment</code>
 * 
 * @author Feng OuYang
 * @version 1.0
 */
public final class Argsment {

	/**
	 * 云端文件扫描间隔时间
	 */
	public static final String ClOUDFILE_MONITOR_INTERVAL = "bdfs.cloudfile.monitor.interval";

	/**
	 * 本地文件扫描间隔时间
	 */
	public static final String LOCALFILE_MONITOR_INTERVAL = "bdfs.localfile.monitor.interval";

	public final static long getCloudFileMonitorInterval() {
		return Long.parseLong(System.getProperty(ClOUDFILE_MONITOR_INTERVAL, "600000"));
	}

	public final static void setCloudFileMonitorInterval(long interval) {
		System.setProperty(LOCALFILE_MONITOR_INTERVAL, "" + interval);
	}

	public final static long getLocalFileMonitorInterval() {
		return Long.parseLong(System.getProperty(LOCALFILE_MONITOR_INTERVAL, "600000"));
	}

	public final static void setLocalFileMonitorInterval(long interval) {
		System.setProperty(LOCALFILE_MONITOR_INTERVAL, "" + interval);
	}

}
