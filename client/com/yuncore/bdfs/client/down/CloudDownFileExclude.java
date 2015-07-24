package com.yuncore.bdfs.client.down;

import com.yuncore.bdfs.client.util.BDFSFileExclude;

public class CloudDownFileExclude extends BDFSFileExclude {

	@Override
	public synchronized boolean rmExclude(String exclude) {
		if (!excludes.isEmpty() && null != exclude) {
			for (String s : excludes) {
				if (exclude.startsWith(s)) {
					return true;
				}
			}
		}
		return false;
	}

}
