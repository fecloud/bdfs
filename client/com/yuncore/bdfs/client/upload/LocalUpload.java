package com.yuncore.bdfs.client.upload;

import java.io.File;

import com.yuncore.bdfs.client.api.FSApi;
import com.yuncore.bdfs.client.api.ServerApi;
import com.yuncore.bdfs.client.api.imple.FSApiImple;
import com.yuncore.bdfs.client.api.imple.ServerApiImple;
import com.yuncore.bdfs.client.util.Log;
import com.yuncore.bdfs.entity.BDFSFile;
import com.yuncore.bdfs.exception.ServerApiException;

public class LocalUpload extends Thread {

	static final String TAG = "LocalUpload";

	protected volatile boolean falg;

	protected ServerApi serverApi;

	protected FSApi api;

	protected String tmpDir;

	protected String root;

	public LocalUpload(String root, String tmpDir) {
		this.root = root;
		this.tmpDir = tmpDir;
		serverApi = new ServerApiImple();
		api = new FSApiImple();

		final File file = new File(tmpDir);
		if (!file.exists()) {
			file.mkdirs();
		}
	}

	@Override
	public void run() {
		setName(LocalUpload.class.getSimpleName());
		Log.i(TAG,
				String.format("CloudDownLoad root:%s tmpDir:%s", root, tmpDir));
		falg = true;

		BDFSFile file;
		boolean upload = true;
		while (falg) {
			file = getUpload();
			if (file != null) {
				Log.i(TAG, "getUpload " + file.getAbsolutePath());
				// downloaded = downloadFile(cloudFile);
				if (upload) {
					delUpload(file);
				}
			} else {
				try {
					Thread.sleep(30000);
				} catch (InterruptedException e) {
					break;
				}
			}
		}
	}

	private BDFSFile getUpload() {
		try {
			return serverApi.getUpload();
		} catch (ServerApiException e) {
			Log.e(TAG, "getUpload error", e);
		}
		return null;
	}

	private void delUpload(BDFSFile file) {
		try {
			if (serverApi.delUpload(file.getId())) {
				Log.i(TAG, "delUpload " + file.getAbsolutePath());
			}
		} catch (ServerApiException e) {
			Log.e(TAG, "delUpload error", e);
		}
	}

}
