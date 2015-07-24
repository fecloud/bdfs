package com.yuncore.bdfs.server.api.imple;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.yuncore.bdfs.api.BDFSURL;
import com.yuncore.bdfs.app.Context;
import com.yuncore.bdfs.entity.CloudFile;
import com.yuncore.bdfs.exception.ApiException;
import com.yuncore.bdfs.http.Http;
import com.yuncore.bdfs.http.Http.Method;
import com.yuncore.bdfs.server.Const;
import com.yuncore.bdfs.server.api.FSApi;
import com.yuncore.bdfs.server.entity.CloudPageFile;
import com.yuncore.bdfs.util.DateUtil;

public class FSApiImple implements FSApi {

	public static boolean DEBUG = true;

	private static Context context;

	final static String TOKEN_KEY = "MYBDSTOKEN";

	/**
	 * 每页数量
	 */
	private final int PAGESIZE = 2000;

	public FSApiImple() {
		super();
		inStanceContext();
	}

	private void inStanceContext() {
		try {
			if (null == context) {
				context = (Context) Class.forName(
						System.getProperty(Const.CONTEXT)).newInstance();
			}
		} catch (Exception e) {
		}
	}

	@Override
	public CloudPageFile list(String dir) throws ApiException {
		int i = 1;
		CloudPageFile file = new CloudPageFile();
		file.setList(new ArrayList<CloudFile>());
		CloudPageFile pageFile = null;
		while ((pageFile = list(dir, i)) != null) {
			file.setErrno(pageFile.getErrno());
			file.getList().addAll(pageFile.getList());
			if (pageFile.getErrno() != 0 || pageFile.getList().isEmpty()
					|| pageFile.getList().size() < PAGESIZE) {
				break;
			}
			i++;
		}
		return file;
	}

	@Override
	public CloudPageFile list(String dir, int page) throws ApiException {
		return list(dir, page, PAGESIZE);
	}

	@Override
	public CloudPageFile list(String dir, int page, int page_num)
			throws ApiException {
		try {
			if (context.load()) {
				final long c_time = DateUtil.current_time_ss();
				final String url = BDFSURL.list(page, page_num, dir, c_time,
						context.getProperty(TOKEN_KEY));

				final Http http = new Http(url, Method.GET);
				if (http.http()) {
					// if (DEBUG)
					// logger.debug(String.format("list result:%s",
					// http.result()));
					final CloudPageFile pageFile = new CloudPageFile();
					if (pageFile.formJOSN(http.result())) {
						return pageFile;
					}
				}

			}

		} catch (Exception e) {
			throw new ApiException("list error", e);
		}
		return null;
	}

	@Override
	public Map<String, String> diskHomePage() throws ApiException {
		final String url = BDFSURL.diskHomePage();

		final Http http = new Http(url, Method.GET);
		try {

			if (http.http()) {
				// if (DEBUG)
				// logger.debug(String.format("diskHomePage:%s",
				// http.result()));
				final Pattern pattern = Pattern
						.compile("yunData\\.\\w+\\s*=\\s*['|\"]\\w*['|\"];");
				final Matcher matcher = pattern.matcher(http.result());
				String temp = null;
				String[] strings = null;
				Map<String, String> maps = new Hashtable<String, String>();
				while (matcher.find()) {
					temp = matcher.group();
					if (null != temp) {
						strings = temp.split("=");
						if (null != strings && strings.length == 2) {
							maps.put(
									strings[0].trim()
											.replaceAll("yunData.", ""),
									strings[1].trim().replaceAll("'", "")
											.replaceAll(";", "")
											.replaceAll("\"", ""));
						}
					}
				}
				return maps;
			}
		} catch (IOException e) {
			throw new ApiException("diskHomePage error", e);
		}
		return null;
	}

}
