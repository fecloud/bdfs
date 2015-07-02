package com.yuncore.bdfs.client.util;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.yuncore.bdfs.entity.BDFSFile;

public class FileListWrite extends FileOutputStream {

	private List<BDFSFile> caches = new ArrayList<BDFSFile>();
	
	private static final int PAGE_SIZE = 5000;

	public FileListWrite(String file, boolean append) throws IOException {
		super(file, append);
	}

	public synchronized boolean insertAllCacahe(List<BDFSFile> files) {
		if (caches.size() < PAGE_SIZE) {
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
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		final ByteBuffer buffer = ByteBuffer.allocate(2048);
		byte[] bs = null;
		for (BDFSFile f : files) {
			buffer.clear();
			bs = f.getPath().getBytes();
			buffer.putShort((short) bs.length).put(bs).putLong(f.getLength()).put(f.getType());
			buffer.flip();
			out.write(buffer.array(), 0, buffer.limit());
		}
		try {
			//size
			buffer.clear();
			buffer.putInt(out.size());
			buffer.flip();
			
			write(buffer.array(), 0, buffer.limit());
			write(out.toByteArray());
			flush();
			out.close();
		} catch (IOException e) {
			Log.e("FileListWrite", "", e);
		}
		return false;
	}

}
