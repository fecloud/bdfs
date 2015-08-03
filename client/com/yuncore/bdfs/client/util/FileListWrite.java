package com.yuncore.bdfs.client.util;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

import com.yuncore.bdfs.entity.BDFSFile;

public class FileListWrite extends FileOutputStream {

	private ByteArrayOutputStream caches;

	private int size;

	private static final int PAGE_SIZE = 5000;

	public FileListWrite(String file, boolean append) throws IOException {
		super(file, append);
	}

	public synchronized boolean insertAllCacahe(List<BDFSFile> files) {
		if (size < PAGE_SIZE) {
			return addAll(files);
		} else {
			addAll(files);
			return insertAllCacaheFlush();
		}
	}

	private synchronized boolean addAll(List<BDFSFile> files) {
		if (null == caches) {
			caches = new ByteArrayOutputStream();
		}
		final ByteBuffer buffer = ByteBuffer.allocate(1024 * 5);
		byte[] bs = null;
		for (BDFSFile f : files) {
			buffer.clear();
			bs = f.getPath().getBytes();
			buffer.putShort((short) bs.length).put(bs)
					.putLong(f.getLength())
					.put((byte) (f.isDir() ? 0x1 : 0x0))
					.putInt((int) f.getMtime());
			buffer.flip();
			caches.write(buffer.array(), 0, buffer.limit());
		}
		size += files.size();
		return true;
	}

	public synchronized boolean insertAllCacaheFlush() {
		final boolean result = insertAll();
		return result;
	}

	public synchronized boolean insertAll() {
		
		if(null == caches){
			return true;
		}
		try {
			final ByteBuffer buffer = ByteBuffer.allocate(10);
			// size
			buffer.clear();
			buffer.putInt(caches.size());
			buffer.flip();

			write(buffer.array(), 0, buffer.limit());
			write(caches.toByteArray());
			flush();
			caches = null;
			size = 0;
			System.gc();
			return true;
		} catch (IOException e) {
			Log.e("FileListWrite", "", e);
		}
		return false;
	}

}
