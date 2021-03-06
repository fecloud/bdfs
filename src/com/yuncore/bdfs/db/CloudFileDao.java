package com.yuncore.bdfs.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.yuncore.bdfs.entity.CloudFile;
import com.yuncore.bdfs.entity.LocalFile;
import com.yuncore.bdfs.sync.cloud.GetCloudFile;
import com.yuncore.bdfs.tools.Stopwatch;

public class CloudFileDao extends BaseDao {

	Logger logger = Logger.getLogger(BaseDao.class.getSimpleName());

	private List<CloudFile> caches = new ArrayList<CloudFile>();

	public synchronized boolean insertAllCacahe(List<CloudFile> files) {
		if (caches.size() < 10000) {
			return caches.addAll(files);
		} else {
			caches.addAll(files);
			return insertAllCacaheFlush();
		}
	}

	public synchronized boolean insertAllCacaheFlush() {
		final boolean result = insertAll(caches);
		caches.clear();
		return result;
	}

	public synchronized boolean insertAll(List<CloudFile> files) {
		try {
			Stopwatch stopwatch = new Stopwatch();
			stopwatch.start();
			final Connection connection = getDB();
			final PreparedStatement prepareStatement = connection
					.prepareStatement(String
							.format("INSERT INTO %s (id,dir,name,length,type,fid,session) VALUES(UUID(),?,?,?,?,?,?)",
									getTableName()));
			for (CloudFile f : files) {
				prepareStatement.setString(1, f.getDir());
				prepareStatement.setString(2, f.getName());
				prepareStatement.setLong(3, f.getLength());
				prepareStatement.setLong(4, f.getType());
				prepareStatement.setString(5, f.toFid());
				prepareStatement.setString(6,
						System.getProperty(GetCloudFile.CLOUDLIST_SESSION));
				prepareStatement.addBatch();
			}

			connection.setAutoCommit(false);

			final boolean result = prepareStatement.executeBatch().length == caches
					.size();
			prepareStatement.clearBatch();
			prepareStatement.close();

			connection.commit();
			connection.setAutoCommit(true);
			connection.close();

			stopwatch.stop("CloudFileDao insertAll");
			return result;
		} catch (SQLException e) {
			logger.error("", e);
		}
		return false;
	}

	public synchronized boolean deleteFiles(List<CloudFile> files) {
		try {

			final Connection connection = getDB();
			final PreparedStatement prepareStatement = connection
					.prepareStatement(String.format(
							"DELETE FROM %s WHERE dir=? and name=?",
							getTableName()));
			for (LocalFile f : files) {
				prepareStatement.setString(1, f.getDir());
				prepareStatement.setString(2, f.getName());
				prepareStatement.addBatch();
			}
			connection.setAutoCommit(false);
			final boolean result = prepareStatement.executeBatch().length == files
					.size();
			prepareStatement.close();
			connection.commit();
			connection.setAutoCommit(true);
			connection.close();
			return result;
		} catch (SQLException e) {
			logger.error("", e);
		}
		return false;
	}

	protected static LocalFile buildLocalFile(ResultSet resultSet)
			throws SQLException {
		final LocalFile file = new LocalFile();
		file.setId(resultSet.getString("id"));
		file.setDir(resultSet.getString("dir"));
		file.setName(resultSet.getString("name"));
		file.setLength(resultSet.getLong("length"));
		file.setType(resultSet.getInt("type"));
		file.setSession(resultSet.getLong("session"));
		return file;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yuncore.dbpcs.db.BaseDao#getTableName()
	 */
	@Override
	public String getTableName() {
		return "cloudfile";
	}
}
