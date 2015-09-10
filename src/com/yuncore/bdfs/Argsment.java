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

	/**
	 * 是否允许下载
	 */
	public static final String ALLOW_DOWNLOAD = "bdfs.allow.download";

	/**
	 * 是否允许上传
	 */
	public static final String ALLOW_UPLOAD = "bdfs.allow.upload";

	public static String getProperty(String key) {
		return System.getProperty(key);
	}
	
	public static String getProperty(String key, String def) {
		return System.getProperty(key, def);
	}
	
	public static void setProperty(String key, String value) {
		System.setProperty(key, value);
	}
	
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

	public final static boolean getAllowDownload() {
		return !System.getProperty(ALLOW_DOWNLOAD, "0").equals("0");
	}

	public final static void setAllowDownload(String allow) {
		System.setProperty(ALLOW_DOWNLOAD, allow);
	}

	public final static boolean getAllowUpload() {
		return !System.getProperty(ALLOW_UPLOAD, "0").equals("0");
	}

	public final static void setAllowUpload(String allow) {
		System.setProperty(ALLOW_UPLOAD, allow);
	}

}
