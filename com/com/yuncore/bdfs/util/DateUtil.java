package com.yuncore.bdfs.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

	public static String formatTime(long time) {
		final SimpleDateFormat format = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		return format.format(new Date(time));
	}
	
}
