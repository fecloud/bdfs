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

	public static final String getDBFile() {
		return System.getProperty(DB_FILE, "bdfs.db");
	}

	public static final String getLogFile() {
		return System.getProperty(DB_FILE, "bdfs.log");
	}

	public static final String getLogPriority() {
		return System.getProperty(LOG_PRIORITY, "VERBOSE");
	}

	public static final String getTmpDir(){
		return System.getProperty("java.io.tmpdir", "");
	}
}
