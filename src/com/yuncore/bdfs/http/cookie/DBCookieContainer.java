/**
 * 
 */
package com.yuncore.bdfs.http.cookie;

import org.json.JSONArray;

import com.yuncore.bdfs.db.CookieDao;

/**
 * @author ouyangfeng
 * 
 */
public class DBCookieContainer extends FileCookieContainer {

	private CookieDao cookieDao = new CookieDao();

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yuncore.bdfs.http.cookie.FileCookieContainer#save()
	 */
	@Override
	public boolean save() {
		return cookieDao.saveCookie(toJSON());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yuncore.bdfs.http.cookie.FileCookieContainer#read()
	 */
	@Override
	public boolean read() {

		final String jsons = cookieDao.getCookie();
		if (null != jsons && jsons.trim().length() > 0) {
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
			logger.debug("from db cookie null");
			return false;
		}

		return true;
	}

}
