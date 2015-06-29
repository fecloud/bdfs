package com.yuncore.bdfs.client.api;

import java.util.Map;

import com.yuncore.bdfs.client.util.DownloadInputStream;
import com.yuncore.bdfs.entity.BDFSFile;
import com.yuncore.bdfs.exception.ApiException;

public interface FSApi {

	/**
	 * 登录
	 * 
	 * @param username
	 * @param password
	 * @return
	 */
	public boolean login(String username, String password) throws ApiException;

	/**
	 * 检测是否登录
	 * 
	 * @return
	 */
	public boolean islogin() throws ApiException;

	/**
	 * 下载文件
	 * 
	 * @param file
	 * @return
	 */
	public DownloadInputStream download(BDFSFile file) throws ApiException;

	/**
	 * 下载文件(断点)
	 * 
	 * @param file
	 * @return
	 */
	public DownloadInputStream download(BDFSFile file, long range)
			throws ApiException;

	/**
	 * 到page 取参数
	 * 
	 * @return
	 */
	public Map<String, String> diskHomePage() throws ApiException;

}