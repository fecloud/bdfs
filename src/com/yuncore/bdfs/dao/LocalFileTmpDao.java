/**
 * @(#) LocalFileTmpDao.java Created on 2015年9月8日
 *
 * 
 */
package com.yuncore.bdfs.dao;

/**
 * The class <code>LocalFileTmpDao</code>
 * 
 * @author Feng OuYang
 * @version 1.0
 */
public class LocalFileTmpDao extends LocalFileDao {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yuncore.bdfs.dao.LocalFileDao#getTableName()
	 */
	@Override
	public String getTableName() {
		return "localfile_tmp";
	}
}
