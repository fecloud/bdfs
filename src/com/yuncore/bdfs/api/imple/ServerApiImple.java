package com.yuncore.bdfs.api.imple;

import java.net.HttpURLConnection;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.yuncore.bdfs.api.ServerApi;
import com.yuncore.bdfs.entity.Account;
import com.yuncore.bdfs.entity.CloudFile;
import com.yuncore.bdfs.exception.ServerApiException;
import com.yuncore.bdfs.http.Http;
import com.yuncore.bdfs.http.Http.Method;
import com.yuncore.bdfs.http.HttpOutput;

public class ServerApiImple implements ServerApi {

	Logger logger = Logger.getLogger(ServerApiImple.class.getSimpleName());

	public static final String server_add = "http://yuncore.aliapp.com/bdfs";

	// public static final String server_add =
	// "http://localhost:8080/bdfs/bdfs";

	@Override
	public boolean uploadlocal(String file) throws ServerApiException {
		final String url = String.format("%s?action=%s", server_add,
				"uploadlocal");
		final HttpOutput httpOutput = new HttpOutput(url, file);
		try {
			if (httpOutput.http()) {
				final String result = httpOutput.result();
				if (null != result) {
					final JSONObject object = new JSONObject(result);
					if (object.has("code")
							&& object.getInt("code") == HttpURLConnection.HTTP_OK) {
						return true;
					}
				}
			}
		} catch (Exception e) {
			throw new ServerApiException("uploadlocal error", e);
		}
		return false;
	}

	@Override
	public Account getAccount() throws ServerApiException {
		try {
			Http http = new Http(String.format("%s?action=%s", server_add,
					"account"), Method.GET);
			if (http.http()) {
				final String result = http.result();
				if (null != result) {
					final JSONObject object = new JSONObject(result);
					if (object.has("code")
							&& object.getInt("code") == HttpURLConnection.HTTP_OK) {
						final Account account = new Account();
						account.formJOSN(result);
						return account;
					}
				}
			}
		} catch (Exception e) {
			throw new ServerApiException("getAccount error", e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yuncore.bdfs.api.ServerApi#getDownload()
	 */
	@Override
	public CloudFile getDownload() throws ServerApiException {
		try {
			Http http = new Http(String.format("%s?action=%s", server_add,
					"getdownload"), Method.GET);
			if (http.http()) {
				final String result = http.result();
				if (null != result) {
					final JSONObject object = new JSONObject(result);
					if (object.has("code")
							&& object.getInt("code") == HttpURLConnection.HTTP_OK
							&& object.has("data")) {
						final CloudFile cloudFile = new CloudFile();
						cloudFile.formSuperJOSN(object.getString("data"));
						return cloudFile;
					}
				}
			}
		} catch (Exception e) {
			throw new ServerApiException("getDownload error", e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yuncore.bdfs.api.ServerApi#deldownload(java.lang.String)
	 */
	@Override
	public boolean deldownload(String id) throws ServerApiException {
		try {
			Http http = new Http(String.format("%s?action=%s&id=%s",
					server_add, "deldownload", id), Method.GET);
			if (http.http()) {
				final String result = http.result();
				if (null != result) {
					final JSONObject object = new JSONObject(result);
					if (object.has("code")
							&& object.getInt("code") == HttpURLConnection.HTTP_OK
							&& object.has("data")) {
						return object.getBoolean("data");
					}
				}
			}
		} catch (Exception e) {
			throw new ServerApiException("deldownload error", e);
		}
		return false;
	}

}
