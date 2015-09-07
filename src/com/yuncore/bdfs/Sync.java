package com.yuncore.bdfs;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.yuncore.bdfs.app.ClientContext;
import com.yuncore.bdfs.cloud.GetCloudFile;
import com.yuncore.bdfs.ctrl.Httpd;
import com.yuncore.bdfs.http.cookie.DBCookieContainer;
import com.yuncore.bdfs.local.GetLocalFile;
import com.yuncore.bdfs.util.Log;

public class Sync implements Runnable {

	private static final String TAG = "Sync";

	private String syncdir;

	private Thread pcsSyncThread;

	private Set<String> localExcludeFiles = new HashSet<String>();

	private Set<String> cloudExcludeFiles = new HashSet<String>();

	// private UploadLocalFileList uploadLocalFileList;
	//
	// private CloudDownLoad cloudDownLoad;
	//
	// private LocalUpload localUpload;

	private GetLocalFile getLocalFile;

	private int httpPort = 18080;

	private Httpd httpd;

	public Sync(String[] args) {
		syncdir = args[1];
		setHttpPort(args);
		startHttp();
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
	
	private void startHttp(){
		if (httpd == null) {
			try {
				httpd = new Httpd(httpPort);
			} catch (IOException e) {
				Log.w(TAG, "start httpd service error");
			}
		}
	}

	private void addExcludeFiles(String[] args) {
		localExcludeFiles.add(Environment.BDSYNCDIR);
		localExcludeFiles.add("tmp");
		
		cloudExcludeFiles.add("tmp");
		
		if (args.length > 3) {
			Set<String> to = null;
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
		Environment.setSyncDir(syncdir);
		// System.setProperty(Const.TMP,
		// String.format("%s%s%s", syncdir, File.separator, Const.TMP_DIR));
		// System.setProperty("http_proxy", "localhost:8888");
		Environment.setContextClassName(ClientContext.class.getName());
		Environment.setCookiecontainerClassName(DBCookieContainer.class.getName());

	}

	@Override
	public void run() {
		Log.w("Sync", "sync dir:" + syncdir);
		setEnv();
		startCoreService();
	}

	private void startCoreService() {

		GetCloudFile cloudFile = new GetCloudFile(4, "/");
		cloudFile.addExclude(cloudExcludeFiles);
		cloudFile.list();
		
//		getLocalFile = new GetLocalFile(4, syncdir);
//		getLocalFile.addExclude(localExcludeFiles);
//		getLocalFile.list();

		// if (null == uploadLocalFileList) {
		// uploadLocalFileList = new UploadLocalFileList(localExcludeFiles);
		// uploadLocalFileList.start();
		// }
		// if (null == cloudDownLoad) {
		// cloudDownLoad = new CloudDownLoad(
		// System.getProperty(Const.SYNCDIR),
		// System.getProperty(Const.TMP));
		// cloudDownLoad.addExclude(cloudExcludeFiles);
		// cloudDownLoad.start();
		// }

		// if (null == localUpload) {
		// localUpload = new LocalUpload(System.getProperty(Const.SYNCDIR),
		// System.getProperty(Const.TMP));
		// localUpload.start();
		// }

		// new DeleteRepeat().start();
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
