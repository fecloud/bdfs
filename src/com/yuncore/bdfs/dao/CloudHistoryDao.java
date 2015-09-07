package com.yuncore.bdfs.dao;

public class CloudHistoryDao extends LocalHistoryDao {

	@Override
	public String getTableName() {
		return "cloudhistory";
	}
	
}
