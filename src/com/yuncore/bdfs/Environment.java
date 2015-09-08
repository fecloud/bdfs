/**
 * @(#) Environment.java Created on 2015-9-7
 *
 * 
 */
package com.yuncore.bdfs;

/**
 * The class <code>Environment</code>
 * 
 * @author Feng OuYang
 * @version 1.0
 */
public final class Environment {

	/**
	 * 数据库文件路径
	 */
	public static final String DB_FILE = "bdfs.db";

	/**
	 * 日志文件路径
	 */
	public static final String LOG_FILE = "bdfs.log.file";

	/**
	 * 日志等级
	 */
	public static final String LOG_PRIORITY = "bdfs.log.priority";

	public static final String SYNCDIR = "bdfs.syncdir";

	public static final String BDSYNCDIR = ".bdsync";

	public static final String TMP = "bdfs.tmpdir";

	public static final String CONTEXT = "bdfs.context";

	public static final String COOKIECONTAINER = "bdfs.cookiecontainer";

	public static final String LOCALLIST_SESSION = "bdfs.locallist_session";

	public static final String CLOUDLIST_SESSION = "bdfs.cloudlist_session";
	
	/**
	 * 程序的命令行
	 */
	public static final String CMD = "sun.java.command";

	public static final String getDBFile() {
		return System.getProperty(DB_FILE, "bdfs.db");
	}

	public static final String getLogFile() {
		return System.getProperty(DB_FILE, "bdfs.log");
	}

	public static final String getLogPriority() {
		return System.getProperty(LOG_PRIORITY, "VERBOSE");
	}

	public static final String getJavaTmpDir() {
		return System.getProperty("java.io.tmpdir", "");
	}

	public static final String getSyncDir() {
		return System.getProperty(SYNCDIR, null);
	}

	public static final void setSyncDir(String dir) {
		System.setProperty(SYNCDIR, dir);
	}

	public static final String getContextClassName() {
		return System.getProperty(CONTEXT, null);
	}

	public static final void setContextClassName(String className) {
		System.setProperty(CONTEXT, className);
	}

	public static final void setCookiecontainerClassName(String className) {
		System.setProperty(COOKIECONTAINER, className);
	}

	public static final String getCookiecontainerClassName() {
		return System.getProperty(COOKIECONTAINER, null);
	}

	public static final void setLocallistSession(String session) {
		System.setProperty(LOCALLIST_SESSION, session);
	}

	public static final String getLocallistSession() {
		return System.getProperty(LOCALLIST_SESSION, "0");
	}

	public static final void setCloudlistSession(String session) {
		System.setProperty(CLOUDLIST_SESSION, session);
	}

	public static final String getCloudlistSession() {
		return System.getProperty(CLOUDLIST_SESSION, "0");
	}

	public static final String getCommdLine(){
		return System.getProperty(CMD, "");
	}
}
