package com.yuncore.bdfs.local;

import java.util.ArrayList;
import java.util.List;

import com.yuncore.bdfs.Const;
import com.yuncore.bdfs.dao.LocalFileDao;
import com.yuncore.bdfs.entity.BDFSFile;
import com.yuncore.bdfs.task.Task;
import com.yuncore.bdfs.task.TaskContainer;
import com.yuncore.bdfs.task.TaskExecute;
import com.yuncore.bdfs.task.TaskStatus;
import com.yuncore.bdfs.util.FileExclude;
import com.yuncore.bdfs.util.FileUtil;

public class GetLocalFileExecute extends TaskExecute {

	private FileExclude exclude;

	private String root;

	private LocalFileDao localFileDao;

	public GetLocalFileExecute(String root, TaskStatus taskStatus,
			TaskContainer taskContainer, FileExclude exclude,
			LocalFileDao localFileDao) {
		super(taskStatus, taskContainer);
		this.root = root;
		this.exclude = exclude;
		this.localFileDao = localFileDao;
	}

	protected void getDirFiles(GetLocalFileTask task) {

		final List<BDFSFile> listFiles = FileUtil.listFiles(root,
				task.getDir(), Long.parseLong(System.getProperty(
						Const.LOCALLIST_SESSION, "0")));
		if (listFiles != null) {
			excute(listFiles, task.getDir());
		}
	}

	protected void excute(List<BDFSFile> files, String dir) {

		checkExcludeAndAddTask(files);

		// 把最新本地结果放入数据库
		localFileDao.insertCache(files);

	}

	@Override
	protected void doTask(Task task) {
		getDirFiles((GetLocalFileTask) task);
	}

	/**
	 * 检查并排除目录
	 * 
	 * @param files
	 */
	private void checkExcludeAndAddTask(List<BDFSFile> files) {

		final List<BDFSFile> deletes = new ArrayList<BDFSFile>();
		for (BDFSFile f : files) {
			if (f.isDirectory()) {
				if (exclude.rmExclude(f.getAbsolutePath())) {
					deletes.add(f);
				} else {
					taskContainer.addTask(new GetLocalFileTask(f
							.getAbsolutePath()));
				}

			}
		}
		files.removeAll(deletes);
	}
}
