package com.yuncore.bdfs.server.localflle;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;

import com.yuncore.bdfs.db.LocalFileDao;
import com.yuncore.bdfs.db.LocalHistoryDao;
import com.yuncore.bdfs.entity.LocalFile;
import com.yuncore.bdfs.sync.compare.LocalCompare;
import com.yuncore.bdfs.sync.local.GetLocalFile;
import com.yuncore.bdfs.task.Task;
import com.yuncore.bdfs.task.TaskContainer;
import com.yuncore.bdfs.task.TaskExecute;
import com.yuncore.bdfs.task.TaskStatus;
import com.yuncore.bdfs.tools.Stopwatch;

public class UploadLocalFileExecute extends TaskExecute {

	Logger logger = Logger.getLogger(UploadLocalFileExecute.class
			.getSimpleName());

	private LocalFileDao localFileDao;

	public UploadLocalFileExecute(TaskStatus taskStatus,
			TaskContainer taskContainer, LocalFileDao localFileDao) {
		super(taskStatus, taskContainer);
		this.localFileDao = localFileDao;
	}

	@Override
	protected void doTask(Task task) {
		final UploadLocalFileTask uploadLocalFileTask = (UploadLocalFileTask) task;
		if (readToDB(uploadLocalFileTask.getFilename())) {
			new LocalHistoryDao().insert();
			if (compare()) {
				final File file = new File(uploadLocalFileTask.getFilename());
				file.delete();
			}
		} else {
			logger.info("readToDB fail");
		}

	}

	/**
	 * 读取文件入库
	 * 
	 * @param filename
	 * @return
	 */
	private boolean readToDB(String filename) {
		try {
			Stopwatch stopwatch = new Stopwatch();
			stopwatch.start();
			final BufferedReader reader = new BufferedReader(
					new InputStreamReader(new FileInputStream(filename)));
			String line = null;
			boolean firsetLine = true;
			LocalFile localFile = null;
			while (null != (line = reader.readLine())) {
				localFile = parseLine(line);
				if (null == localFile)
					break;

				if (firsetLine) {
					setLocalfileSession(localFile.getSession());
					firsetLine = false;
				}

				localFileDao.insertCache(localFile);

			}
			reader.close();
			localFileDao.insertAllCacaheFlush();
			stopwatch.stop("readToDB " + filename);
			return true;
		} catch (Exception e) {
			logger.error("readToDB", e);
		}
		return false;
	}

	private static LocalFile parseLine(String line) {
		if (null != line && line.trim().length() > 0) {
			final String[] strings = line.trim().split("\\|");
			if (null != strings && strings.length == 6) {
				final LocalFile file = new LocalFile();
				file.setDir(strings[0]);
				file.setName(strings[1]);
				file.setLength(Long.parseLong(strings[2]));
				file.setType(Integer.parseInt(strings[3]));
				file.setfId(strings[4]);
				file.setSession(Long.parseLong(strings[5]));
				return file;
			}
		}
		return null;
	}

	private void setLocalfileSession(long session) {
		System.setProperty(GetLocalFile.LOCALLIST_SESSION, "" + session);
	}

	private boolean compare() {
		Stopwatch stopwatch = new Stopwatch();
		stopwatch.start();
		final LocalCompare localCompare = new LocalCompare();
		final boolean result = localCompare.compare();
		stopwatch.stop("UploadLocalFileExecute compare");
		return result;
	}
}
