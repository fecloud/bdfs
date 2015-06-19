package com.yuncore.bdfs.sync.local;

import java.util.List;

import com.yuncore.bdfs.entity.LocalFile;
import com.yuncore.bdfs.task.Task;
import com.yuncore.bdfs.task.TaskContainer;
import com.yuncore.bdfs.task.TaskExecute;
import com.yuncore.bdfs.task.TaskStatus;
import com.yuncore.bdfs.tools.FileListWrite;
import com.yuncore.bdfs.tools.Util;

public class GetLocalFileExecute extends TaskExecute {

	private FileListWrite fileListWrite;

	private FileExclude exclude;

	public GetLocalFileExecute(TaskStatus taskStatus,
			TaskContainer taskContainer, FileExclude exclude,
			FileListWrite fileListWrite) {
		super(taskStatus, taskContainer);
		this.fileListWrite = fileListWrite;
		this.exclude = exclude;
	}

	protected void analysisDIRFiles(GetLocalFileTask task) {

		final List<LocalFile> listFiles = Util.listFiles(task.getDir(),
				Long.parseLong(System.getProperty("locallist_session")));
		if (listFiles != null) {
			compare(listFiles, task.getDir());
		}
	}

	protected void compare(List<LocalFile> files, String dir) {

		// 把最新本地结果放入数据库
		fileListWrite.insertAllCacahe(files);

		checkExcludeAndAddTask(files);

	}

	@Override
	protected void doTask(Task task) {
		analysisDIRFiles((GetLocalFileTask) task);
	}

	/**
	 * 检查并排除目录
	 * 
	 * @param files
	 */
	private void checkExcludeAndAddTask(List<LocalFile> files) {
		for (LocalFile f : files) {
			if (f.isDirectory()) {
				if (!exclude.rmExclude(f.getAbsolutePath())) {
					taskContainer.addTask(new GetLocalFileTask(f
							.getAbsolutePath()));
				}

			}
		}
	}
}
