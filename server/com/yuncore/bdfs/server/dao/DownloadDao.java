package com.yuncore.bdfs.server.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.yuncore.bdfs.entity.BDFSFile;

public class DownloadDao extends BaseDao {

	Logger logger = Logger.getLogger(DownloadDao.class.getSimpleName());

	@Override
	public String getTableName() {
		return "clouddownload";
	}

	public boolean insert(BDFSFile file) {
		try {
			final Connection connection = getDB();

			final PreparedStatement prepareStatement = connection
					.prepareStatement(String.format(
							"INSERT INTO %s (file) VALUES(%s)", getTableName(),
							file));
			connection.setAutoCommit(false);
			int result = prepareStatement.executeUpdate();
			connection.commit();
			connection.setAutoCommit(true);
			connection.close();

			return result > 0;
		} catch (SQLException e) {
			logger.error("insert error", e);
		}
		return false;
	}

	public boolean delete(String id) {
		try {
			final Connection connection = getDB();
			final PreparedStatement prepareStatement = connection
					.prepareStatement(String.format(
							"DELETE FROM %s WHERE id=?", getTableName()));
			prepareStatement.setString(1, id);
			connection.setAutoCommit(false);
			int result = prepareStatement.executeUpdate();
			connection.commit();
			connection.setAutoCommit(true);
			connection.close();

			return result > 0;
		} catch (SQLException e) {
			logger.error("delete error", e);
		}
		return false;
	}

	public boolean delete(BDFSFile file) {
		try {
			final Connection connection = getDB();
			final PreparedStatement prepareStatement = connection
					.prepareStatement(String.format(
							"DELETE FROM %s WHERE id=?", getTableName()));
			prepareStatement.setString(1, file.getId());
			connection.setAutoCommit(false);
			int result = prepareStatement.executeUpdate();
			connection.commit();
			connection.setAutoCommit(true);
			connection.close();

			return result > 0;
		} catch (SQLException e) {
			logger.error("delete error", e);
		}
		return false;
	}

	public BDFSFile query() {
		List<BDFSFile> query = query(1);
		if (null != query && !query.isEmpty()) {
			return query.get(0);
		} else {
			return null;
		}
	}

	public List<BDFSFile> query(int num) {

		try {
			final List<BDFSFile> list = new ArrayList<BDFSFile>();
			final String sql = String.format("SELECT * FROM %s LIMIT 0,%s",
					getTableName(), num);

			final Connection connection = getDB();
			final PreparedStatement prepareStatement = connection
					.prepareStatement(sql);
			final ResultSet resultSet = prepareStatement.executeQuery();
			BDFSFile file = null;
			while (resultSet.next()) {
				file = new BDFSFile();
				file.setId(resultSet.getString("id"));
				file.setfId(resultSet.getString("fid"));
				file.setDir(resultSet.getString("dir"));
				file.setName(resultSet.getString("name"));
				file.setType(resultSet.getbo("type"));
				file.setSession(resultSet.getLong("session"));
				file.setLength(resultSet.getLong("length"));
				list.add(file);
			}
			resultSet.close();
			prepareStatement.close();
			connection.close();

			return list;
		} catch (SQLException e) {
			logger.error("query error", e);
		}
		return null;
	}

}
