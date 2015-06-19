package com.yuncore.bdfs.sync.down;

import java.io.File;
import java.io.FileOutputStream;

import org.apache.log4j.Logger;

import com.yuncore.bdfs.api.Api;
import com.yuncore.bdfs.api.ServerApi;
import com.yuncore.bdfs.api.imple.ApiImple;
import com.yuncore.bdfs.api.imple.ServerApiImple;
import com.yuncore.bdfs.entity.CloudFile;
import com.yuncore.bdfs.exception.ServerApiException;
import com.yuncore.bdfs.tools.DownloadInputStream;
import com.yuncore.bdfs.tools.FileMV;

public class CloudDownLoad extends Thread {

	Logger logger = Logger.getLogger(CloudDownLoad.class.getSimpleName());

	private volatile boolean falg;

	private ServerApi serverApi;

	private Api api;

	private String tmpDir;

	private String root;

	public CloudDownLoad(String root, String tmpDir) {
		this.root = root;
		this.tmpDir = tmpDir;
		serverApi = new ServerApiImple();
		api = new ApiImple();

		final File file = new File(tmpDir);
		if (!file.exists()) {
			file.mkdirs();
		}

	}

	@Override
	public void run() {
		logger.info(String.format("CloudDownLoad root:%s tmpDir:%s", root,
				tmpDir));
		falg = true;
		CloudFile cloudFile;
		boolean downloaded = false;
		while (falg) {
			cloudFile = getDownLoad();
			if (cloudFile != null) {
				downloaded = downloadFile(cloudFile);
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

	private CloudFile getDownLoad() {
		try {
			return serverApi.getDownload();
		} catch (ServerApiException e) {
			logger.error("CloudDownLoad getDownLoad error", e);
		}
		return null;
	}

	private void delDownLoad(CloudFile cloudFile) {
		try {
			if (serverApi.deldownload(cloudFile.getId())) {
				logger.info("delDownLoad " + cloudFile.getAbsolutePath());
			}
		} catch (ServerApiException e) {
			logger.error("CloudDownLoad getDownLoad error", e);
		}
	}

	/**
	 * 返回flase下载文件
	 * 
	 * @param cloudFile
	 * @return
	 */
	private boolean checkFile(CloudFile cloudFile) {
		final String file = root + cloudFile.getAbsolutePath();
		final File targetFile = new File(file);
		if (targetFile.exists()) {
			if (targetFile.isFile()) {
				if (cloudFile.isFile()
						&& cloudFile.getLength() == targetFile.length()) {
					return true;
				} else {
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

	private boolean downloadFile(CloudFile cloudFile) {
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
	private boolean downloadFileContext(CloudFile cloudFile) {
		logger.info("downloadFileContext " + cloudFile.getAbsolutePath());
		final String tmpFile = tmpDir + File.separator + cloudFile.getfId();
		final String file = root + cloudFile.getAbsolutePath();
		boolean reslut = false;
		long sum = 0;
		try {
			long fileStart = checkTempFile(tmpFile);

			DownloadInputStream in = null;
			FileOutputStream out = null;
			if (fileStart > 0) {
				logger.info("continue download file start:" + fileStart);
				sum = fileStart;
				in = api.download(cloudFile, fileStart);
				out = new FileOutputStream(tmpFile,true);
			} else {
				logger.info("new download file start:0");
				in = api.download(cloudFile);
				out = new FileOutputStream(tmpFile);
			}

			if (in != null && in.getLength() == -1) {
				// 文件被删除了,可能之前有临时文件 删除
				logger.warn("cloudfile is delete can not down");
				final File file2 = new File(tmpFile);
				file2.delete();
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
			logger.error("downloadFileContext error", e);
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
