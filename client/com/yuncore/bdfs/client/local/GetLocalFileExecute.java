package com.yuncore.bdfs.client.local;

import java.util.List;

import com.yuncore.bdfs.client.Const;
import com.yuncore.bdfs.client.util.FileListWrite;
import com.yuncore.bdfs.client.util.FileUtil;
import com.yuncore.bdfs.entity.BDFSFile;
import com.yuncore.bdfs.task.Task;
import com.yuncore.bdfs.task.TaskContainer;
import com.yuncore.bdfs.task.TaskExecute;
import com.yuncore.bdfs.task.TaskStatus;

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

		final List<BDFSFile> listFiles = FileUtil.listFiles(task.getDir(), Long
				.parseLong(System.getProperty(Const.LOCALLIST_SESSION, "0")));
		if (listFiles != null) {
			compare(listFiles, task.getDir());
		}
	}

	protected void compare(List<BDFSFile> files, String dir) {

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
	private void checkExcludeAndAddTask(List<BDFSFile> files) {
		for (BDFSFile f : files) {
			if (f.isDirectory()) {
				if (!exclude.rmExclude(f.getAbsolutePath())) {
					taskContainer.addTask(new GetLocalFileTask(f
							.getAbsolutePath()));
				}

			}
		}
	}
}
