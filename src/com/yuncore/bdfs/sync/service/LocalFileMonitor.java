/**
 * @(#) LocalFileMonitor.java Created on 2015年9月8日
 *
 * 
 */
package com.yuncore.bdfs.sync.service;

import java.util.HashSet;
import java.util.Set;

import com.yuncore.bdfs.Argsment;
import com.yuncore.bdfs.Environment;
import com.yuncore.bdfs.compare.LocalCompare;
import com.yuncore.bdfs.local.GetLocalFile;
import com.yuncore.bdfs.util.Log;

/**
 * The class <code>LocalFileMonitor</code>
 * 
 * @author Feng OuYang
 * @version 1.0
 */
public class LocalFileMonitor extends Thread {

	private static final String TAG = "LocalFileMonitor";

	private volatile boolean flag;

	protected Set<String> excludeFiles = new HashSet<String>();

	public LocalFileMonitor(String[] args) {
		addExcludeFiles(args);
	}

	protected String getTag() {
		return TAG;
	}

	public String getExcludeFilesFlag() {
		return "-l";
	}

	private void addExcludeFiles(String[] args) {
		excludeFiles.add("tmp");
		if (args.length > 3) {
			boolean startAdd = false;
			for (int i = 2; i < args.length; i++) {
				if (args[i].equals(getExcludeFilesFlag())) {
					startAdd = true;
				} else if (args[i].startsWith("-")) {
					break;
				} else if (startAdd) {
					excludeFiles.add(args[i]);
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		setName(getTag());
		super.run();
		while (flag) {
			work();
			try {
				Thread.sleep(getSleep());
			} catch (InterruptedException e) {
				Log.e(getTag(), "", e);
			}
		}
	}

	/**
	 * 下一次的间隔时间
	 * 
	 * @return
	 */
	protected long getSleep() {
		return Argsment.getLocalFileMonitorInterval();
	}

	/**
	 * 读取最新的文件
	 * 
	 * @return
	 */
	protected boolean listNewFiles() {
		final GetLocalFile getLocalFile = new GetLocalFile(4, Environment.getSyncDir());
		getLocalFile.addExclude(excludeFiles);
		return getLocalFile.list();
	}

	/**
	 * 与之前的文件进行对比
	 * 
	 * @return
	 */
	protected boolean compareFiles() {
		final LocalCompare localCompare = new LocalCompare();
		return localCompare.compare();
	}

	/**
	 * 
	 */
	protected void work() {
		if (listNewFiles()) {
			Log.w(getTag(), "GetFiles success");
			if (compareFiles()) {
				Log.w(getTag(), "Compare success");
			} else {
				Log.w(getTag(), "Compare success");
			}
		} else {
			Log.w(getTag(), "GetFiles fail");
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#start()
	 */
	@Override
	public synchronized void start() {
		this.flag = true;
		super.start();
	}

	public synchronized void shutdown() {
		this.flag = false;
	}

}
