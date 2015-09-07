/**
 * 
 */
package com.yuncore.bdfs.cloud;

import com.yuncore.bdfs.Const;
import com.yuncore.bdfs.api.imple.FSApiImple;
import com.yuncore.bdfs.dao.CloudFileDao;
import com.yuncore.bdfs.entity.CloudFile;
import com.yuncore.bdfs.entity.CloudPageFile;
import com.yuncore.bdfs.exception.ApiException;
import com.yuncore.bdfs.http.cookie.AppCookieContainer;
import com.yuncore.bdfs.task.Task;
import com.yuncore.bdfs.task.TaskContainer;
import com.yuncore.bdfs.task.TaskExecute;
import com.yuncore.bdfs.task.TaskStatus;
import com.yuncore.bdfs.util.Log;

/**
 * @author ouyangfeng
 * 
 */
public class GetCloudFileExecute extends TaskExecute {

	private static final String TAG = "GetCloudFileExecute";

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
					final long session = Long.parseLong(System.getProperty(
							Const.CLOUDLIST_SESSION, "0"));
					
					for (CloudFile f : listFiles.getList()) {
						f.setSession(session);
					}
					cloudFileDao.insertAllCacahe(listFiles.getList());
					for (CloudFile f : listFiles.getList()) {
						if (f.isDirectory()) {
							// System.out.println(f.getAbsolutePath());
							taskContainer.addTask(new GetCloudFileTask(f
									.getAbsolutePath()));
						}
					}
				} else if (listFiles.getErrno() == -9) {
					Log.w(TAG, "dir:" + fileTask.getDir() + " is not exits"); // 目录不存在了
				} else {
					if (listFiles.getErrno() == -6) {
						Log.w(TAG, "cookie problem"); // cookie有错误
						System.setProperty(AppCookieContainer.COOKIE_LOAD,
								"false");
					}
					Log.w(TAG,
							"CloudPageFile listFiles error:"
									+ listFiles.getErrno());
					// 因为某种原因没有取得成功
					taskContainer.addTask(task);
				}

			} else {
				Log.w(TAG, "CloudPageFile listFiles null");
				// 因为某种原因没有取得成功
				taskContainer.addTask(task);
			}
		} catch (ApiException e) {
			Log.e(TAG, "GetCloudFileExecute list", e);
			// 因为网络失败取得失败
			taskContainer.addTask(task);
		}
	}
}
