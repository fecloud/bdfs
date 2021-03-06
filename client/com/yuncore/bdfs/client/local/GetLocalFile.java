package com.yuncore.bdfs.client.local;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.yuncore.bdfs.client.ClientEnv;
import com.yuncore.bdfs.client.Const;
import com.yuncore.bdfs.client.util.BDFSFileExclude;
import com.yuncore.bdfs.client.util.FileListWrite;
import com.yuncore.bdfs.client.util.Log;
import com.yuncore.bdfs.task.TaskExecute;
import com.yuncore.bdfs.task.TaskService;

public class GetLocalFile extends TaskService {

	static final String TAG = "GetLocalFile";

	private File dir;

	private BDFSFileExclude exclude;

	private String outFilename = System.getProperty("java.io.tmpdir")
			+ File.separator + "localfiles";

	private FileListWrite fileListWrite;

	public GetLocalFile(int threads, String dir) {
		this.threads = threads;
		exclude = new BDFSFileExclude();
		this.dir = new File(dir);

		try {
			final File file = new File(outFilename);
			if (!file.exists()) {
				file.getParentFile().mkdirs();
			}
			fileListWrite = new FileListWrite(outFilename, false);
		} catch (IOException e) {
			Log.e(TAG, "", e);
		}
	}

	public String getOutFilename() {
		return outFilename;
	}

	@Override
	protected TaskExecute newTaskExecute() {
		final GetLocalFileExecute getLocalExecute = new GetLocalFileExecute(
				dir.getAbsolutePath(), taskStatus, taskContainer, exclude,
				fileListWrite);
		return getLocalExecute;
	}

	public synchronized boolean list() {
		if (dir.exists()) {
			final long session = System.currentTimeMillis();
			System.setProperty(Const.LOCALLIST_SESSION, "" + session);
			taskContainer.addTask(new GetLocalFileTask(""));
			ClientEnv.setProperty(ClientEnv.key_localfilelist_last, session);
			waitTaskFinish();
			fileListWrite.insertAllCacaheFlush();
			try {
				fileListWrite.flush();
				fileListWrite.close();
			} catch (IOException e) {
				Log.e(TAG, "", e);
			}
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
