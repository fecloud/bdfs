package com.yuncore.bdfs.client.upload;

import java.io.File;

import com.yuncore.bdfs.client.ClientEnv;
import com.yuncore.bdfs.client.api.FSApi;
import com.yuncore.bdfs.client.api.ServerApi;
import com.yuncore.bdfs.client.api.imple.FSApiImple;
import com.yuncore.bdfs.client.api.imple.ServerApiImple;
import com.yuncore.bdfs.client.entity.MkDirResult;
import com.yuncore.bdfs.client.util.FileUtil;
import com.yuncore.bdfs.client.util.Log;
import com.yuncore.bdfs.entity.BDFSFile;
import com.yuncore.bdfs.entity.CloudFile;
import com.yuncore.bdfs.exception.ApiException;
import com.yuncore.bdfs.exception.ServerApiException;
import com.yuncore.bdfs.http.HttpFormOutput.OutputDataListener;

public class LocalUpload extends Thread implements OutputDataListener {

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
		Log.i(TAG, String.format("LocalUpload root:%s tmpDir:%s", root, tmpDir));
		falg = true;

		BDFSFile file = null;
		boolean upload = true;
		while (falg) {
			file = getUpload();
			if (file != null) {
				Log.i(TAG, "getUpload " + file.getAbsolutePath() + " size:"
						+ FileUtil.byteSizeToHuman(file.getLength()));
				ClientEnv.setProperty(ClientEnv.key_uploading, file);
				upload = uploadFile(file);
				if (upload) {
					ClientEnv.setProperty(ClientEnv.key_uploading, "");
					delUpload(file);
				}
			} else {
				try {
					Thread.sleep(30000);
					ClientEnv.setProperty(ClientEnv.key_uploading, false);
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

	/**
	 * 上传文件
	 * 
	 * @param file
	 * @return
	 * @throws ApiException 
	 */
	private boolean uploadFile(BDFSFile file) {
		try{
			ClientEnv.setProperty(ClientEnv.key_upload_size, 0);
			if (checkBDFSFile(file)) {
				Log.w(TAG, "file too big ,not upload");
				return true;
			}
			if (!checkLocalFile(file)) {// 本地文件不在了,直接删除任务
				Log.w(TAG, "local file " + file.getAbsolutePath() + " is deleted");
				return true;
			}
			if (fileExists(file)) {
				return true;
			}
			
			//
			if (file.isDirectory()) {
				return mkdirCloud(file);
			} else if (file.isFile()) {
				return uploadFileContext(file);
			}
		} catch (ApiException e){
			Log.e(TAG, "uploadFile" ,e);
		}
		return false;
	}

	/**
	 * 检查要上传的文件大小,http超过1G上传不鸟
	 * 
	 * @param file
	 * @return
	 */
	private boolean checkBDFSFile(BDFSFile file) {
		if (file.getLength() >= 1024 * 1024 * 1024) {
			return true;
		}
		return false;
	}

	/**
	 * 检查本地文件是否还在
	 * 
	 * @param file
	 * @return
	 */
	private boolean checkLocalFile(BDFSFile file) {
		final String localpath = String.format("%s/%s", root,
				file.getAbsolutePath());
		final File localFile = new File(localpath);
		if (localFile.exists()) {
			return true;
		}
		return false;
	}

	/**
	 * 上传文件正文
	 * 
	 * @param file
	 * @return
	 */
	private boolean uploadFileContext(BDFSFile file) {
		final long fileLen = file.getLength();
		// 判断是否大小分块上传的单块数,可以用秒传试一下
		if (fileLen > FSApi.RAPIDUPLOAD) {
			Log.d(TAG, "try secondFileContext");
			if (secondFileContext(file)) {
				Log.d(TAG, "secondFileContext ok");
				return true;
			} else {
				return norMalFileContext(file);
			}
		} else {
			return norMalFileContext(file);
		}
	}

	/**
	 * 以普通的form上传文件(不可以断点的)
	 * 
	 * @param file
	 * @return
	 */
	private boolean norMalFileContext(BDFSFile file) {
		Log.d(TAG, "norMalFileContext");
		try {
			final String localpath = String.format("%s/%s", root,
					file.getAbsolutePath());
			final String cloudpath = file.getAbsolutePath();
			return api.upload2(localpath, cloudpath, this);
		} catch (ApiException e) {
			Log.e(TAG,
					String.format("uploadFileContext file:%s error",
							file.getAbsolutePath()), e);
			ClientEnv.setProperty(ClientEnv.key_upload_size, 0);
		}
		return false;
	}

	/**
	 * 秒传文件的方式
	 * 
	 * @param file
	 * @return
	 */
	private boolean secondFileContext(BDFSFile file) {
		try {
			final String localpath = String.format("%s/%s", root,
					file.getAbsolutePath());
			final String cloudpath = file.getAbsolutePath();
			return api.secondUpload(localpath, cloudpath);
		} catch (ApiException e) {
			Log.e(TAG,
					String.format("secondFileContext file:%s error",
							file.getAbsolutePath()), e);
		}
		return false;
	}

	/**
	 * 检查文件在云端是否存在
	 * 如果文件存在,长度跟本地不一样,删了
	 * @param file
	 * @return
	 * @throws ApiException 
	 */
	private boolean fileExists(BDFSFile file) throws ApiException {
		final CloudFile fileExists = api.fileExists(file.getAbsolutePath());
		if (fileExists != null) {
			Log.d(TAG, String.format("%s exists cloud", file.getAbsolutePath()));
			// 两个都是文件
			if (file.isFile() && fileExists.isFile()) {
				// 两个文件长度一样
				if (file.getLength() == fileExists.getLength()) {
					Log.d(TAG,
							String.format("%s exists cloud len equal",
									file.getAbsolutePath()));
					return true;
				} else {
					// 把文件删了
					api.rm(file.getAbsolutePath());
				}
			} else if (file.isDir() && fileExists.isDir()) {
				Log.d(TAG,
						String.format("%s exists cloud isdir",
								file.getAbsolutePath()));
				return true;
			}
		} else {
			Log.d(TAG, String.format("%s not exists cloud",
					file.getAbsolutePath()));
		}

		return false;
	}

	/**
	 * 在云端创建目录
	 * 
	 * @param file
	 * @return
	 */
	private boolean mkdirCloud(BDFSFile file) {
		try {

			final MkDirResult mkdir = api.mkdir(file.getAbsolutePath());
			if (null != mkdir) {
				Log.d(TAG, "mkir " + file.getAbsolutePath());
				if (mkdir.getStatus() == 0) {
					return true;
				}
				Log.w(TAG, "mkdirCloud " + file.getAbsolutePath() + " error:"
						+ mkdir.getStatus());
			}
		} catch (ApiException e) {
			Log.e(TAG, "mkdirCloud error", e);
		}
		return false;
	}

	@Override
	public void onWrite(long sum, long commit) {
		ClientEnv.setProperty(ClientEnv.key_upload_size, commit);
	}
}
