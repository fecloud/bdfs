package com.yuncore.bdfs.client;

import java.util.ArrayList;
import java.util.List;

import com.yuncore.bdfs.client.api.ServerApi;
import com.yuncore.bdfs.client.api.imple.ServerApiImple;
import com.yuncore.bdfs.client.local.GetLocalFile;
import com.yuncore.bdfs.client.util.Log;
import com.yuncore.bdfs.client.util.Stopwatch;
import com.yuncore.bdfs.util.FileGzip;

public class UploadLocalFileList extends Thread {

	private List<String> excludeFiles = new ArrayList<String>();

	static final String TAG = "UploadLocalFileList";

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

			} catch (Exception e) {
				Log.e(TAG, "", e);
			}
			try {
				Thread.sleep(60000);
			} catch (InterruptedException e) {
				Log.e(TAG, "", e);
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
		final String zipname = listFiles.getOutFilename() + ".gzip";
		final FileGzip fileZip = new FileGzip(listFiles.getOutFilename(), zipname);
		final boolean fileZipResult = fileZip.gzip();
		stopwatch.stop("UploadLocalFileList FileZip");
		if (fileZipResult) {
			stopwatch.start();
			final ServerApi serverApi = new ServerApiImple();
			int i = 3;
			while (i > 0) {
				if (serverApi.uploadlocal(zipname)) {
					break;
				} else {
					Log.w(TAG, "upload "+ zipname + " fail");
				}
				i--;
			}
			stopwatch.stop("UploadLocalFileList ServerApi");
		} else {
			Log.i(TAG, "FileZip " + zipname + " fail");
		}

	}
}
