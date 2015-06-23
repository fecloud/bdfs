package com.yuncore.bdfs.client.util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Log {

	private FileWriter writer;

	private int priority;

	private static final String line_separator = System.getProperty(
			"line.separator", "\n");

	private Log() {
		try {
			writer = new FileWriter(System.getProperty("java.io.logdir", "")
					+ "bdfs.log");
			priority = getPriority(System.getProperty("java.io.logpriority",
					"VERBOSE"));
		} catch (IOException e) {
		}
	}

	private static final Log log = new Log();

	/**
	 * Priority constant for the println method; use Log.v.
	 */
	public static final int VERBOSE = 2;

	/**
	 * Priority constant for the println method; use Log.d.
	 */
	public static final int DEBUG = 3;

	/**
	 * Priority constant for the println method; use Log.i.
	 */
	public static final int INFO = 4;

	/**
	 * Priority constant for the println method; use Log.w.
	 */
	public static final int WARN = 5;

	/**
	 * Priority constant for the println method; use Log.e.
	 */
	public static final int ERROR = 6;

	/**
	 * Priority constant for the println method.
	 */
	public static final int ASSERT = 7;

	public static int v(String tag, String msg) {
		return println_native(VERBOSE, tag, msg);
	}

	public static int v(String tag, String msg, Throwable tr) {
		return println_native(VERBOSE, tag, msg + '\n'
				+ getStackTraceString(tr));
	}

	public static int d(String tag, String msg) {
		return println_native(DEBUG, tag, msg);
	}

	public static int d(String tag, String msg, Throwable tr) {
		return println_native(DEBUG, tag, msg + '\n' + getStackTraceString(tr));
	}

	public static int i(String tag, String msg) {
		return println_native(INFO, tag, msg);
	}

	public static int i(String tag, String msg, Throwable tr) {
		return println_native(INFO, tag, msg + '\n' + getStackTraceString(tr));
	}

	public static int w(String tag, String msg) {
		return println_native(WARN, tag, msg);
	}

	public static int w(String tag, String msg, Throwable tr) {
		return println_native(WARN, tag, msg + '\n' + getStackTraceString(tr));
	}

	public static int w(String tag, Throwable tr) {
		return println_native(WARN, tag, getStackTraceString(tr));
	}

	public static int e(String tag, String msg) {
		return println_native(ERROR, tag, msg);
	}

	public static int e(String tag, String msg, Throwable tr) {
		return println_native(ERROR, tag, msg + '\n' + getStackTraceString(tr));
	}

	/**
	 * Handy function to get a loggable stack trace from a Throwable
	 * 
	 * @param tr
	 *            An exception to log
	 */
	public static String getStackTraceString(Throwable tr) {
		if (tr == null) {
			return "";
		}

		// This is to reduce the amount of log spew that apps do in the
		// non-error
		// condition of the network being unavailable.
		Throwable t = tr;
		while (t != null) {
			if (t instanceof UnknownHostException) {
				return "";
			}
			t = t.getCause();
		}

		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		tr.printStackTrace(pw);
		return sw.toString();
	}

	private static int getPriority(String priority) {
		if (priority != null && priority.trim().length() > 0) {
			if ("VERBOSE".equalsIgnoreCase(priority)) {
				return VERBOSE;
			}
			if ("DEBUG".equalsIgnoreCase(priority)) {
				return DEBUG;
			}
			if ("INFO".equalsIgnoreCase(priority)) {
				return INFO;
			}
			if ("WARN".equalsIgnoreCase(priority)) {
				return WARN;
			}
			if ("ERROR".equalsIgnoreCase(priority)) {
				return ERROR;
			}

		}
		return 1;
	}

	private static String getPriority(int priority) {
		if (priority <= ERROR && priority >= VERBOSE) {
			switch (priority) {
			case VERBOSE:
				return "VERBOSE";
			case DEBUG:
				return "DEBUG";
			case INFO:
				return "INFO";
			case WARN:
				return "WARN";
			case ERROR:
				return "ERROR";
			}

		}
		return "";
	}

	private static String getTime() {
		final SimpleDateFormat format = new SimpleDateFormat(
				"MM-dd HH:mm:ss,SSS");
		return format.format(new Date());
	}

	private static int println_native(int priority, String tag, String msg) {
		if (log.priority <= priority) {
			final String str = String.format("%s [%s] %s :%s%s", getTime(),
					getPriority(priority), tag, msg, line_separator);
			try {
				System.out.print(str);
				log.writer.write(str);
				log.writer.flush();
			} catch (IOException e) {
			}
		}
		return 1;
	}
	
	public static void main(String[] args) {
		Log.d("==", "msg");
	}
}
