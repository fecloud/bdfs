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

	private CookieDao cookieDao = new CookieDao();

	@Override
	protected boolean readForm() {
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
				return true;
			}
		} else {
			logger.debug("from db cookie null");
		}
		return false;
	}

	@Override
	protected boolean saveTo() {
		return cookieDao.saveCookie(toJSON());
	}

}
