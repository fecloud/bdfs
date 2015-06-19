/**
 * 
 */
package com.yuncore.bdfs.sync.compare;

import com.yuncore.bdfs.db.CloudCompareDao;
import com.yuncore.bdfs.sync.cloud.GetCloudFile;

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
		return Long.parseLong(System
				.getProperty(GetCloudFile.CLOUDLIST_SESSION));
	}
}
