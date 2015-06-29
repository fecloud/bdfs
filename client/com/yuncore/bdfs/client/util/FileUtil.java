package com.yuncore.bdfs.client.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.yuncore.bdfs.entity.BDFSFile;

public class FileUtil {

	/**
	 * 读取目录
	 * 
	 * @param dir
	 * @return
	 */
	public static List<BDFSFile> listFiles(String dir, long session) {
		final File file = new File(dir);
		if (file.exists() && file.isDirectory()) {
			File[] listFiles = file.listFiles();
			if (null != listFiles) {
				final List<BDFSFile> list = new ArrayList<BDFSFile>();
				BDFSFile localFile = null;
				for (File f : listFiles) {
					localFile = new BDFSFile();
					localFile.setDir(f.getParent());
					if (f.isFile()) {
						localFile.setLength(f.length());
					}
					localFile.setName(f.getName());
					localFile.setType(f.isFile() ? 0 : 1);
					localFile.setSession(session);
					localFile.setfId(localFile.toFid());
					list.add(localFile);
				}

				return list;
			}
		}
		return null;
	}

	public static boolean rmDirFile(String dir) {
		final File dirFile = new File(dir);
		if (dirFile.exists()) {
			final File[] listFiles = dirFile.listFiles();
			if (listFiles != null) {
				for (File f : listFiles) {
					if (!f.delete()) {
						return false;
					}
				}
			}
		}
		return false;
	}

}