package com.yuncore.bdfs.server.localflle;

import org.apache.log4j.Logger;

import com.yuncore.bdfs.server.dao.LocalFileDao;
import com.yuncore.bdfs.task.TaskExecute;
import com.yuncore.bdfs.task.TaskService;

public class UploadLocalFile extends TaskService {

	Logger logger = Logger.getLogger(UploadLocalFile.class.getSimpleName());

	private LocalFileDao localFileDao = new LocalFileDao();

	@Override
	protected TaskExecute newTaskExecute() {
		final UploadLocalFileExecute uploadLocalFileExecute = new UploadLocalFileExecute(
				taskStatus, taskContainer, localFileDao);
		return uploadLocalFileExecute;
	}

	@Override
	protected String getTaskExecuteName() {
		return "UploadLocalFile";
	}

	public void addTask(String filename) {
		logger.debug("addTask " + filename);
		taskContainer.addTask(new UploadLocalFileTask(filename));
	}

}
