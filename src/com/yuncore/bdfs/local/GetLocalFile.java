package com.yuncore.bdfs.local;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import com.yuncore.bdfs.ClientEnv;
import com.yuncore.bdfs.Environment;
import com.yuncore.bdfs.dao.LocalFileDao;
import com.yuncore.bdfs.task.TaskExecute;
import com.yuncore.bdfs.task.TaskService;
import com.yuncore.bdfs.util.BDFSFileExclude;

public class GetLocalFile extends TaskService {

	static final String TAG = "GetLocalFile";

	private File dir;

	private BDFSFileExclude exclude;

	private LocalFileDao localFileDao;

	private long session;

	public GetLocalFile(int threads, String dir) {
		this.threads = threads;
		exclude = new BDFSFileExclude();
		this.dir = new File(dir);
		localFileDao = new LocalFileDao("localfile_tmp");

		localFileDao.delete(localFileDao.getTableName());
		localFileDao
				.executeSQL(String
						.format("CREATE TABLE %s (id INTEGER PRIMARY KEY AUTOINCREMENT,path TEXT, length INTEGER, isdir INTEGER, mtime INTEGER, fid TEXT, session INTEGER);",
								localFileDao.getTableName()));
	}

	@Override
	protected TaskExecute newTaskExecute() {
		final GetLocalFileExecute getLocalExecute = new GetLocalFileExecute(
				dir.getAbsolutePath(), taskStatus, taskContainer, exclude,
				localFileDao, session);
		return getLocalExecute;
	}

	public synchronized boolean list() {
		if (dir.exists()) {
			session = System.currentTimeMillis();
			Environment.setLocallistSession("" + session);
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
	public synchronized void addExclude(Set<String> files) {
		final Set<String> list = new HashSet<String>();
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
