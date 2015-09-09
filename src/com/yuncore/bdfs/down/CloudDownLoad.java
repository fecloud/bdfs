package com.yuncore.bdfs.down;

import java.io.File;
import java.io.FileOutputStream;

import com.yuncore.bdfs.Argsment;
import com.yuncore.bdfs.Environment;
import com.yuncore.bdfs.StatusMent;
import com.yuncore.bdfs.api.FSApi;
import com.yuncore.bdfs.api.imple.FSApiImple;
import com.yuncore.bdfs.dao.DownloadDao;
import com.yuncore.bdfs.entity.BDFSFile;
import com.yuncore.bdfs.util.DownloadInputStream;
import com.yuncore.bdfs.util.FileMV;
import com.yuncore.bdfs.util.Log;

public class CloudDownLoad extends Thread {

	static final String TAG = "CloudDownLoad";

	private String root;

	private String tmpDir;

	private FSApi api;

	private DownloadDao downloadDao;

	public CloudDownLoad(String root, String tmpDir) {
		this.root = root;
		this.tmpDir = tmpDir;
		api = new FSApiImple();
		downloadDao = new DownloadDao();

		final File file = new File(tmpDir);
		if (!file.exists()) {
			file.mkdirs();
		}
	}

	@Override
	public void run() {
		setName(CloudDownLoad.class.getSimpleName());
		Log.i(TAG, String.format("CloudDownLoad root:%s tmpDir:%s", root, tmpDir));
		BDFSFile cloudFile = null;
		boolean downloaded = true;
		while (true) {

			if (Argsment.getAllowDownload()) {

				cloudFile = getDownLoad();
				if (cloudFile != null) {
					Log.i(TAG, "getDownLoad " + cloudFile.getAbsolutePath());
					StatusMent.setProperty(StatusMent.key_downloading, cloudFile);
					StatusMent.setProperty(StatusMent.key_download_size, 0);
					// 检查是否是排除下载的目录
					downloaded = downloadFile(cloudFile);
					// 删除下载任务
					if (downloaded) {
						Environment.setProperty(Environment.key_downloading, "");
						delDownLoad(cloudFile);
					}
				} else {
					Log.w(TAG, "not found download task sleep");
					try {
						Thread.sleep(30000);
						StatusMent.setProperty(StatusMent.key_downloading, false);
					} catch (InterruptedException e) {
						break;
					}
				}
			} else {
				Log.w(TAG, "not allow download");
				while (!Argsment.getAllowDownload()) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
					}
				}
			}
		}
	}

	private BDFSFile getDownLoad() {
		return downloadDao.query();
	}

	private void delDownLoad(BDFSFile cloudFile) {
		if (downloadDao.deleteByFid(cloudFile.getfId())) {
			Log.i(TAG, "delDownLoad " + cloudFile.getAbsolutePath());
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
				if (cloudFile.isFile() && cloudFile.getLength() == targetFile.length()) {
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
	private boolean downloadFileContext(BDFSFile cloudFile) {
		Log.i(TAG, "downloadFileContext " + cloudFile.getAbsolutePath());
		final String tmpFile = tmpDir + File.separator + cloudFile.getfId();
		final String file = root + cloudFile.getAbsolutePath();
		boolean reslut = false;
		long sum = 0;
		try {
			long fileStart = checkTempFile(tmpFile);
			// 如果文件下载完成没有移动到目录路径
			if (fileStart == cloudFile.getLength()) {
				if (new FileMV(tmpFile, file).mv()) {
					return true;
				} else {
					fileStart = 0;
					// 重新下载
				}
			}

			StatusMent.setProperty(StatusMent.key_download_size, fileStart);
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
				out.close();
				return true;
			}

			if (in != null) {
				final byte[] buffer = new byte[1024 * 1024];
				int len = -1;

				while (-1 != (len = in.read(buffer))) {
					// ClientEnv.setProperty(ClientEnv.key_download_size, sum);
					out.write(buffer, 0, len);
					sum += len;
					if (sum == cloudFile.getLength()) {
						break;
					}
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

}
