package com.yuncore.bdfs.client.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

public class FileZip {

	static final String TAG = "FileZip";

	private String src;

	private String dest;

	public FileZip(String src, String dest) {
		this.src = src;
		this.dest = dest;
	}

	public boolean zip() {
		final File file = new File(src);
		if (file.exists()) {
			final File destFile = new File(dest);
			if (!destFile.exists()) {
				destFile.getParentFile().mkdirs();
			}

			try {
				final GZIPOutputStream out = new GZIPOutputStream(
						new FileOutputStream(destFile));
				final FileInputStream in = new FileInputStream(file);

				final byte[] buffer = new byte[1024 * 100];
				int len = -1;

				while (-1 != (len = in.read(buffer))) {
					out.write(buffer, 0, len);
				}
				in.close();
				out.flush();
				out.close();
				return true;
			} catch (IOException e) {
				Log.e(TAG, "", e);
			}

		}
		return false;
	}
}
