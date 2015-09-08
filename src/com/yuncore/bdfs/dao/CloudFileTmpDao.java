/**
 * @(#) CloudFileTmpDao.java Created on 2015年9月8日
 *
 * 
 */
package com.yuncore.bdfs.dao;

/**
 * The class <code>CloudFileTmpDao</code>
 * 
 * @author Feng OuYang
 * @version 1.0
 */
public class CloudFileTmpDao extends CloudFileDao {

	/* (non-Javadoc)
	 * @see com.yuncore.bdfs.dao.CloudFileDao#getTableName()
	 */
	@Override
	public String getTableName() {
		return "cloudfile_tmp";
	}
	
}
