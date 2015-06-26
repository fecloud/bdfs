package com.yuncore.bdfs.server.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.yuncore.bdfs.entity.BDFSFile;
import com.yuncore.bdfs.server.util.Stopwatch;

public class LocalFileDao extends BaseDao {

	Logger logger = Logger.getLogger(BaseDao.class.getSimpleName());

	private List<BDFSFile> caches = new ArrayList<BDFSFile>();

	@Override
	public String getTableName() {
		return "localfile";
	}

	public synchronized boolean insertAllCacahe(List<BDFSFile> files) {
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

	public synchronized boolean insertCache(BDFSFile file) {
		if (caches.size() < 10000) {
			return caches.add(file);
		} else {
			caches.add(file);
			return insertAllCacaheFlush();
		}
	}

	public synchronized boolean insertAll(List<BDFSFile> files) {
		try {

			Stopwatch stopwatch = new Stopwatch();
			stopwatch.start();
			final Connection connection = getDB();
			final PreparedStatement prepareStatement = connection
					.prepareStatement(String
							.format("INSERT INTO %s (id,dir,name,length,type,fid,session) VALUES(UUID(),?,?,?,?,?,?)",
									getTableName()));
			for (BDFSFile f : files) {
				prepareStatement.setString(1, f.getDir());
				prepareStatement.setString(2, f.getName());
				prepareStatement.setLong(3, f.getLength());
				prepareStatement.setLong(4, f.getType());
				prepareStatement.setString(5, f.getfId());
				prepareStatement.setLong(6, f.getSession());
				prepareStatement.addBatch();
			}

			connection.setAutoCommit(false);

			final boolean result = prepareStatement.executeBatch().length == caches
					.size();
			connection.commit();
			prepareStatement.clearBatch();
			prepareStatement.close();

			connection.setAutoCommit(true);
			connection.close();

			stopwatch.stop("LocalFileDao insertAll");
			return result;
		} catch (SQLException e) {
			logger.error("", e);
		}
		return false;
	}

	protected static BDFSFile buildLocalFile(ResultSet resultSet)
			throws SQLException {
		final BDFSFile file = new BDFSFile();
		file.setId(resultSet.getString("id"));
		file.setDir(resultSet.getString("dir"));
		file.setName(resultSet.getString("name"));
		file.setLength(resultSet.getLong("length"));
		file.setType(resultSet.getInt("type"));
		file.setSession(resultSet.getLong("session"));
		return file;
	}

}
