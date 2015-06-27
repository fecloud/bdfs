package com.yuncore.bdfs.server.files.local;

import com.yuncore.bdfs.task.Task;

public class UploadLocalFileTask implements Task {

	private String filename;

	public UploadLocalFileTask(String filename) {
		super();
		this.filename = filename;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

}
