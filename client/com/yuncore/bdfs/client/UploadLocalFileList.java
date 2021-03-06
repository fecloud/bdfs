package com.yuncore.bdfs.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.yuncore.bdfs.client.api.ServerApi;
import com.yuncore.bdfs.client.api.imple.ServerApiImple;
import com.yuncore.bdfs.client.local.GetLocalFile;
import com.yuncore.bdfs.client.util.Log;
import com.yuncore.bdfs.client.util.Stopwatch;
import com.yuncore.bdfs.exception.ServerApiException;
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
				ClientEnv.setProperty(ClientEnv.key_localfilelist, "sleep");
				Thread.sleep(60 * 1000 * 10);
			} catch (InterruptedException e) {
				Log.e(TAG, "", e);
			}
		}

	}

	private void work() throws IOException {
		Stopwatch stopwatch = new Stopwatch();
		stopwatch.start();
		final GetLocalFile listFiles = new GetLocalFile(4,
				System.getProperty("syncdir"));
		listFiles.addExclude(excludeFiles);
		ClientEnv.setProperty(ClientEnv.key_localfilelist, "listing");
		listFiles.list();
		stopwatch.stop("GetLocalFile");

		stopwatch.start();
		final String zipname = listFiles.getOutFilename() + ".gzip";
		final FileGzip fileZip = new FileGzip(listFiles.getOutFilename(),
				zipname);
		ClientEnv.setProperty(ClientEnv.key_localfilelist, "gziping");
		final boolean fileZipResult = fileZip.gzip();
		stopwatch.stop("UploadLocalFileList FileZip");
		if (fileZipResult) {
			stopwatch.start();
			final ServerApi serverApi = new ServerApiImple();
			int i = 3;
			while (i > 0) {
				boolean upload = false;
				try {
					// 上传到服务器
					ClientEnv.setProperty(ClientEnv.key_localfilelist,
							"uploading");
					upload = serverApi.uploadlocal(zipname);
				} catch (ServerApiException e) {
				}
				if (upload) {
					break;
				} else {
					Log.w(TAG, "upload " + zipname + " fail");
				}
				i--;
			}
			stopwatch.stop("UploadLocalFileList ServerApi");
		} else {
			Log.i(TAG, "FileZip " + zipname + " fail");
		}

	}
}
