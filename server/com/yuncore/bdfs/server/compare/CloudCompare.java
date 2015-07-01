/**
 * 
 */
package com.yuncore.bdfs.server.compare;

import com.yuncore.bdfs.server.Const;
import com.yuncore.bdfs.server.dao.CloudCompareDao;

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
		return Long.parseLong(System.getProperty(Const.CLOUDLIST_SESSION, "0"));
	}
}
