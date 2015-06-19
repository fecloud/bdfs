package com.yuncore.bdfs.app.imple;

import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.yuncore.bdfs.Const;
import com.yuncore.bdfs.api.Api;
import com.yuncore.bdfs.api.imple.ApiImple;
import com.yuncore.bdfs.app.Context;
import com.yuncore.bdfs.db.AccountDao;
import com.yuncore.bdfs.entity.Account;
import com.yuncore.bdfs.exception.ApiException;
import com.yuncore.bdfs.exception.BDFsException;
import com.yuncore.bdfs.tools.Util;

public class ServerContextImple implements Context {

	Logger logger = Logger.getLogger(ServerContextImple.class.getSimpleName());

	protected Properties properties = new Properties();

	/**
	 * 10分针刷新一次
	 */
	private static int interval = 10 * 60 * 1000;

	private volatile long time;

	@Override
	public String getProperty(String key) {
		return properties.getProperty(key);
	}

	@Override
	public String getProperty(String key, String defaultValue) {
		return properties.getProperty(key, defaultValue);
	}

	@Override
	public Object setProperty(String key, String value) {
		return properties.setProperty(key, value);
	}

	@Override
	public boolean load() throws BDFsException {
		if (time == 0 || System.currentTimeMillis() - time > interval) {
			time = System.currentTimeMillis();
			final Api api = new ApiImple();
			try {
				final Map<String, String> diskHomePage = api.diskHomePage();
				if (null != diskHomePage && !diskHomePage.isEmpty()) {
					properties.clear();
					properties.putAll(diskHomePage);
					return true;
				} else {
					logger.debug("not login or login expired reloging...");
					Util.rmDirFile(System.getProperty(Const.DATA));
					final boolean result = login();
					if (result) {
						final Map<String, String> map = api.diskHomePage();
						if (null != map && !map.isEmpty()) {
							properties.clear();
							properties.putAll(map);
						}
					}
					return result;
				}
			} catch (ApiException e) {
				throw new BDFsException("load diskHomePage error", e);
			}

		}
		return true;
	}

	@Override
	public String getUserName() {
		final Account account = new AccountDao().getAccount();
		if (null != account) {
			return account.getUsername();
		}
		return "";
		// return "ou.yangfeng@cmge.com";
	}

	@Override
	public String getPassWord() {
		final Account account = new AccountDao().getAccount();
		if (null != account) {
			return account.getPassword();
		}
		return "";
		// return "ouyangfeng";
	}

	@Override
	public boolean login() throws BDFsException {
		try {
			return new ApiImple().login(getUserName(), getPassWord());
		} catch (Exception e) {
			throw new BDFsException("login error", e);
		}

	}

}
