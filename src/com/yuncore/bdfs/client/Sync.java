package com.yuncore.bdfs.client;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.yuncore.bdfs.Const;
import com.yuncore.bdfs.app.imple.ClientContextImle;
import com.yuncore.bdfs.http.cookie.FileCookieContainer;
import com.yuncore.bdfs.sync.down.CloudDownLoad;

public class Sync implements Runnable {

	private String syncdir;

	private Thread pcsSyncThread;

	private List<String> excludeFiles = new ArrayList<String>();

	private UploadLocalFileList uploadLocalFileList;

	private CloudDownLoad cloudDownLoad;

	public Sync(String[] args) {
		syncdir = args[0];
		addExcludeFiles(args);
	}

	private void addExcludeFiles(String[] args) {
		excludeFiles.add(Const.DATA_DIR);
		if (args.length > 1) {
			for (int i = 1; i < args.length; i++) {
				excludeFiles.add(args[i]);
			}
		}
	}

	private void setEnv() {
		System.setProperty(Const.SYNCDIR, syncdir);
		System.setProperty(Const.DATA, String.format("%s%s%s", syncdir,
				File.separator, Const.DATA_DIR));
		// System.setProperty("http_proxy", "localhost:8888");
		System.setProperty(Const.TMP, String.format("%s%s%s",
				System.getProperty(Const.DATA), File.separator, Const.TMP));
		System.setProperty(Const.CONTEXT, ClientContextImle.class.getName());
		System.setProperty(Const.COOKIECONTAINER,
				FileCookieContainer.class.getName());

	}

	@Override
	public void run() {
		setEnv();
		startCoreService();
	}

	private void startCoreService() {
//		if (null == uploadLocalFileList) {
//			uploadLocalFileList = new UploadLocalFileList(excludeFiles);
//			uploadLocalFileList.start();
//		}
		if (null == cloudDownLoad) {
			cloudDownLoad = new CloudDownLoad(
					System.getProperty(Const.SYNCDIR),
					System.getProperty(Const.TMP));
			cloudDownLoad.start();
		}
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
