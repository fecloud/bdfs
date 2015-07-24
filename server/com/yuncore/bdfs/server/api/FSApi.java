package com.yuncore.bdfs.server.api;

import java.util.Map;

import com.yuncore.bdfs.exception.ApiException;
import com.yuncore.bdfs.server.entity.CloudPageFile;

public interface FSApi {

	/**
	 * 列表当前目录的文件(包含文件夹)
	 * {"errno":-9,"request_id":8897598895336496977} //目录不存在
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
	 * 到page 取参数
	 * 
	 * @return
	 */
	public Map<String, String> diskHomePage() throws ApiException;
}
