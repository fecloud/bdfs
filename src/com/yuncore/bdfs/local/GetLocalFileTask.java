package com.yuncore.bdfs.local;

import com.yuncore.bdfs.task.Task;

public class GetLocalFileTask implements Task {

	private String dir;

	public GetLocalFileTask(String dir) {
		super();
		this.dir = dir;
	}

	public String getDir() {
		return dir;
	}

	public void setDir(String dir) {
		this.dir = dir;
	}
	
}
