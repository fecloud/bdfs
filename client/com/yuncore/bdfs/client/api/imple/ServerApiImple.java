package com.yuncore.bdfs.client.api.imple;

import java.net.HttpURLConnection;

import org.json.JSONObject;

import com.yuncore.bdfs.client.Const;
import com.yuncore.bdfs.client.api.ServerApi;
import com.yuncore.bdfs.entity.BDFSFile;
import com.yuncore.bdfs.exception.ServerApiException;
import com.yuncore.bdfs.http.Http;
import com.yuncore.bdfs.http.Http.Method;
import com.yuncore.bdfs.http.HttpOutput;

public class ServerApiImple implements ServerApi {

	@Override
	public boolean uploadlocal(String file) throws ServerApiException {
		final String url = String.format("%s?action=%s", Const.SERVER_ADD,
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yuncore.bdfs.api.ServerApi#getDownload()
	 */
	@Override
	public BDFSFile getDownload() throws ServerApiException {
		try {
			Http http = new Http(String.format("%s?action=%s",
					Const.SERVER_ADD, "getdownload"), Method.GET);
			if (http.http()) {
				final String result = http.result();
				if (null != result) {
					final JSONObject object = new JSONObject(result);
					if (object.has("code")
							&& object.getInt("code") == HttpURLConnection.HTTP_OK
							&& object.has("data")) {
						final BDFSFile cloudFile = new BDFSFile();
						cloudFile.formJOSN(object.getJSONObject("data"));
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
					Const.SERVER_ADD, "deldownload", id), Method.GET);
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

	@Override
	public boolean uploadCookie(String file) throws ServerApiException {
		final String url = String.format("%s?action=%s", Const.SERVER_ADD,
				"uploadcookie");
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

}
