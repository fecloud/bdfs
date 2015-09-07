package com.yuncore.bdfs.local;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.yuncore.bdfs.ClientEnv;
import com.yuncore.bdfs.Const;
import com.yuncore.bdfs.dao.LocalFileDao;
import com.yuncore.bdfs.task.TaskExecute;
import com.yuncore.bdfs.task.TaskService;
import com.yuncore.bdfs.util.BDFSFileExclude;

public class GetLocalFile extends TaskService {

	static final String TAG = "GetLocalFile";

	private File dir;

	private BDFSFileExclude exclude;

	private LocalFileDao localFileDao;

	public GetLocalFile(int threads, String dir) {
		this.threads = threads;
		exclude = new BDFSFileExclude();
		this.dir = new File(dir);
		this.localFileDao = new LocalFileDao();
	}

	@Override
	protected TaskExecute newTaskExecute() {
		final GetLocalFileExecute getLocalExecute = new GetLocalFileExecute(
				dir.getAbsolutePath(), taskStatus, taskContainer, exclude,
				localFileDao);
		return getLocalExecute;
	}

	public synchronized boolean list() {
		if (dir.exists()) {
			final long session = System.currentTimeMillis();
			System.setProperty(Const.LOCALLIST_SESSION, "" + session);
			taskContainer.addTask(new GetLocalFileTask(""));
			ClientEnv.setProperty(ClientEnv.key_localfilelist_last, session);
			waitTaskFinish();
			localFileDao.insertAllCacaheFlush();
			return true;
		}
		return false;
	}

	public synchronized boolean setList(String dir) {
		if (new File(dir).exists()) {
			this.dir = new File(dir);
			return true;
		}
		return false;
	}

	@Override
	protected String getTaskExecuteName() {
		return "GetLocalFile";
	}

	/**
	 * 添加要过滤的目录或者文件
	 * 
	 * @param file
	 */
	public synchronized void addExclude(List<String> files) {
		final List<String> list = new ArrayList<String>();
		String filename = null;
		for (String f : files) {
			filename = "/" + f;
			if (!list.contains(filename)) {
				list.add(filename);
			}
		}
		exclude.addExclude(list);
	}
}
