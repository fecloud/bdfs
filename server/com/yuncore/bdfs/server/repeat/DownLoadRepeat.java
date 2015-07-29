package com.yuncore.bdfs.server.repeat;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.yuncore.bdfs.entity.BDFSFile;
import com.yuncore.bdfs.server.BDFSServer;
import com.yuncore.bdfs.server.dao.BaseDao;
import com.yuncore.bdfs.server.dao.DownloadDao;

public class DownLoadRepeat extends Thread {

	Logger logger = Logger.getLogger(BDFSServer.class.getSimpleName());

	protected DownloadDao downloadDao;

	protected RepeatDao repeatDao = new RepeatDao();

	protected Connection connection;

	public DownLoadRepeat() {
		downloadDao = new DownloadDao();
	}

	protected String getFromTableName() {
		return downloadDao.getTableName();
	}

	protected String selectTableName() {
		return "localfile";
	}

	protected String getTAG() {
		return "DownLoadRepeat";
	}

	@Override
	public void run() {
		setName(getTAG());

		while (true) {
			try {
				doWork();
				Thread.sleep(5000);
			} catch (Exception e) {
			}
		}
	}

	private void doWork() {
		logger.debug(String.format("%s doWork...", getTAG()));
		connection = repeatDao.getConnection();

		List<BDFSFile> list = null;
		List<String> deleteIds = new ArrayList<String>();
		try {
			while (null != (list = downloadDao.query(1000))) {
				for (BDFSFile file : list) {
					if (exists(file)) {
						deleteIds.add(file.getId());
					}
				}
				deletes(deleteIds);
			}
		} catch (Exception e) {
		} finally {
			if (null != connection) {
				try {
					connection.close();
				} catch (SQLException e) {
					connection = null;
				}
			}
		}
	}

	/**
	 * 查找重复的
	 * 
	 * @param file
	 * @return
	 * @throws SQLException
	 */
	protected synchronized boolean exists(BDFSFile file) throws SQLException {
		boolean conn = false;
		final PreparedStatement prepareStatement = connection
				.prepareStatement(String
						.format("SELECT COUNT(*) FROM %s WHERE fid=? AND isdir=? AND length=?",
								selectTableName()));
		prepareStatement.setString(1, file.getfId());
		prepareStatement.setInt(2, file.isDir() ? 0 : 1);
		prepareStatement.setLong(3, file.getLength());

		final ResultSet executeQuery = prepareStatement.executeQuery();
		conn = executeQuery.next();
		executeQuery.close();
		prepareStatement.close();
		return conn;
	}

	/**
	 * 删除已存在的
	 * 
	 * @param ids
	 * @throws SQLException
	 */
	protected synchronized void deletes(List<String> ids) throws SQLException {
		if (null != ids && !ids.isEmpty()) {
			final StringBuilder builder = new StringBuilder("DELETE FROM "
					+ getFromTableName() + " WHERE id IN (");
			for (int i = 0; i < ids.size(); i++) {
				if (i != 0) {
					builder.append(",");
				}

				builder.append(ids.get(i));
			}
			builder.append(")");
			final PreparedStatement preparedStatement = connection
					.prepareStatement(builder.toString());
			final int count = preparedStatement.executeUpdate();
			logger.debug(String.format("%s delete %s local exists files",
					getTAG(), count));
			preparedStatement.close();
		}
	}

	protected class RepeatDao extends BaseDao {

		@Override
		public String getTableName() {
			return "";
		}

		public synchronized Connection getConnection() {
			return getDB();
		}

	}
}
