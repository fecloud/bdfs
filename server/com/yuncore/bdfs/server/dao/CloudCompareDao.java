/**
 * 
 */
package com.yuncore.bdfs.server.dao;

/**
 * @author ouyangfeng
 * 
 */
public class CloudCompareDao extends LocalCompareDao {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yuncore.dbpcs.db.LocalCompareDao#getTableName()
	 */
	@Override
	public String getTableName() {
		return "cloudcompare";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yuncore.dbpcs.db.LocalCompareDao#getCopyTableName()
	 */
	@Override
	public String getCopyTableName() {
		return "cloudfile";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yuncore.dbpcs.db.LocalCompareDao#getSameTableName()
	 */
	@Override
	public String getSameTableName() {
		return "cloudcomparesame";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yuncore.dbpcs.db.LocalCompareDao#getActionTableName()
	 */
	@Override
	public String getActionTableName() {
		return "clouddownload";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yuncore.dbpcs.db.LocalCompareDao#getDeleteTableName()
	 */
	@Override
	public String getDeleteTableName() {
		return "clouddelete";
	}

	@Override
	public String getSession() {
		return "cloudlist_session";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yuncore.bdfs.db.LocalCompareDao#getTag()
	 */
	@Override
	protected String getTag() {
		return this.getClass().getSimpleName();
	}
	
	@Override
	protected String getCopyTableDataSql() {
		return "INSERT INTO %s SELECT id,dir,name,length,type,fid,md5,session FROM %s";
	}

}
