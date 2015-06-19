package com.yuncore.bdfs.app.imple;

import org.apache.log4j.Logger;

import com.yuncore.bdfs.api.ServerApi;
import com.yuncore.bdfs.api.imple.ServerApiImple;
import com.yuncore.bdfs.entity.Account;
import com.yuncore.bdfs.exception.ServerApiException;

public class ClientContextImle extends ServerContextImple {

	Logger logger = Logger.getLogger(ClientContextImle.class.getSimpleName());

	@Override
	public String getPassWord() {
		final ServerApi serverApi = new ServerApiImple();

		try {
			final Account account = serverApi.getAccount();
			if (null != account) {
				return account.getPassword();
			}
		} catch (ServerApiException e) {
			logger.error("getAccount error", e);
		}

		return "";
	}

	@Override
	public String getUserName() {
		final ServerApi serverApi = new ServerApiImple();

		try {
			final Account account = serverApi.getAccount();
			if (null != account) {
				return account.getUsername();
			}
		} catch (ServerApiException e) {
			logger.error("getAccount error", e);
		}
		return "";
	}

}
