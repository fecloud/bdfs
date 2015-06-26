package com.yuncore.bdfs.client.api;

import com.yuncore.bdfs.entity.BDFSFile;
import com.yuncore.bdfs.exception.ServerApiException;

public interface ServerApi {

	/**
	 * 上传本地文件列表
	 * 
	 * @param file
	 * @return
	 * @throws ServerApiException
	 */
	boolean uploadlocal(String file) throws ServerApiException;

	/**
	 * 上传cookie
	 * 
	 * @param file
	 * @return
	 * @throws ServerApiException
	 */
	boolean uploadCookie(String file) throws ServerApiException;

	/**
	 * 取一个本地下载的文件
	 * 
	 * @return
	 * @throws ServerApiException
	 */
	BDFSFile getDownload() throws ServerApiException;

	/**
	 * 取一个本地下载的文件
	 * 
	 * @param id
	 * @return
	 * @throws ServerApiException
	 */
	boolean deldownload(String id) throws ServerApiException;

}
