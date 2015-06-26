package com.yuncore.bdfs.server.dao;

public class UploadDao extends DownloadDao{

	@Override
	public String getTableName() {
		return "localupload";
	}
	
}
