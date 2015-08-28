/**
 * 
 */
package com.yuncore.bdfs.server;

import org.apache.log4j.Logger;

import com.yuncore.bdfs.server.app.ServerContext;
import com.yuncore.bdfs.server.compare.CloudCompare;
import com.yuncore.bdfs.server.dao.CloudHistoryDao;
import com.yuncore.bdfs.server.files.cloud.GetCloudFile;
import com.yuncore.bdfs.server.http.cookie.DBCookieContainer;
import com.yuncore.bdfs.server.repeat.DownLoadRepeat;
import com.yuncore.bdfs.server.repeat.UpLoadRepeat;

/**
 * @author ouyangfeng
 * 
 */
public class BDFSServer extends Thread {

	Logger logger = Logger.getLogger(BDFSServer.class.getSimpleName());

	private DownLoadRepeat downLoadRepeat;

	private UpLoadRepeat upLoadRepeat;

	private void setEnv() {
		System.setProperty(Const.CONTEXT, ServerContext.class.getName());
		System.setProperty(Const.COOKIECONTAINER,
				DBCookieContainer.class.getName());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		setName("BDFSServer");
		setEnv();
		startReperat();
		while (true) {
			GetCloudFile getCloudFile = new GetCloudFile(4, "/");
			if (getCloudFile.list()) {
				new CloudHistoryDao().insert();
				new CloudCompare().compare();
			}
			break;
		}
	}

	public void startReperat() {
		if (downLoadRepeat == null) {
			downLoadRepeat = new DownLoadRepeat();
			downLoadRepeat.start();
		}
		if (upLoadRepeat == null) {
			upLoadRepeat = new UpLoadRepeat();
			upLoadRepeat.start();
		}
	}

	public static void main(String[] args) {
		BDFSServer bdfsServer = new BDFSServer();
		bdfsServer.start();
	}

}
