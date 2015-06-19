package com.yuncore.bdfs.tools;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.yuncore.bdfs.entity.LocalFile;

public class FileListWrite extends FileWriter {

	Logger logger = Logger.getLogger(FileListWrite.class.getSimpleName());

	private List<LocalFile> caches = new ArrayList<LocalFile>();

	public FileListWrite(String file, boolean append) throws IOException {
		super(file, append);
	}

	public synchronized boolean insertAllCacahe(List<LocalFile> files) {
		if (caches.size() < 10000) {
			return caches.addAll(files);
		} else {
			caches.addAll(files);
			return insertAllCacaheFlush();
		}
	}

	public synchronized boolean insertAllCacaheFlush() {
		final boolean result = insertAll(caches);
		caches.clear();
		return result;
	}

	public synchronized boolean insertAll(List<LocalFile> files) {
		final StringBuilder builder = new StringBuilder();
		for (LocalFile f : files) {
			builder.append(f.getDir()).append("|");
			builder.append(f.getName()).append("|");
			builder.append(f.getLength()).append("|");
			builder.append(f.getType()).append("|");
			builder.append(f.getfId()).append("|");
			builder.append(f.getSession());
			builder.append(System.getProperty("line.separator"));
		}
		if (builder.length() > 0) {
			try {
				write(builder.toString());
				flush();
			} catch (IOException e) {
				logger.error("", e);
			}
		}
		return false;
	}

	@Override
	protected void finalize() throws Throwable {
		this.close();
		super.finalize();
	}

}
