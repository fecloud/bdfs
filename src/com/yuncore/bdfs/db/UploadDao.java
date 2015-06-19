package com.yuncore.bdfs.db;

public class UploadDao extends DownloadDao{

	@Override
	public String getTableName() {
		return "localupload";
	}
	
}
