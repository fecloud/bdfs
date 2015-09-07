package com.yuncore.bdfs.util;

import java.util.ArrayList;
import java.util.List;

public class BDFSFileExclude implements FileExclude {

	protected List<String> excludes = new ArrayList<String>();

	@Override
	public synchronized List<String> getExcludes() {
		return excludes;
	}

	@Override
	public synchronized boolean rmExclude(String exclude) {
		if (!excludes.isEmpty() && excludes.contains(exclude)) {
			return excludes.remove(exclude);
		}
		return false;
	}

	@Override
	public synchronized void addExclude(List<String> files) {
		excludes.addAll(files);
	}

	@Override
	public synchronized void addExclude(String file) {
		excludes.add(file);
	}

}
