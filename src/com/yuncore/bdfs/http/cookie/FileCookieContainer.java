package com.yuncore.bdfs.http.cookie;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONArray;

import com.yuncore.bdfs.Const;

public class FileCookieContainer implements CookieContainer {

	protected Logger logger = Logger.getLogger(FileCookieContainer.class
			.getName());

	private String filename = System.getProperty(Const.DATA) + File.separator
			+ "cookie.json";

	public List<Cookie> cookies = new ArrayList<Cookie>();

	protected boolean load;

	public synchronized boolean addCookie(Cookie cookie) {
		boolean result = false;

		if (null != cookie && cookie.getDomain() != null) {
			Cookie findCookie = findCookie(cookie);

			if (null != findCookie) {
				findCookie.update(findCookie);
				result = true;
			} else {
				result = cookies.add(cookie);
			}
			save();
		}

		return result;
	}

	public synchronized Cookie findCookie(Cookie cookie) {

		for (Cookie c : cookies) {
			if (cookie.getDomain() != null
					&& c.getDomain().equals(cookie.getDomain())
					&& c.getName().equals(cookie.getName())) {
				return c;
			}
		}
		return null;
	}

	public synchronized boolean removeCookie(Cookie cookie) {
		final Cookie c = findCookie(cookie);
		boolean result = false;
		if (null != c) {
			cookies.remove(c);
			result = save();
		}
		return result;
	}

	public synchronized String toJSON() {
		final JSONArray array = new JSONArray();
		for (Cookie c : cookies) {
			array.put(c.toJSON());
		}
		return array.toString();
	}

	public synchronized boolean saveToFile() {
		// logger.debug("saveToFile filename:" + filename);
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
			logger.error("saveToFile fail", e);
		}

		return true;
	}

	public synchronized boolean formFile() {
		// logger.debug("formFile");
		final String jsons = readFile();
		if (null != jsons) {
			final JSONArray array = new JSONArray(jsons);
			if (array != null && array.length() > 0) {
				Cookie cookie = null;
				for (int i = 0; i < array.length(); i++) {
					cookie = new Cookie();
					cookie.formJOSN(array.getString(i));
					cookies.add(cookie);
				}

			}
		} else {
			return false;
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
			logger.error("readFile fail", e);
		}
		return null;
	}

	public synchronized List<Cookie> getCookieList(String url) {

		if (!load) {
			read();
			load = true;
		}
		final List<Cookie> results = new ArrayList<Cookie>();
		final String[] strings = urlPathAndDomain(url);
		if (null != strings && strings.length == 2) {
			final String host = strings[0];
			final String path = strings[1];
			for (Cookie c : cookies) {
				if (host.endsWith(c.getDomain())
						&& path.startsWith(c.getPath())) {
					results.add(c);
				}
			}

		}
		return results;
	}

	public synchronized boolean addCookieOrUpdate(String string) {
		final Cookie cookie = new Cookie();
		if (cookie.parseCookie(string)) {
			addCookie(cookie);
		}
		return true;
	}

	public synchronized Cookie getCookie(String name) {
		for (Cookie c : cookies) {
			if (c.getName().equals(name)) {
				return c;
			}
		}
		return null;
	}

	public static String[] urlPathAndDomain(String string) {

		try {
			final URL url = new URL(string);
			String host = url.getHost();
			String path = url.getPath();
			if (path != null && path.equals("")) {
				path = "/";
			}
			return new String[] { host, path };
		} catch (Exception e) {

		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yuncore.bdfs.http.cookie.CookieContainer#clear()
	 */
	@Override
	public void clear() {
		cookies.clear();
		load = false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yuncore.bdfs.http.cookie.CookieContainer#save()
	 */
	@Override
	public boolean save() {
		return saveToFile();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yuncore.bdfs.http.cookie.CookieContainer#read()
	 */
	@Override
	public boolean read() {
		return formFile();
	}

}
