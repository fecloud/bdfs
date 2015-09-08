package com.yuncore.bdfs.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.yuncore.bdfs.db.DBHelper;
import com.yuncore.bdfs.util.Log;

public abstract class BaseDao {

	static String TAG = "BaseDao";

	private static final DBHelper db = new DBHelper();

	/**
	 * 取数据库连接
	 * 
	 * @return
	 */
	protected synchronized Connection getConnection() {
		return db.getConnection();
	}

	/**
	 * 执行sql语句
	 * 
	 * @param sql
	 * @return
	 */
	public synchronized boolean executeSQL(String sql) {
		Log.d(TAG, "executeSQL:" + sql);
		return db.executeSQL(sql);
	}

	/**
	 * 清除表所有数据
	 * 
	 * @return
	 */
	public synchronized boolean clear() {
		return executeSQL(String.format("DELETE FROM %s", getTableName()));
	}

	/**
	 * 查询表数据条数
	 * 
	 * @return
	 */
	public synchronized long count() {
		long count = 0l;
		try {
			final Connection connection = getConnection();
			final PreparedStatement prepareStatement = connection
					.prepareStatement(String.format("SELECT COUNT(*) FROM %s", getTableName()));
			final ResultSet resultSet = prepareStatement.executeQuery();
			if (null != resultSet && resultSet.next()) {
				count = resultSet.getLong(1);
				resultSet.close();
			}
			prepareStatement.close();
			connection.close();
		} catch (SQLException e) {
			Log.e(TAG, "count", e);
		}

		return count;
	}

	/**
	 * 删除表
	 * 
	 * @param table
	 * @return
	 */
	public synchronized boolean delete(String table) {
		return executeSQL(String.format("DROP TABLE IF EXISTS %s", table));
	}

	/**
	 * 删除表
	 * 
	 * @param tables
	 * @return
	 */
	public synchronized boolean deletes(String... tables) {

		boolean result = false;
		if (null != tables) {
			for (String t : tables) {
				result = delete(t);
				if (!result) {
					return result;
				}
			}
		}
		return true;
	}

	public abstract String getTableName();

}
