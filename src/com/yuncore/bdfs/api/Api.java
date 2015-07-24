package com.yuncore.bdfs.api;

import java.util.Map;

import com.yuncore.bdfs.entity.BDFSFile;
import com.yuncore.bdfs.entity.CloudFile;
import com.yuncore.bdfs.entity.CloudMkDirResult;
import com.yuncore.bdfs.entity.CloudPageFile;
import com.yuncore.bdfs.entity.CloudQuota;
import com.yuncore.bdfs.entity.CloudRmResult;
import com.yuncore.bdfs.exception.ApiException;
import com.yuncore.bdfs.tools.DownloadInputStream;

public interface Api {

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
	 * 登出
	 * 
	 * @return
	 */
	public boolean logout() throws ApiException;

	/**
	 * 改变目录
	 * 
	 * @param dir
	 * @return
	 */
	public boolean chdir(String dir);

	/**
	 * 返回当前所在的目录
	 * 
	 * @return
	 */
	public String pwddir();

	/**
	 * 列表当前目录的文件(包含文件夹)
	 * 
	 * @param dir
	 * @return
	 */
	public CloudPageFile list(String dir) throws ApiException;

	/**
	 * 列表当前目录的文件(包含文件夹)
	 * 
	 * @param dir
	 * @return
	 */
	public CloudPageFile list(String dir, int page) throws ApiException;

	/**
	 * 列表当前目录的文件(包含文件夹)
	 * 
	 * @param dir
	 * @return
	 */
	public CloudPageFile list(String dir, int page, int page_num)
			throws ApiException;

	/**
	 * 建立文件夹
	 * 
	 * @param dir
	 * @return
	 */
	public CloudMkDirResult mkdir(String dir) throws ApiException;

	/**
	 * 删除文件或文件夹
	 * 
	 * @param filename
	 * @return
	 */
	public CloudRmResult rm(String... filename) throws ApiException;

	/**
	 * 空间使用情况
	 * 
	 * @return
	 */
	public CloudQuota quota() throws ApiException;

	/**
	 * 当前用户
	 * 
	 * @return
	 */
	public String who() throws ApiException;

	/**
	 * 到page 取参数
	 * 
	 * @return
	 */
	public Map<String, String> diskHomePage() throws ApiException;

	/**
	 * 下载文件
	 * 
	 * @param filename
	 * @param file
	 * @return
	 */
	// public boolean download(String filename, PCSDownloadFile file);

	/**
	 * 下载文件
	 * 
	 * @param file
	 * @return
	 */
	public DownloadInputStream download(CloudFile file) throws ApiException;
	/**
	 * 下载文件(断点)
	 * 
	 * @param file
	 * @return
	 */
	public DownloadInputStream download(CloudFile file,long range) throws ApiException;

	/**
	 * 搜索文件
	 * 
	 * @param filename
	 * @return
	 */
	public CloudPageFile search(String filename) throws ApiException;

	/**
	 * 搜索文件
	 * 
	 * @param filename
	 * @return
	 */
	public CloudPageFile searchAll(String keyword) throws ApiException;

	/**
	 * 查看回收站里面的文件
	 * 
	 * @return
	 */
	public CloudPageFile recyclebin(int page, int page_num) throws ApiException;

	/**
	 * 查看回收站里面的文件
	 * 
	 * @return
	 */
	public CloudPageFile recyclebin(int page) throws ApiException;

	/**
	 * 查看回收站里面的文件
	 * 
	 * @return
	 */
	public CloudPageFile recyclebin() throws ApiException;

	/**
	 * 文件或者目录是否存在
	 * 
	 * @param path
	 * @return
	 */
	public BDFSFile fileExists(String path) throws ApiException;

	/**
	 * 上传文件
	 * 
	 * @param file
	 * @param 服务器文件夹
	 * @return
	 */
	public String upload(String file, String dir) throws ApiException;

}
