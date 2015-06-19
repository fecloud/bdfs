package com.yuncore.bdfs.sync.local;

import java.util.ArrayList;
import java.util.List;

public class LocalFileExclude implements FileExclude {

	private List<String> exclude = new ArrayList<String>();

	@Override
	public synchronized List<String> getExcludes() {
		return exclude;
	}

	@Override
	public synchronized boolean rmExclude(String exclude) {
		if (!this.exclude.isEmpty() && this.exclude.contains(exclude)) {
			return this.exclude.remove(exclude);
		}
		return false;
	}

	@Override
	public synchronized void addExclude(List<String> files) {
		this.exclude.addAll(files);
	}

	@Override
	public synchronized void addExclude(String file) {
		this.exclude.add(file);
	}

}
