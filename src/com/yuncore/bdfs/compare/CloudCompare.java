/**
 * 
 */
package com.yuncore.bdfs.compare;

import com.yuncore.bdfs.Environment;
import com.yuncore.bdfs.dao.CloudCompareDao;
import com.yuncore.bdfs.dao.CloudFileDao;

/**
 * @author ouyangfeng
 * 
 */
public class CloudCompare extends LocalCompare {

	public CloudCompare() {
		compareDao = new CloudCompareDao();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yuncore.bdfs.sync.compare.LocalCompare#getSession()
	 */
	@Override
	protected long getSession() {
		return Long.parseLong(Environment.getCloudlistSession());
	}
	
	/* (non-Javadoc)
	 * @see com.yuncore.bdfs.compare.LocalCompare#needCompareBefore()
	 */
	@Override
	public synchronized boolean needCompareBefore() {
		final CloudFileDao cloudFileDao = new CloudFileDao();
		return cloudFileDao.count() > 0;
	}
}
