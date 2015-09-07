/**
 * 
 */
package com.yuncore.bdfs.http.cookie;

import org.json.JSONArray;

import com.yuncore.bdfs.dao.CookieDao;
import com.yuncore.bdfs.util.Log;

/**
 * @author ouyangfeng
 * 
 */
public class DBCookieContainer extends AppCookieContainer {

	private static final String TAG = "DBCookieContainer";

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
			Log.d(TAG, "from db cookie null");
		}
		return false;
	}

	@Override
	protected boolean saveTo() {
		return cookieDao.saveCookie(toJSON());
	}

}
