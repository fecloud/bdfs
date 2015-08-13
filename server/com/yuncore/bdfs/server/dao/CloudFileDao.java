package com.yuncore.bdfs.server.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.apache.log4j.Logger;

import com.yuncore.bdfs.entity.CloudFile;
import com.yuncore.bdfs.server.Const;
import com.yuncore.bdfs.server.util.Stopwatch;

public class CloudFileDao extends BaseDao {

	Logger logger = Logger.getLogger(CloudFileDao.class.getSimpleName());

	private StringBuilder insert;

	private int size;

	private static final int CACHE_SIZE = 5000;

	public synchronized boolean insertAllCacahe(List<CloudFile> files) {
		if (size < CACHE_SIZE) {
			return addAll(files);
		} else {
			addAll(files);
			return insertAllCacaheFlush();
		}
	}

	private boolean addAll(List<CloudFile> files) {
		if (size == 0) {
			insert = new StringBuilder(
					String.format(
							"INSERT INTO %s (id,path,length,isdir,mtime,fid,md5,session) VALUES ",
							getTableName()));
		}
		final long session = Long.parseLong(System.getProperty(
				Const.CLOUDLIST_SESSION, "0"));
		for (CloudFile f : files) {
			f.setSession(session);
			if (size != 0) {
				insert.append(",");
			}
			insert.append(String.format(
					"(CONCAT(UUID(),RAND()),\"%s\",%s,%s,%s,\"%s\",\"%s\",%s)", f.getPath(),
					f.getLength(), f.isDir() ? 1 : 0, f.getMtime(), f.toFid(),
					f.getMd5() == null ? "" : f.getMd5(), f.getSession()));
			size++;
		}
		return true;
	}

	public synchronized boolean insertAllCacaheFlush() {
		if (size != 0) {
			final boolean result = insertAll();
			size = 0;
			insert = null;
			return result;
		} else {
			return true;
		}
	}

	public synchronized boolean insertAll() {
		try {
			Stopwatch stopwatch = new Stopwatch();
			stopwatch.start();
			final Connection connection = getDB();

			final Statement prepareStatement = connection.createStatement();
			connection.setAutoCommit(false);

			final boolean result = prepareStatement.executeUpdate(insert
					.toString()) == size;
			connection.commit();
			prepareStatement.close();

			connection.setAutoCommit(true);
			connection.close();

			stopwatch.stop("CloudFileDao insertAll " + size);
			return result;
		} catch (SQLException e) {
			logger.error("error " + insert.toString(), e);
		}
		return false;
	}

	public synchronized boolean deleteFiles(List<CloudFile> files) {
		try {

			final Connection connection = getDB();
			final PreparedStatement prepareStatement = connection
					.prepareStatement(String.format(
							"DELETE FROM %s WHERE path=?", getTableName()));
			for (CloudFile f : files) {
				prepareStatement.setString(1, f.getPath());
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

	protected static CloudFile buildLocalFile(ResultSet resultSet)
			throws SQLException {
		final CloudFile file = new CloudFile();
		file.setId(resultSet.getString("id"));
		file.setPath(resultSet.getString("path"));
		file.setLength(resultSet.getLong("length"));
		file.setDir(resultSet.getBoolean("isdir"));
		file.setSession(resultSet.getLong("session"));
		file.setMtime(resultSet.getLong("mtime"));
		return file;
	}

	/**
	 * 是否存在一样的数据
	 * @param file
	 * @return
	 
	public boolean exists(BDFSFile file) {
		boolean con = false;
		if (null != file) {
			try {
				final Connection connection = getDB();
				final PreparedStatement prepareStatement = connection
						.prepareStatement(String
								.format("SELECT COUNT(*) FROM %s WHERE fid=? AND isdir=? AND length=?",
										getTableName()));
				prepareStatement.setString(1, file.getfId());
				prepareStatement.setInt(2, file.isDir() ? 0 : 1);
				prepareStatement.setLong(3, file.getLength());

				final ResultSet executeQuery = prepareStatement.executeQuery();
				con = executeQuery.next();
				executeQuery.close();
				prepareStatement.close();
				connection.close();
			} catch (SQLException e) {
				logger.error("", e);
			}
		}
		return con;
	}
	*/
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
