package com.yuncore.bdfs.server.files.local;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import org.apache.log4j.Logger;

import com.yuncore.bdfs.entity.BDFSFile;
import com.yuncore.bdfs.server.Const;
import com.yuncore.bdfs.server.compare.LocalCompare;
import com.yuncore.bdfs.server.dao.LocalFileDao;
import com.yuncore.bdfs.server.dao.LocalHistoryDao;
import com.yuncore.bdfs.server.util.Stopwatch;
import com.yuncore.bdfs.task.Task;
import com.yuncore.bdfs.task.TaskContainer;
import com.yuncore.bdfs.task.TaskExecute;
import com.yuncore.bdfs.task.TaskStatus;

public class UploadLocalFileExecute extends TaskExecute {

	Logger logger = Logger.getLogger(UploadLocalFileExecute.class
			.getSimpleName());

	private long session = System.currentTimeMillis();

	private LocalFileDao localFileDao;

	public UploadLocalFileExecute(TaskStatus taskStatus,
			TaskContainer taskContainer, LocalFileDao localFileDao) {
		super(taskStatus, taskContainer);
		this.localFileDao = localFileDao;
	}

	@Override
	protected void doTask(Task task) {
		final UploadLocalFileTask uploadLocalFileTask = (UploadLocalFileTask) task;
		setLocalfileSession();
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
			final FileInputStream in = new FileInputStream(filename);
			final FileChannel fileChannel = in.getChannel();
			final ByteBuffer buffer = ByteBuffer.allocate(4);
			while (fileChannel.read(buffer) == 4) {
				buffer.flip();
				pareOnce(fileChannel, buffer.getInt());
			}
			in.close();
			localFileDao.insertAllCacaheFlush();
			stopwatch.stop("readToDB " + filename);
			return true;
		} catch (Exception e) {
			logger.error("readToDB", e);
		}
		return false;
	}

	private void pareOnce(FileChannel fileChannel, int length)
			throws IOException {
		final ByteBuffer buffer = ByteBuffer.allocate(length);
		if (fileChannel.read(buffer) == length) {
			buffer.flip();
			short len = 0;
			BDFSFile file = null;
			while (buffer.hasRemaining()) {
				file = new BDFSFile();
				len = buffer.getShort();
				file.setPath(new String(buffer.array(), buffer.position(), len));
				buffer.position(buffer.position() + len);
				file.setLength(buffer.getLong());
				file.setDir(buffer.get() == 0x1 ? true : false);
				localFileDao.insertCache(file);
			}
		}
	}

	private void setLocalfileSession() {
		System.setProperty(Const.LOCALLIST_SESSION, "" + session);
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
