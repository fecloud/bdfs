package com.yuncore.bdfs.client;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.yuncore.bdfs.client.app.ClientContext;
import com.yuncore.bdfs.client.down.CloudDownLoad;
import com.yuncore.bdfs.client.http.cookie.MemCookieContainer;
import com.yuncore.bdfs.client.upload.LocalUpload;
import com.yuncore.bdfs.client.util.Log;

public class Sync implements Runnable {

	private String syncdir;

	private Thread pcsSyncThread;

	private List<String> localExcludeFiles = new ArrayList<String>();
	
	private List<String> cloudExcludeFiles = new ArrayList<String>();

	private UploadLocalFileList uploadLocalFileList;

	private CloudDownLoad cloudDownLoad;
	
	private LocalUpload localUpload;

	public Sync(String[] args) {
		syncdir = args[1];
		addExcludeFiles(args);
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
		
		if (null == uploadLocalFileList) {
			uploadLocalFileList = new UploadLocalFileList(localExcludeFiles);
			uploadLocalFileList.start();
		}
		if (null == cloudDownLoad) {
			cloudDownLoad = new CloudDownLoad(
					System.getProperty(Const.SYNCDIR),
					System.getProperty(Const.TMP));
			cloudDownLoad.addExclude(cloudExcludeFiles);
			cloudDownLoad.start();
		}
		
		if(null == localUpload){
			localUpload = new LocalUpload(System.getProperty(Const.SYNCDIR),
					System.getProperty(Const.TMP));
			localUpload.start();
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
