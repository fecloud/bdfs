package com.yuncore.bdfs.server.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.yuncore.bdfs.entity.History;

public class LocalHistoryDao extends BaseDao {

	Logger logger = Logger.getLogger(LocalHistoryDao.class.getSimpleName());

	@Override
	public String getTableName() {
		return "localhistory";
	}

	public synchronized boolean insert() {
		String sql = String.format("INSERT INTO %s (time) VALUES (?)",
				getTableName());
		try {
			final Connection connection = getDB();
			final PreparedStatement prepareStatement = connection
					.prepareStatement(sql);
			prepareStatement.setString(1, "" + new Date().getTime());
			connection.setAutoCommit(false);
			boolean execute = prepareStatement.execute();
			connection.commit();
			connection.setAutoCommit(true);
			connection.close();

			return execute;
		} catch (Exception e) {
			logger.error("", e);
		}
		return false;
	}

	public synchronized List<History> getHistory() {
		return getHistory(0);
	}

	public synchronized List<History> getHistory(int count) {

		String sql = String.format("SELECT id,time FROM %s ORDER by id DESC",
				getTableName());
		if (count > 0) {
			sql = String.format(
					"SELECT id,time FROM %s ORDER by id DESC LIMIT 0,%s",
					getTableName(), count);
		}

		final List<History> list = new ArrayList<History>();
		try {
			final Connection connection = getDB();
			final PreparedStatement prepareStatement = connection
					.prepareStatement(sql);
			final ResultSet resultSet = prepareStatement.executeQuery();
			History history = null;
			while (resultSet.next()) {
				history = new History();
				history.setTime(resultSet.getLong("time"));
				history.setId(resultSet.getLong("id"));
				list.add(history);
			}
			prepareStatement.close();
			connection.close();
			return list;
		} catch (SQLException e) {
			logger.error("", e);
		}
		return null;
	}
}
