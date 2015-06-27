package com.yuncore.bdfs.client.http.cookie;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.GZIPInputStream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.yuncore.bdfs.client.Const;
import com.yuncore.bdfs.client.util.Log;
import com.yuncore.bdfs.http.cookie.AppCookieContainer;
import com.yuncore.bdfs.http.cookie.Cookie;
import com.yuncore.bdfs.util.TextUtil;

public class MemCookieContainer extends AppCookieContainer {

	static final String TAG = "MemCookieContainer";

	@Override
	public boolean save() {
		return true;
	}

	@Override
	public boolean read() {
		boolean load = Boolean.getBoolean(System.getProperty("CookieLoader",
				"false"));
		if (!load) {

			final String result = fromServerCookie();
			if (result != null) {
				final JSONObject object = new JSONObject(result);
				if (object.has("code")
						&& object.getInt("code") == HttpURLConnection.HTTP_OK) {
					final JSONArray cookies = object.getJSONArray("data");
					if (cookies != null && cookies.length() > 0) {
						Cookie cookie = null;
						for (int i = 0; i < cookies.length(); i++) {
							cookie = new Cookie();
							cookie.formJOSN(cookies.getJSONObject(i));
							this.cookies.add(cookie);
						}

					}
					load = true;
					System.getProperty("CookieLoader", "true");
				}
			}
		}
		return load;
	}

	private String fromServerCookie() {
		String result = null;
		try {
			final HttpURLConnection conn = (HttpURLConnection) new URL(
					String.format("%s?action=cookie", Const.SERVER_ADD))
					.openConnection();
			conn.setDoOutput(false);
			conn.setDoInput(true);
			conn.connect();

			if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
				if (conn.getContentEncoding() != null
						&& conn.getContentEncoding().equalsIgnoreCase("gzip")) {
					result = TextUtil
							.readToString(
									new GZIPInputStream(conn.getInputStream()),
									"UTF-8");
				} else {
					result = TextUtil.readToString(conn.getInputStream(),
							"UTF-8");
				}
			}
			if (result != null && result.trim().length() > 0) {
				return result.trim();
			} else {
				result = null;
			}
			conn.disconnect();
		} catch (Exception e) {
			Log.e(TAG, "fromServerCookie error");
		}
		return result;
	}

}
