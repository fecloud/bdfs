package com.yuncore.bdfs.client;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.yuncore.bdfs.client.app.ClientContext;
import com.yuncore.bdfs.client.ctrl.Httpd;
import com.yuncore.bdfs.client.down.CloudDownLoad;
import com.yuncore.bdfs.client.http.cookie.MemCookieContainer;
import com.yuncore.bdfs.client.repeat.DeleteRepeat;
import com.yuncore.bdfs.client.upload.LocalUpload;
import com.yuncore.bdfs.client.util.Log;

public class Sync implements Runnable {

	private static final String TAG = "Sync";

	private String syncdir;

	private Thread pcsSyncThread;

	private List<String> localExcludeFiles = new ArrayList<String>();

	private List<String> cloudExcludeFiles = new ArrayList<String>();

	private UploadLocalFileList uploadLocalFileList;

	private CloudDownLoad cloudDownLoad;

	private LocalUpload localUpload;

	private int httpPort = 18080;

	private Httpd httpd;

	public Sync(String[] args) {
		syncdir = args[1];
		setHttpPort(args);
		addExcludeFiles(args);
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

	private void addExcludeFiles(String[] args) {
		localExcludeFiles.add(Const.TMP_DIR);
		if (args.length > 3) {
			List<String> to = null;
			for (int i = 2; i < args.length; i++) {
				if (args[i].equals("-l")) {
					to = localExcludeFiles;
				} else if (args[i].equals("-c")) {
					to = cloudExcludeFiles;
				} else if (args[i].equals("-p")) {
					break;
				} else {
					to.add(args[i]);
				}
			}
		}
	}

	private void setEnv() {
		System.setProperty(Const.SYNCDIR, syncdir);
		System.setProperty(Const.TMP,
				String.format("%s%s%s", syncdir, File.separator, Const.TMP_DIR));
		// System.setProperty("http_proxy", "localhost:8888");
		System.setProperty(Const.CONTEXT, ClientContext.class.getName());
		System.setProperty(Const.COOKIECONTAINER,
				MemCookieContainer.class.getName());

	}

	@Override
	public void run() {
		Log.w("Sync", "sync dir:" + syncdir);
		setEnv();
		startCoreService();
	}

	private void startCoreService() {

		if (httpd == null) {
			try {
				httpd = new Httpd(httpPort);
			} catch (IOException e) {
				Log.w(TAG, "start httpd service error");
			}
		}

		if (null == uploadLocalFileList) {
			uploadLocalFileList = new UploadLocalFileList(localExcludeFiles);
			uploadLocalFileList.start();
		}
		// if (null == cloudDownLoad) {
		// cloudDownLoad = new CloudDownLoad(
		// System.getProperty(Const.SYNCDIR),
		// System.getProperty(Const.TMP));
		// cloudDownLoad.addExclude(cloudExcludeFiles);
		// cloudDownLoad.start();
		// }

		if (null == localUpload) {
			localUpload = new LocalUpload(System.getProperty(Const.SYNCDIR),
					System.getProperty(Const.TMP));
			localUpload.start();
		}

//		new DeleteRepeat().start();
	}

	public void start() {
		if (pcsSyncThread == null) {
			pcsSyncThread = new Thread(this);
			pcsSyncThread.setName(Sync.class.getSimpleName());
			pcsSyncThread.start();
		}
	}

	public void stop() {

	}

}
