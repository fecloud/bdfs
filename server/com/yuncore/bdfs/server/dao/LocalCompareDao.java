package com.yuncore.bdfs.server.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.yuncore.bdfs.server.Const;
import com.yuncore.bdfs.server.util.Stopwatch;

public class LocalCompareDao extends BaseDao {

	Logger logger = Logger.getLogger(LocalCompareDao.class.getSimpleName());

	@Override
	public String getTableName() {
		return "localcompare";
	}

	public String getCopyTableName() {
		return "localfile";
	}

	public String getSameTableName() {
		return "localcomparesame";
	}

	/**
	 * 本地被删除表
	 * 
	 * @return
	 */
	public String getDeleteTableName() {
		return "localdelete";
	}

	/**
	 * 本地要上传表
	 * 
	 * @return
	 */
	public String getActionTableName() {
		return "localupload";
	}

	public String getSession() {
		return Const.LOCALLIST_SESSION;
	}

	protected String getTag() {
		return this.getClass().getSimpleName();
	}

	/**
	 * 删除表
	 * 
	 * @param tables
	 * @return
	 */
	public synchronized boolean clearTables() {
		return clearTables(getTableName(), getSameTableName());
	}

	/**
	 * 删除表
	 * 
	 * @param tables
	 * @return
	 */
	public synchronized boolean clearTables(String... tables) {

		boolean result = false;
		if (null != tables) {
			for (String t : tables) {
				result = executeSQL(String.format("DROP TABLE IF EXISTS %s", t));
				if (!result) {
					return result;
				}
			}
		}
		return true;
	}

	/**
	 * 从本地找重复的数据
	 * 
	 * @return
	 */
	public synchronized boolean copyFromLocaFile() {

		Stopwatch stopwatch = new Stopwatch();
		stopwatch.start();

		boolean result = executeSQL(String.format("DROP TABLE IF EXISTS %s",
				getTableName()));

		result = executeSQL(String.format(
				"CREATE TABLE %s AS SELECT * FROM %s", getTableName(),
				getCopyTableName()));

		stopwatch.stop(getTag() + " copyFromLocaFile");

		return result;
	}

	public synchronized boolean deleteAll(List<String> files) {

		if (null != files && !files.isEmpty()) {

			try {
				final Connection connection = getDB();
				final StringBuilder sql = new StringBuilder(String.format(
						"DELETE FROM %s WHERE fid IN( ", getTableName()));

				final int size = files.size();
				for (int i = 0; i < size; i++) {
					sql.append("'").append(files.get(i)).append("'");
					if (i < size - 1)
						sql.append(",");
				}
				sql.append(" )");

				final PreparedStatement prepareStatement = connection
						.prepareStatement(sql.toString());
				// Stopwatch stopwatch = new Stopwatch();
				// stopwatch.start();

				connection.setAutoCommit(false);
				final int executeBatch = prepareStatement.executeUpdate();
				// logger.debug(String.format("delete count:%s", executeBatch));
				connection.commit();
				connection.setAutoCommit(true);
				connection.close();

				// stopwatch.stop(getTag() + " deleteAll");

				return executeBatch > 0;

			} catch (SQLException e) {
				logger.error("", e);
			}

		}
		return false;
	}

	/**
	 * 从本地找重复的数据,复到localcomparesame表
	 * 
	 * @return
	 */
	public synchronized boolean findSame() {
		Stopwatch stopwatch = new Stopwatch();
		stopwatch.start();

		boolean result = executeSQL(String.format("DROP TABLE IF EXISTS %s",
				getSameTableName()));

		result = executeSQL(String
				.format("CREATE TABLE %s AS SELECT fid FROM %s group by fid having count(1) > 1",
						getSameTableName(), getCopyTableName()));
		stopwatch.stop(getTag() + " findSame");

		return result;
	}

	/**
	 * 取相同表的数据删除localcompare表中相同的数据
	 * 
	 * @return
	 */
	public synchronized boolean deleteSame() {

		long start = 0;
		int count = 2000;
		List<String> sames = null;
		Stopwatch stopwatch = new Stopwatch();
		stopwatch.start();
		while (true) {

			sames = getLocalCompareSame(start, count);

			if (sames != null && !sames.isEmpty()) {
				if (deleteAll(sames)) {
					start += count;
				} else {
					return false;
				}

			} else {
				stopwatch.stop(getTag() + " deleteSame");
				return true;
			}

		}
	}

	public synchronized boolean deleteBefore() {
		try {
			final Connection connection = getDB();
			final StringBuilder sql = new StringBuilder(String.format(
					"DELETE FROM %s WHERE session<>%s", getCopyTableName(),
					System.getProperty(getSession())));

			final PreparedStatement prepareStatement = getDB()
					.prepareStatement(sql.toString());
			Stopwatch stopwatch = new Stopwatch();
			stopwatch.start();

			connection.setAutoCommit(false);
			final int executeBatch = prepareStatement.executeUpdate();
			connection.commit();
			connection.setAutoCommit(true);
			connection.close();
			stopwatch.stop(getTag() + " deleteBefore");

			return executeBatch > 0;

		} catch (SQLException e) {
			logger.error("", e);
		}

		return false;

	}

	/**
	 * 取相同表的数据
	 * 
	 * @param start
	 * @param count
	 * @return
	 */
	public synchronized List<String> getLocalCompareSame(long start, long count) {
		try {
			final List<String> list = new ArrayList<String>();

			// Stopwatch stopwatch = new Stopwatch();
			// stopwatch.start();
			final String sql = String.format("SELECT fid FROM %s LIMIT %s,%s",
					getSameTableName(), start, count);
			final Connection connection = getDB();
			final PreparedStatement prepareStatement = connection
					.prepareStatement(sql);

			final ResultSet resultSet = prepareStatement.executeQuery();
			while (resultSet.next()) {
				list.add(buildLocalFile(resultSet));
			}
			// stopwatch.stop(getTag() + " getLocalCompareSame");
			resultSet.close();
			prepareStatement.close();
			connection.close();

			return list;
		} catch (SQLException e) {
			logger.error("", e);
		}

		return null;
	}

	public synchronized List<Long> groupBySession(String table) {
		final String sql = String.format(
				"SELECT session FROM %s GROUP BY session", table);
		try {
			final List<Long> list = new ArrayList<Long>();

			Stopwatch stopwatch = new Stopwatch();
			stopwatch.start();

			logger.debug(sql);
			final Connection connection = getDB();
			final PreparedStatement prepareStatement = connection
					.prepareStatement(sql);

			final ResultSet resultSet = prepareStatement.executeQuery();
			while (resultSet.next()) {
				logger.debug("groupBySession:" + resultSet.getLong("session"));
				list.add(resultSet.getLong("session"));
			}

			stopwatch.stop(getTag() + " groupBySession");
			resultSet.close();
			prepareStatement.close();
			connection.close();

			return list;

		} catch (SQLException e) {
			logger.error("", e);
		}
		return null;
	}

	/**
	 * 复制本地数据到上传或者下载表
	 * 
	 * @return
	 */
	public synchronized boolean copyTableData(String src, String dest) {
		return copyTableData(src, dest, null);
	}

	/**
	 * 复制表数据sql
	 * 
	 * @return
	 */
	protected String getCopyTableDataSql() {
		return "INSERT INTO %s SELECT id,path,length,isdir,mtime,fid,session FROM %s";
	}

	/**
	 * 复制本地数据到上传或者下载表
	 * 
	 * @return
	 */
	public synchronized boolean copyTableData(String src, String dest,
			String srcWhere) {
		String sql = String.format(getCopyTableDataSql(), dest, src);
		if (null != srcWhere) {
			sql += String.format(" WHERE ", srcWhere);
		}
		try {

			Stopwatch stopwatch = new Stopwatch();
			stopwatch.start();

			logger.debug(sql);
			final Connection connection = getDB();
			final PreparedStatement prepareStatement = connection
					.prepareStatement(sql);

			connection.setAutoCommit(false);
			final boolean result = prepareStatement.execute();

			prepareStatement.close();
			connection.commit();
			connection.setAutoCommit(true);
			connection.close();
			stopwatch.stop(getTag() + " copyAllToAction");

			return result;

		} catch (SQLException e) {
			logger.error("", e);
		}
		return false;
	}

	protected static String buildLocalFile(ResultSet resultSet)
			throws SQLException {
		final String fid = resultSet.getString("fid");
		return fid;
	}

}
