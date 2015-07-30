package com.yuncore.bdfs.server.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.yuncore.bdfs.server.db.DBHelper;

public abstract class BaseDao {

	Logger logger = Logger.getLogger(BaseDao.class.getSimpleName());

	private static final DBHelper db = new DBHelper();

	protected synchronized Connection getDB() {
		return db.getConnection();
	}

	protected synchronized boolean executeSQL(String sql) {
		logger.debug("executeSQL:" + sql);
		return db.executeSQL(sql);
	}

	/**
	 * 清除表所有数据
	 * 
	 * @return
	 */
	public synchronized boolean clear() {
		boolean result = false;
		try {
			final Connection connection = getDB();
			final PreparedStatement prepareStatement = connection
					.prepareStatement(String.format("DELETE FROM %s",
							getTableName()));

			connection.setAutoCommit(false);
			result = prepareStatement.execute();
			connection.commit();
			connection.setAutoCommit(true);
			connection.close();
			
		} catch (SQLException e) {
			logger.error("clear", e);
		}

		return result;
	}
	
	/**
	 * 查询表数据条数
	 * @return
	 */
	public synchronized long count(){
		long count = 0l;
		try {
			final Connection connection = getDB();
			final PreparedStatement prepareStatement = connection
					.prepareStatement(String.format("SELECT COUNT(*) FROM %s", getTableName()));
			final ResultSet resultSet = prepareStatement.executeQuery();
			if(null != resultSet && resultSet.next()){
				count = resultSet.getLong(1);
				resultSet.close();
			}
			prepareStatement.close();
			connection.close();
		} catch (SQLException e) {
			logger.error("count", e);
		}
		
		return count;
	}

	public abstract String getTableName();

}
