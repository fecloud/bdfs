/**
 * @(#) CloudFileMonitor.java Created on 2015年9月8日
 *
 * 
 */
package com.yuncore.bdfs.sync.service;

import com.yuncore.bdfs.Argsment;
import com.yuncore.bdfs.cloud.GetCloudFile;
import com.yuncore.bdfs.compare.CloudCompare;

/**
 * The class <code>CloudFileMonitor</code>
 * 
 * @author Feng OuYang
 * @version 1.0
 */
public class CloudFileMonitor extends LocalFileMonitor {

	private static final String TAG = "CloudFileMonitor";

	/**
	 * @param args
	 */
	public CloudFileMonitor(String[] args) {
		super(args);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yuncore.bdfs.sync.service.LocalFileMonitor#getTag()
	 */
	@Override
	protected String getTag() {
		return TAG;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yuncore.bdfs.sync.service.LocalFileMonitor#getExcludeFilesFlag()
	 */
	@Override
	public String getExcludeFilesFlag() {
		return "-c";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yuncore.bdfs.sync.service.LocalFileMonitor#getSleep()
	 */
	@Override
	protected long getSleep() {
		return Argsment.getCloudFileMonitorInterval();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yuncore.bdfs.sync.service.LocalFileMonitor#listNewFiles()
	 */
	@Override
	protected boolean listNewFiles() {
		final GetCloudFile getCloudFile = new GetCloudFile(4, "/");
		getCloudFile.addExclude(excludeFiles);
		return getCloudFile.list();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yuncore.bdfs.sync.service.LocalFileMonitor#compareFiles()
	 */
	@Override
	protected boolean compareFiles() {
		final CloudCompare cloudCompare = new CloudCompare();
		return cloudCompare.compare();
	}

}
