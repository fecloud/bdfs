/**
 * 
 */
package com.yuncore.bdfs.server;

import org.apache.log4j.Logger;

import com.yuncore.bdfs.Const;
import com.yuncore.bdfs.app.imple.ServerContextImple;
import com.yuncore.bdfs.db.CloudHistoryDao;
import com.yuncore.bdfs.http.cookie.DBCookieContainer;
import com.yuncore.bdfs.sync.cloud.GetCloudFile;
import com.yuncore.bdfs.sync.compare.CloudCompare;

/**
 * @author ouyangfeng
 * 
 */
public class BDFSServer extends Thread {

	Logger logger = Logger.getLogger(BDFSServer.class.getSimpleName());

	private void setEnv() {
		System.setProperty(Const.DATA, System.getProperty("java.io.tmpdir"));
		System.setProperty(Const.CONTEXT, ServerContextImple.class.getName());
		System.setProperty(Const.COOKIECONTAINER, DBCookieContainer.class.getName());
		logger.warn("java.io.tmpdir:" + System.getProperty("java.io.tmpdir"));
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
		while (true) {
			GetCloudFile getCloudFile = new GetCloudFile(4, "/");
			if (getCloudFile.list()) {
				new CloudHistoryDao().insert();
				new CloudCompare().compare();
			}
		}
	}

}
