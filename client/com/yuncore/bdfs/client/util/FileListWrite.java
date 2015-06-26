package com.yuncore.bdfs.client.util;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.yuncore.bdfs.entity.BDFSFile;

public class FileListWrite extends FileWriter {

	private List<BDFSFile> caches = new ArrayList<BDFSFile>();

	public FileListWrite(String file, boolean append) throws IOException {
		super(file, append);
	}

	public synchronized boolean insertAllCacahe(List<BDFSFile> files) {
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

	public synchronized boolean insertAll(List<BDFSFile> files) {
		final StringBuilder builder = new StringBuilder();
		for (BDFSFile f : files) {
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
				Log.e("FileListWrite", "insertAll", e);
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
