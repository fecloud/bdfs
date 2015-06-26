package com.yuncore.bdfs.client.http.cookie;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.json.JSONArray;

import com.yuncore.bdfs.http.cookie.AppCookieContainer;
import com.yuncore.bdfs.http.cookie.Cookie;

public class FileCookieContainer extends AppCookieContainer {

	private volatile boolean load;

	private String filename = System.getProperty("java.io.tmpdir")
			+ File.separator + "cookie.json";

	public synchronized boolean saveToFile() {
		final File file = new File(filename);
		try {
			if (!file.exists()) {
				file.getParentFile().mkdirs();
				file.createNewFile();
			}
			final FileOutputStream out = new FileOutputStream(file);
			out.write(toJSON().getBytes("UTF-8"));
			out.flush();
			out.close();
		} catch (Exception e) {
			System.err.println("saveToFile fail" + "\n" + e.getMessage());
		}

		return true;
	}

	public synchronized String readFile() {
		final File file = new File(filename);
		try {
			if (file.exists()) {
				final byte[] buff = new byte[1024];
				int len = 0;
				final ByteArrayOutputStream out = new ByteArrayOutputStream();
				final FileInputStream in = new FileInputStream(file);
				while (-1 != (len = in.read(buff))) {
					out.write(buff, 0, len);
				}
				in.close();
				return new String(out.toByteArray(), "UTF-8");
			}

		} catch (Exception e) {
			System.err.println("readFile fail" + "\n" + e.getMessage());
		}
		return null;
	}

	public synchronized boolean formFile() {
		final String jsons = readFile();
		if (null != jsons) {
			final JSONArray array = new JSONArray(jsons);
			if (array != null && array.length() > 0) {
				Cookie cookie = null;
				for (int i = 0; i < array.length(); i++) {
					cookie = new Cookie();
					cookie.formJOSN(array.getJSONObject(i));
					cookies.add(cookie);
				}

			}
		} else {
			return false;
		}

		return true;
	}

	@Override
	public boolean save() {
		return saveToFile();
	}

	@Override
	public boolean read() {
		if (!load) {
			load = formFile();
		}
		return load;
	}

}
