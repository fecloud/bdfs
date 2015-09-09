/**
 * 
 */
package com.yuncore.bdfs.compare;

import com.yuncore.bdfs.Environment;
import com.yuncore.bdfs.dao.CloudCompareDao;
import com.yuncore.bdfs.dao.CloudFileDao;
import com.yuncore.bdfs.dao.CloudHistoryDao;

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yuncore.bdfs.compare.LocalCompare#needCompareBefore()
	 */
	@Override
	public synchronized boolean needCompareBefore() {
		final CloudFileDao cloudFileDao = new CloudFileDao();
		return cloudFileDao.count() > 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yuncore.bdfs.compare.LocalCompare#addNewHistory()
	 */
	@Override
	public synchronized boolean addNewHistory() {
		final CloudHistoryDao cloudHistoryDao = new CloudHistoryDao();
		final long time = Long.parseLong(Environment.getCloudlistSession());
		return cloudHistoryDao.insert(time);
	}
	
	/* (non-Javadoc)
	 * @see com.yuncore.bdfs.compare.LocalCompare#getTag()
	 */
	@Override
	public String getTag() {
		return "CloudCompare";
	}
	
}
