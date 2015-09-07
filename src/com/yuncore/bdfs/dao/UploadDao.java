package com.yuncore.bdfs.dao;

public class UploadDao extends DownloadDao{

	@Override
	public String getTableName() {
		return "localupload";
	}
	
}
