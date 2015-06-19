package com.yuncore.bdfs.client;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.yuncore.bdfs.api.ServerApi;
import com.yuncore.bdfs.api.imple.ServerApiImple;
import com.yuncore.bdfs.sync.local.GetLocalFile;
import com.yuncore.bdfs.tools.FileZip;
import com.yuncore.bdfs.tools.Stopwatch;

public class UploadLocalFileList extends Thread {

	private List<String> excludeFiles = new ArrayList<String>();

	Logger logger = Logger.getLogger(UploadLocalFileList.class.getSimpleName());

	public UploadLocalFileList(List<String> excludeFiles) {
		super();
		this.excludeFiles = excludeFiles;
	}

	@Override
	public void run() {

		setName(UploadLocalFileList.class.getSimpleName());

		while (true) {
			try {
				work();
				// break;
				Thread.sleep(100000);
			} catch (Exception e) {
				logger.error("", e);
			}
		}

	}

	private void work() throws Exception {
		Stopwatch stopwatch = new Stopwatch();
		stopwatch.start();
		final GetLocalFile listFiles = new GetLocalFile(4,
				System.getProperty("syncdir"));
		listFiles.addExclude(excludeFiles);
		listFiles.list();
		stopwatch.stop("GetLocalFile");

		stopwatch.start();
		final String zipname = listFiles.getOutFilename() + ".zip";
		final FileZip fileZip = new FileZip(listFiles.getOutFilename(), zipname);
		final boolean fileZipResult = fileZip.zip();
		stopwatch.stop("UploadLocalFileList FileZip");
		if (fileZipResult) {
			stopwatch.start();
			final ServerApi serverApi = new ServerApiImple();
			int i = 3;
			while (i > 0) {
				if (serverApi.uploadlocal(zipname)) {
					break;
				} else {
					logger.warn("Uploadlocal " + zipname + " fail");
				}
				i--;
			}
			stopwatch.stop("UploadLocalFileList ServerApi");
		} else {
			logger.info("FileZip " + zipname + " fail");
		}

	}
}
