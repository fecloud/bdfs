package com.yuncore.bdfs.server.dao;

public class CloudHistoryDao extends LocalHistoryDao {

	@Override
	public String getTableName() {
		return "cloudhistory";
	}
	
}
