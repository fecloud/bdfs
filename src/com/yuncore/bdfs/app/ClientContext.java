package com.yuncore.bdfs.app;

import java.util.Map;

import com.yuncore.bdfs.api.FSApi;
import com.yuncore.bdfs.api.imple.FSApiImple;
import com.yuncore.bdfs.app.imple.AppContext;
import com.yuncore.bdfs.exception.ApiException;
import com.yuncore.bdfs.exception.BDFSException;

public class ClientContext extends AppContext {

	static final String TAG = "ClientContext";

	@Override
	public boolean load() throws BDFSException {
		if (time == 0 || System.currentTimeMillis() - time > interval) {
			time = System.currentTimeMillis();
			final FSApi api = new FSApiImple();
			try {
				final Map<String, String> diskHomePage = api.diskHomePage();
				if (null != diskHomePage && !diskHomePage.isEmpty()) {
					properties.putAll(diskHomePage);
					return true;
				} else {
					properties.clear();
					return false;
				}
			} catch (ApiException e) {
				throw new BDFSException("load diskHomePage error", e);
			}

		}
		return true;
	}

}
