/**
 * 
 */
package com.yuncore.bdfs.server.http.cookie;

import org.apache.log4j.Logger;
import org.json.JSONArray;

import com.yuncore.bdfs.http.cookie.AppCookieContainer;
import com.yuncore.bdfs.http.cookie.Cookie;
import com.yuncore.bdfs.server.dao.CookieDao;

/**
 * @author ouyangfeng
 * 
 */
public class DBCookieContainer extends AppCookieContainer {

	Logger logger = Logger.getLogger(DBCookieContainer.class.getSimpleName());

	private volatile boolean load;

	private CookieDao cookieDao = new CookieDao();
	
	private String preString;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yuncore.bdfs.http.cookie.FileCookieContainer#save()
	 */
	@Override
	public boolean save() {
		if(preString != null) {
			final String c = toJSON();
			System.out.println(c.equals(preString));
			
		}
		preString = toJSON();
		return cookieDao.saveCookie(toJSON());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yuncore.bdfs.http.cookie.FileCookieContainer#read()
	 */
	@Override
	public boolean read() {
		if (!load) {
			final String jsons = cookieDao.getCookie();
			if (null != jsons && jsons.trim().length() > 0) {
				final JSONArray array = new JSONArray(jsons);
				if (array != null && array.length() > 0) {
					Cookie cookie = null;
					for (int i = 0; i < array.length(); i++) {
						cookie = new Cookie();
						cookie.formJOSN(array.getJSONObject(i));
						cookies.add(cookie);
					}
					load = true;
				}
			} else {
				logger.debug("from db cookie null");
				load = false;
			}
		}
		return load;
	}

}
