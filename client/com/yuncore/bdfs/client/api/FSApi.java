package com.yuncore.bdfs.client.api;

import java.util.Map;

import com.yuncore.bdfs.client.entity.MkDirResult;
import com.yuncore.bdfs.client.util.DownloadInputStream;
import com.yuncore.bdfs.entity.BDFSFile;
import com.yuncore.bdfs.entity.CloudFile;
import com.yuncore.bdfs.exception.ApiException;

public interface FSApi {

	/**
	 * 分块上传块大小
	 */
	int RAPIDUPLOAD = (256 * 1024);
	
	/**
	 * bdstoken
	 */
	String BDSTOKEN = "MYBDSTOKEN";
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
	
	/**
	 * 上传文件
	 * @param filename 本地文件路径
	 * @param dir 服务器路径
	 * @return
	 * @throws ApiException
	 */
	public boolean upload(String localpath, String cloudpath)throws ApiException;
	
	/**
	 * 秒传
	 * @param filename 本地文件路径
	 * @param dir 服务器路径
	 * @return
	 * @throws ApiException
	 */
	public boolean secondUpload(String filename,String dir)throws ApiException;
	
	/**
	 * 
	 * @param dir
	 * @return
	 * @throws ApiException
	 */
	public MkDirResult mkdir(String dir) throws ApiException ;
	
	/**
	 * 文件或者目录是否存在
	 * 
	 * @param file
	 * @return
	 */
	public CloudFile fileExists(String file) throws ApiException;

}
