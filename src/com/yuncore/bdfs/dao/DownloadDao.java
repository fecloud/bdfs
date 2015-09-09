package com.yuncore.bdfs.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.yuncore.bdfs.entity.BDFSFile;
import com.yuncore.bdfs.util.Log;

public class DownloadDao extends BaseDao {

	static String TAG = "DownloadDao";

	@Override
	public String getTableName() {
		return "clouddownload";
	}

	public boolean insert(BDFSFile file) {
		try {
			final Connection connection = getConnection();

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
			Log.e(TAG, "insert error", e);
		}
		return false;
	}

	public boolean delete(String id) {
		try {
			final Connection connection = getConnection();
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
			Log.e(TAG, "delete error", e);
		}
		return false;
	}
	
	public boolean deleteByFid(String fid) {
		try {
			final Connection connection = getConnection();
			final PreparedStatement prepareStatement = connection
					.prepareStatement(String.format(
							"DELETE FROM %s WHERE fid=?", getTableName()));
			prepareStatement.setString(1, fid);
			connection.setAutoCommit(false);
			int result = prepareStatement.executeUpdate();
			connection.commit();
			connection.setAutoCommit(true);
			connection.close();

			return result > 0;
		} catch (SQLException e) {
			Log.e(TAG, "delete error", e);
		}
		return false;
	}

	public boolean delete(BDFSFile file) {
		try {
			final Connection connection = getConnection();
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
			Log.e(TAG, "delete error", e);
		}
		return false;
	}

	public BDFSFile query() {
		List<BDFSFile> query = query(0l, 1);
		if (null != query && !query.isEmpty()) {
			return query.get(0);
		} else {
			return null;
		}
	}

	public List<BDFSFile> query(long start, int num) {

		try {
//			final Stopwatch stopwatch = new Stopwatch();
//			stopwatch.start();
			final List<BDFSFile> list = new ArrayList<BDFSFile>();
			final String sql = String.format(
					"SELECT * FROM %s ORDER BY isdir DESC LIMIT %s,%s",
					getTableName(), start, num);

			final Connection connection = getConnection();
			final PreparedStatement prepareStatement = connection
					.prepareStatement(sql);
			final ResultSet resultSet = prepareStatement.executeQuery();
			BDFSFile file = null;
			while (resultSet.next()) {
				file = new BDFSFile();
				file.setId(resultSet.getString("id"));
				file.setfId(resultSet.getString("fid"));
				file.setDir(resultSet.getBoolean("isdir"));
				file.setPath(resultSet.getString("path"));
				file.setSession(resultSet.getLong("session"));
				file.setLength(resultSet.getLong("length"));
				file.setMtime(resultSet.getLong("mtime"));
				list.add(file);
			}
			resultSet.close();
			prepareStatement.close();
			connection.close();
//			stopwatch.stop(getClass().getSimpleName() + " query:" + num + " "
//					+ getTableName());
			return list;
		} catch (SQLException e) {
			Log.e(TAG, "query error", e);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see com.yuncore.bdfs.dao.BaseDao#getTag()
	 */
	@Override
	public String getTag() {
		return TAG;
	}
	
}