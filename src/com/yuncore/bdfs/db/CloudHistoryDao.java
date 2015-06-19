package com.yuncore.bdfs.db;

public class CloudHistoryDao extends LocalHistoryDao {

	@Override
	public String getTableName() {
		return "cloudhistory";
	}
	
}
