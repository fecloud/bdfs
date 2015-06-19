/**
 * 
 */
package com.yuncore.bdfs.tools;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.apache.log4j.Logger;

/**
 * @author ouyangfeng
 * 
 */
public class FileRM {

	Logger logger = Logger.getLogger(FileRM.class.getSimpleName());

	private String path;

	public FileRM() {
	}

	public FileRM(String path) {
		super();
		this.path = path;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * 删除
	 * 
	 * @return
	 */
	public void rm() {

		try {
			final File file = new File(path);
			if (file.exists()) {
				logger.warn("delete file "+ path);
				if (file.isFile()) {
					file.delete();
				} else {
					deleFiles(path);
				}
			}
		} catch (Exception e) {
			logger.error("rm error", e);
		}
	}

	private final void  deleFiles(String dir) {

		Stack<File> stack = new Stack<File>();
		stack.push(new File(dir));

		// 查找文件并删除
		List<File> dirs = searchDIR(dir, stack);
		while (!dirs.isEmpty()) {
			final List<File> tmp = new ArrayList<File>();
			for (File d : dirs) {
				tmp.addAll(searchDIR(d.getAbsolutePath(), stack));
			}
			dirs = tmp;
		}

		// 删除目录
		File file = null;
		while (!stack.isEmpty()) {
			file = stack.pop();
			logger.debug("delete dir:" + file.getAbsolutePath());
			file.delete();
		}
	}

	private final List<File> searchDIR(String dir, Stack<File> stack) {
		final File file = new File(dir);
		final List<File> dirs = new ArrayList<File>();
		if (file.isDirectory()) {

			final File[] listFiles = file.listFiles();
			if (listFiles != null) {
				for (File f : listFiles) {
					if (f.exists() && f.isDirectory()) {
						dirs.add(f);
						stack.push(f);
					} else {
						logger.debug("delete file:" + f.getAbsolutePath());
						f.delete();
					}
				}
			}
		}
		return dirs;
	}

}
