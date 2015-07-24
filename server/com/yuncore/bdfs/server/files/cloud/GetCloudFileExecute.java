/**
 * 
 */
package com.yuncore.bdfs.server.files.cloud;

import org.apache.log4j.Logger;

import com.yuncore.bdfs.exception.ApiException;
import com.yuncore.bdfs.http.cookie.AppCookieContainer;
import com.yuncore.bdfs.server.api.imple.FSApiImple;
import com.yuncore.bdfs.server.dao.CloudFileDao;
import com.yuncore.bdfs.server.entity.CloudFile;
import com.yuncore.bdfs.server.entity.CloudPageFile;
import com.yuncore.bdfs.task.Task;
import com.yuncore.bdfs.task.TaskContainer;
import com.yuncore.bdfs.task.TaskExecute;
import com.yuncore.bdfs.task.TaskStatus;

/**
 * @author ouyangfeng
 * 
 */
public class GetCloudFileExecute extends TaskExecute {

	Logger logger = Logger.getLogger(GetCloudFileExecute.class.getSimpleName());

	private CloudFileDao cloudFileDao;

	/**
	 * @param taskStatus
	 * @param taskContainer
	 */
	public GetCloudFileExecute(TaskStatus taskStatus,
			TaskContainer taskContainer, CloudFileDao cloudFileDao) {
		super(taskStatus, taskContainer);
		this.cloudFileDao = cloudFileDao;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.yuncore.dbpcs.task.TaskExecute#doTask(com.yuncore.dbpcs.task.Task)
	 */
	@Override
	protected void doTask(Task task) {
		final GetCloudFileTask fileTask = (GetCloudFileTask) task;
		try {
			final CloudPageFile listFiles = new FSApiImple().list(fileTask
					.getDir());
			if (listFiles != null) {
				if (listFiles.getErrno() == 0 && listFiles.getList() != null) {
					cloudFileDao.insertAllCacahe(listFiles.getList());
					for (CloudFile f : listFiles.getList()) {
						if (f.isDirectory()) {
							// System.out.println(f.getAbsolutePath());
							taskContainer.addTask(new GetCloudFileTask(f
									.getAbsolutePath()));
						}
					}
				} else if (listFiles.getErrno() == -9) {
					logger.warn("dir:" + fileTask.getDir() + " is not exits"); // 目录不存在了
				} else {
					if (listFiles.getErrno() == -6) {
						logger.warn("cookie problem"); // cookie有错误
						System.setProperty(AppCookieContainer.COOKIE_LOAD,
								"false");
					}
					logger.warn("CloudPageFile listFiles error:"
							+ listFiles.getErrno());
					// 因为某种原因没有取得成功
					taskContainer.addTask(task);
				}

			} else {
				logger.warn("CloudPageFile listFiles null");
				// 因为某种原因没有取得成功
				taskContainer.addTask(task);
			}
		} catch (ApiException e) {
			logger.error("GetCloudFileExecute list", e);
			// 因为网络失败取得失败
			taskContainer.addTask(task);
		}
	}
}
