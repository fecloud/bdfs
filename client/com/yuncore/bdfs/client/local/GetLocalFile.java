package com.yuncore.bdfs.client.local;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.yuncore.bdfs.client.util.FileListWrite;
import com.yuncore.bdfs.task.TaskExecute;
import com.yuncore.bdfs.task.TaskService;

public class GetLocalFile extends TaskService {

	Logger logger = Logger.getLogger(GetLocalFile.class.getSimpleName());

	public static final String LOCALLIST_SESSION = "locallist_session";

	private File dir;

	private LocalFileExclude exclude;

	private String outFilename = System.getProperty("java.io.tmpdir")
			+ File.separator + "localfiles";

	private FileListWrite fileListWrite;

	public GetLocalFile(int threads, String dir) {
		this.threads = threads;
		exclude = new LocalFileExclude();
		this.dir = new File(dir);

		try {
			final File file = new File(outFilename);
			if (!file.exists()) {
				file.getParentFile().mkdirs();
			}
			fileListWrite = new FileListWrite(outFilename, false);
		} catch (IOException e) {
			logger.error("", e);
		}
	}

	public String getOutFilename() {
		return outFilename;
	}

	@Override
	protected TaskExecute newTaskExecute() {
		final GetLocalFileExecute GetLocalExecute = new GetLocalFileExecute(
				taskStatus, taskContainer, exclude, fileListWrite);
		return GetLocalExecute;
	}

	public synchronized boolean list() {
		if (dir.exists()) {
			System.setProperty(LOCALLIST_SESSION,
					"" + System.currentTimeMillis());
			taskContainer.addTask(new GetLocalFileTask(dir.getAbsolutePath()));
			waitTaskFinish();
			fileListWrite.insertAllCacaheFlush();
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
			filename = System.getProperty("syncdir") + File.separator + f;
			if (!list.contains(filename)) {
				list.add(filename);
			}
		}
		exclude.addExclude(list);
	}
}
