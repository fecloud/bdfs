/**
 * 
 */
package com.yuncore.bdfs.dao;

import com.yuncore.bdfs.Const;
import com.yuncore.bdfs.Environment;

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
		return Environment.CLOUDLIST_SESSION;
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
		return "INSERT INTO %s SELECT id,path,length,isdir,mtime,fid,md5,session FROM %s";
	}

}
