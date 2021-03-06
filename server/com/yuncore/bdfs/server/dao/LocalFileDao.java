package com.yuncore.bdfs.server.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

import com.yuncore.bdfs.entity.BDFSFile;
import com.yuncore.bdfs.server.util.Stopwatch;

public class LocalFileDao extends BaseDao {

	Logger logger = Logger.getLogger(LocalFileDao.class.getSimpleName());

	private StringBuilder insert;

	private int size;

	private static final int CACHE_SIZE = 5000;

	@Override
	public String getTableName() {
		return "localfile";
	}

	private boolean add(BDFSFile f) {
		if (size == 0) {
			insert = new StringBuilder(
					String.format(
							"INSERT INTO %s (id,path,length,isdir,mtime,fid,session) VALUES ",
							getTableName()));
		}
		if (size != 0) {
			insert.append(",");
		}
		// insert.append(String.format("(UUID(),\"%s\",\"%s\",%s,%s,\"%s\",%s)",
		// f
		// .getDir().replace("\\", "\\\\"), f.getName(), f.getLength(), f
		// .getType(), f.getfId(), f.getSession()));
		insert.append(String.format("(CONCAT(UUID(),RAND()),\"%s\",%s,%s,%s,\"%s\",%s)",
				f.getPath(), f.getLength(), f.isDir() ? 1 : 0, f.getMtime(), f.getfId(),
				f.getSession()));
		size++;
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

	public synchronized boolean insertCache(BDFSFile file) {
		if (size < CACHE_SIZE) {
			return add(file);
		} else {
			add(file);
			return insertAllCacaheFlush();
		}
	}

	public synchronized boolean insertAll() {
		try {

			Stopwatch stopwatch = new Stopwatch();
			stopwatch.start();
			final Connection connection = getDB();

			final Statement prepareStatement = connection
					.createStatement();
			connection.setAutoCommit(false);

			final boolean result = prepareStatement.executeUpdate(insert.toString()) == size;
			connection.commit();
			prepareStatement.close();

			connection.setAutoCommit(true);
			connection.close();

			stopwatch.stop("LocalFileDao insertAll " + size);
			return result;
		} catch (SQLException e) {
			logger.error("", e);
		}
		return false;
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

	protected static BDFSFile buildLocalFile(ResultSet resultSet)
			throws SQLException {
		final BDFSFile file = new BDFSFile();
		file.setId(resultSet.getString("id"));
		file.setPath(resultSet.getString("path"));
		file.setLength(resultSet.getLong("length"));
		file.setDir(resultSet.getBoolean("isdir"));
		file.setSession(resultSet.getLong("session"));
		return file;
	}

}
