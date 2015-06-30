/**
 * 
 */
package com.yuncore.bdfs.server.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.yuncore.bdfs.server.util.Stopwatch;

/**
 * @author ouyangfeng
 * 
 */
public class CookieDao extends BaseDao {

	public CookieDao() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yuncore.bdfs.db.BaseDao#getTableName()
	 */
	@Override
	public String getTableName() {
		return "cookie";
	}

	/**
	 * 保存cookie
	 * 
	 * @param cookie
	 * @return
	 */
	public synchronized boolean saveCookie(String cookie) {
		try {
			 Stopwatch stopwatch = new Stopwatch();
			 stopwatch.start();
			final Connection connection = getDB();
			final PreparedStatement prepareStatement = connection
					.prepareStatement(String.format(
							"REPLACE INTO %s (id,cookie) VALUES (1,?)",
							getTableName()));

			prepareStatement.setString(1, cookie);

			connection.setAutoCommit(false);

			final boolean result = prepareStatement.executeUpdate() > 0;
			prepareStatement.close();

			connection.commit();
			connection.setAutoCommit(true);
			connection.close();
			 stopwatch.stop("CookieDao saveCookie");
			return result;
		} catch (SQLException e) {
			logger.error("", e);
		}
		return false;
	}

	/**
	 * 取cookie
	 * 
	 * @return
	 */
	public synchronized String getCookie() {
		try {
			 Stopwatch stopwatch = new Stopwatch();
			 stopwatch.start();
			final Connection connection = getDB();
			final PreparedStatement prepareStatement = connection
					.prepareStatement(String.format(
							"SELECT id,cookie FROM %s LIMIT 0,1",
							getTableName()));

			final ResultSet resultSet = prepareStatement.executeQuery();
			String result = null;
			if (resultSet.next()) {
				result = resultSet.getString("cookie");
			}
			prepareStatement.close();
			connection.close();
			 stopwatch.stop("CookieDao getCookie");
			return result;
		} catch (SQLException e) {
			logger.error("", e);
		}
		return null;
	}

}
