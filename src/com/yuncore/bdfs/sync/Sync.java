package com.yuncore.bdfs.sync;

import java.io.IOException;

import com.yuncore.bdfs.Environment;
import com.yuncore.bdfs.app.ClientContext;
import com.yuncore.bdfs.ctrl.Httpd;
import com.yuncore.bdfs.http.cookie.DBCookieContainer;
import com.yuncore.bdfs.sync.service.CloudFileMonitor;
import com.yuncore.bdfs.sync.service.LocalFileMonitor;
import com.yuncore.bdfs.util.Log;

public class Sync {

	private static final String TAG = "Sync";

	private String syncdir;

	private int httpPort = 18080;

	private Httpd httpd;

	private String[] args;

	public Sync(String[] args) {
		this.args = args;
		syncdir = args[1];
		setHttpPort(args);
		startHttp();
	}

	private void setHttpPort(String[] args) {
		for (int i = 2; i < args.length; i++) {
			if (args[i].equals("-p") && (i + 1) <= args.length - 1) {
				try {
					httpPort = Integer.parseInt(args[i + 1]);
				} catch (NumberFormatException e) {
					Log.w(TAG, "parse httpport error use default " + httpPort);
				}
				break;
			}
		}
	}

	private void startHttp() {
		if (httpd == null) {
			try {
				httpd = new Httpd(httpPort);
			} catch (IOException e) {
				Log.w(TAG, "start httpd service error");
			}
		}
	}

	private void setEnv() {
		Environment.setSyncDir(syncdir);
		// System.setProperty(Const.TMP,
		// String.format("%s%s%s", syncdir, File.separator, Const.TMP_DIR));
		// System.setProperty("http_proxy", "localhost:8888");
		Environment.setContextClassName(ClientContext.class.getName());
		Environment.setCookiecontainerClassName(DBCookieContainer.class.getName());

	}

	public void start() {
		Log.w("Sync", "sync dir:" + syncdir);
		setEnv();
		startCoreService();
	}

	private void startCoreService() {
		new LocalFileMonitor(args).start();
		new CloudFileMonitor(args).start();
	}

	public void stop() {

	}

}
