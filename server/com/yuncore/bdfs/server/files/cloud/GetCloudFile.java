/**
 * 
 */
package com.yuncore.bdfs.server.files.cloud;

import com.yuncore.bdfs.server.dao.CloudFileDao;
import com.yuncore.bdfs.server.util.Stopwatch;
import com.yuncore.bdfs.task.TaskExecute;
import com.yuncore.bdfs.task.TaskService;

/**
 * @author ouyangfeng
 * 
 */
public class GetCloudFile extends TaskService {

	public static final String CLOUDLIST_SESSION = "cloudlist_session";

	private String dir;

	private CloudFileDao cloudFileDao;

	public GetCloudFile(int threads, String dir) {
		this.threads = threads;
		this.dir = dir;
		cloudFileDao = new CloudFileDao();
	}

	public synchronized boolean list() {
		Stopwatch stopwatch = new Stopwatch();
		stopwatch.start();
		System.setProperty(CLOUDLIST_SESSION, "" + System.currentTimeMillis());
		taskContainer.addTask(new GetCloudFileTask(dir));
		waitTaskFinish();
		cloudFileDao.insertAllCacaheFlush();
		stopwatch.stop(getTaskExecuteName());
		return true;
	}

	public synchronized boolean setList(String dir) {
		this.dir = dir;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yuncore.dbpcs.task.TaskService#newTaskExecute()
	 */
	@Override
	protected TaskExecute newTaskExecute() {
		return new GetCloudFileExecute(taskStatus, taskContainer, cloudFileDao);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yuncore.dbpcs.task.TaskService#getTaskExecuteName()
	 */
	@Override
	protected String getTaskExecuteName() {
		return "GetCloudFile";
	}

}
