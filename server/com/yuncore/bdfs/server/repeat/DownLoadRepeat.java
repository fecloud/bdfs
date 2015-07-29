package com.yuncore.bdfs.server.repeat;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.yuncore.bdfs.entity.BDFSFile;
import com.yuncore.bdfs.server.dao.BaseDao;
import com.yuncore.bdfs.server.dao.DownloadDao;
import com.yuncore.bdfs.server.util.Stopwatch;

public class DownLoadRepeat extends Thread {

	private static final int UNIT = 2000;
	
	protected Logger logger;

	protected DownloadDao downloadDao;

	protected RepeatDao repeatDao = new RepeatDao();

	protected Connection connection;
	
	public DownLoadRepeat() {
		logger = Logger.getLogger(getTAG());
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
			Stopwatch stopwatch = null;
			while (null != (list = downloadDao.query(UNIT))) {
				stopwatch = new Stopwatch();
				for (BDFSFile file : list) {
					if (exists(file)) {
						deleteIds.add(file.getfId());
					}
				}
				stopwatch.stop(getTAG() + " select exists");
				deletes(deleteIds);
			}
		} catch (Exception e) {
			logger.error("", e);
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
						.format("SELECT COUNT(*) FROM %s WHERE fid=?",
								selectTableName()));
		prepareStatement.setString(1, file.getfId());

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
			final Stopwatch stopwatch = new Stopwatch();
			stopwatch.start();
			final StringBuilder builder = new StringBuilder("DELETE FROM "
					+ getFromTableName() + " WHERE fid IN (");
			for (int i = 0; i < ids.size(); i++) {
				if (i != 0) {
					builder.append(",");
				}

				builder.append(String.format("\"%s\"", ids.get(i)));
			}
			builder.append(")");
			final PreparedStatement preparedStatement = connection
					.prepareStatement(builder.toString());
			connection.setAutoCommit(false);
			final int count = preparedStatement.executeUpdate();
			connection.commit();
			connection.setAutoCommit(true);
			logger.debug(String.format("%s delete %s exists files",
					getTAG(), count));
			preparedStatement.close();
			stopwatch.stop(getTAG() + " deletes count:" + ids.size());
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
