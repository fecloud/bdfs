package com.yuncore.bdfs.client.down;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import com.yuncore.bdfs.client.api.FSApi;
import com.yuncore.bdfs.client.api.ServerApi;
import com.yuncore.bdfs.client.api.imple.FSApiImple;
import com.yuncore.bdfs.client.api.imple.ServerApiImple;
import com.yuncore.bdfs.client.util.BDFSFileExclude;
import com.yuncore.bdfs.client.util.DownloadInputStream;
import com.yuncore.bdfs.client.util.FileMV;
import com.yuncore.bdfs.client.util.Log;
import com.yuncore.bdfs.entity.BDFSFile;
import com.yuncore.bdfs.exception.ServerApiException;

public class CloudDownLoad extends Thread {

	static final String TAG = "CloudDownLoad";

	private volatile boolean falg;

	private BDFSFileExclude exclude;

	private ServerApi serverApi;

	private FSApi api;

	private String tmpDir;

	private String root;

	public CloudDownLoad(String root, String tmpDir) {
		this.root = root;
		this.tmpDir = tmpDir;
		serverApi = new ServerApiImple();
		api = new FSApiImple();

		final File file = new File(tmpDir);
		if (!file.exists()) {
			file.mkdirs();
		}
		exclude = new CloudDownFileExclude();
	}

	/**
	 * 添加要过滤的目录或者文件
	 * 
	 * @param file
	 */
	public synchronized void addExclude(List<String> files) {
		final List<String> list = new ArrayList<String>();
		String filename = null;
		for (String f : files) {
			filename = "/" + f;
			if (!list.contains(filename)) {
				list.add(filename);
			}
		}
		exclude.addExclude(list);
	}

	@Override
	public void run() {
		setName(CloudDownLoad.class.getSimpleName());
		Log.i(TAG,
				String.format("CloudDownLoad root:%s tmpDir:%s", root, tmpDir));
		falg = true;
		BDFSFile cloudFile = null;
		boolean downloaded = true;
		while (falg) {
			cloudFile = getDownLoad();
			if (cloudFile != null) {
				Log.i(TAG, "getDownLoad " + cloudFile.getAbsolutePath());
				// 检查是否是排除下载的目录
				if (isExclude(cloudFile)) {
					downloaded = true;
				} else {
					downloaded = downloadFile(cloudFile);
				}
				// 删除下载任务
				if (downloaded) {
					delDownLoad(cloudFile);
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

	private BDFSFile getDownLoad() {
		try {
			return serverApi.getDownload();
		} catch (ServerApiException e) {
			Log.e(TAG, "getDownLoad error", e);
		}
		return null;
	}

	private void delDownLoad(BDFSFile cloudFile) {
		try {
			if (serverApi.delDownload(cloudFile.getId())) {
				Log.i(TAG, "delDownLoad " + cloudFile.getAbsolutePath());
			}
		} catch (ServerApiException e) {
			Log.e(TAG, "delDownload error", e);
		}
	}

	/**
	 * 返回flase下载文件
	 * 
	 * @param cloudFile
	 * @return
	 */
	private boolean checkFile(BDFSFile cloudFile) {
		final String file = root + cloudFile.getAbsolutePath();
		final File targetFile = new File(file);
		if (targetFile.exists()) {
			if (targetFile.isFile()) {
				if (cloudFile.isFile()
						&& cloudFile.getLength() == targetFile.length()) {
					Log.i(TAG, "file local exists");
					return true;
				} else {
					Log.i(TAG, "file local exists but length not equals");
					targetFile.delete();
					return false;
				}
			} else if (targetFile.isDirectory()) {
				if (cloudFile.isDirectory()) {
					return true;
				}
			}
		}

		return false;
	}

	private boolean downloadFile(BDFSFile cloudFile) {
		final boolean checkResult = checkFile(cloudFile);
		final String file = root + cloudFile.getAbsolutePath();
		if (!checkResult) {
			if (cloudFile.isDirectory()) {
				return new File(file).mkdirs();
			} else if (cloudFile.isFile()) {
				for (int i = 0; i < 10; i++) {
					if (downloadFileContext(cloudFile)) {
						return true;
					}
				}
				return false;
			} else {
				return true;
			}
		} else {
			delDownLoad(cloudFile);
			return true;
		}
	}

	/**
	 * 下载文件内容
	 * 
	 * @param cloudFile
	 * @return
	 */
	@SuppressWarnings("resource")
	private boolean downloadFileContext(BDFSFile cloudFile) {
		Log.i(TAG, "downloadFileContext " + cloudFile.getAbsolutePath());
		final String tmpFile = tmpDir + File.separator + cloudFile.getfId();
		final String file = root + cloudFile.getAbsolutePath();
		boolean reslut = false;
		long sum = 0;
		try {
			long fileStart = checkTempFile(tmpFile);

			DownloadInputStream in = null;
			FileOutputStream out = null;
			if (fileStart > 0) {
				Log.i(TAG, "continue download file start:" + fileStart);
				sum = fileStart;
				in = api.download(cloudFile, fileStart);
				out = new FileOutputStream(tmpFile, true);
			} else {
				Log.i(TAG, "new download file start:0");
				in = api.download(cloudFile);
				out = new FileOutputStream(tmpFile);
			}

			if (in != null && in.getLength() == -1) {
				// 文件被删除了,可能之前有临时文件 删除
				Log.w(TAG, "cloudfile is delete can not down");
				final File file2 = new File(tmpFile);
				file2.delete();
				in.close();
				return true;
			}

			if (in != null) {
				final byte[] buffer = new byte[1024 * 1024];
				int len = -1;

				while (-1 != (len = in.read(buffer))) {
					out.write(buffer, 0, len);
					sum += len;
				}
				out.flush();
				out.close();
				if (sum == cloudFile.getLength()) {
					if (new FileMV(tmpFile, file).mv()) {
						reslut = true;
					}
				}
				in.close();
			}
		} catch (Exception e) {
			Log.e(TAG, "downloadFileContext error", e);
		}
		return reslut;
	}

	/**
	 * 检查临时文件
	 * 
	 * @param file
	 * @return
	 */
	private long checkTempFile(String file) {
		final File f = new File(file);
		if (f.exists()) {
			return f.length();
		} else {
			return -1;
		}
	}

	/**
	 * 检查并排除目录
	 * 
	 * @param files
	 */
	private boolean isExclude(BDFSFile f) {
		if (exclude.rmExclude(f.getAbsolutePath())) {
			return true;
		}
		return false;
	}

}
