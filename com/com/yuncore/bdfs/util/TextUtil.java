package com.yuncore.bdfs.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class TextUtil {
	
	public static String readToString(InputStream in, String charset)
			throws IOException {
		if (null != in) {
			final ByteArrayOutputStream bos = new ByteArrayOutputStream();
			final byte[] buffer = new byte[1024];
			// 1KB的缓冲区
			int read = -1;
			while ((read = in.read(buffer)) > 0) { // 读取到缓冲区
				bos.write(buffer, 0, read);
			}
			return new String(bos.toByteArray(), charset);

		}
		return null;
	}
}
