package com.yuncore.bdfs.server.repeat;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Iterator;
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
				Thread.sleep(30000);
			} catch (Exception e) {
			}
		}
	}

	private void doWork() {
		logger.debug(String.format("%s doWork...", getTAG()));
		connection = repeatDao.getConnection();

		List<BDFSFile> list = null;
		HashSet<String> deleteIds = null;
		try {
			if (chekcSelectTable()) {
				long start = 0;
				Stopwatch stopwatch = null;
				while (null != (list = downloadDao.query(start, UNIT))
						&& !list.isEmpty()) {
					stopwatch = new Stopwatch();
					stopwatch.start();
					deleteIds = exists(list);
					stopwatch.stop(getTAG() + " select exists");
					if (null != deleteIds && !deleteIds.isEmpty()) {
						//找到重复的,删除了limit 不加
						deletes(deleteIds);
					}else {
						start += list.size();
					}
				}
			} else {
				logger.warn(selectTableName() + " no data return");
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
	 * 检查查询表是否有数据
	 * @return
	 * @throws SQLException
	 */
	protected boolean chekcSelectTable() throws SQLException {
		boolean conn = false;
		final PreparedStatement preparedStatement = connection.prepareStatement("SELECT COUNT(*) FROM " + selectTableName());
		final ResultSet resultSet = preparedStatement.executeQuery();
		if(resultSet != null && resultSet.next()){
			final long count = resultSet.getLong(1);
			conn = (count > 0);
		}
		resultSet.close();
		preparedStatement.close();
		return conn;
	}
	
	/**
	 * 查找重复的
	 * 
	 * @param file
	 * @return
	 * @throws SQLException
	 */
	protected synchronized HashSet<String> exists(List<BDFSFile> files)
			throws SQLException {
		final HashSet<String> list = new HashSet<String>();
		if (null != files && !files.isEmpty()) {
			final StringBuilder builder = new StringBuilder("SELECT fid FROM "
					+ selectTableName() + " WHERE fid IN (");
			for (int i = 0; i < files.size(); i++) {
				if (i != 0) {
					builder.append(",");
				}

				builder.append("\"").append( files.get(i).getfId()).append("\"");
			}
			builder.append(")");
			final PreparedStatement prepareStatement = connection
					.prepareStatement(builder.toString());
			final ResultSet resulSet = prepareStatement.executeQuery();
			if (null != resulSet) {
				while (resulSet.next()) {
					list.add(resulSet.getString(1));
				}
				resulSet.close();
			}
			prepareStatement.close();
			
			logger.debug("found " + list.size() + " in " + selectTableName());
		}
		return list;
	}

	/**
	 * 删除已存在的
	 * 
	 * @param ids
	 * @throws SQLException
	 */
	protected synchronized void deletes(HashSet<String> ids) throws SQLException {
		if (null != ids && !ids.isEmpty()) {
			final Stopwatch stopwatch = new Stopwatch();
			stopwatch.start();
			final StringBuilder builder = new StringBuilder("DELETE FROM "
					+ getFromTableName() + " WHERE fid IN (");
			final Iterator<String> iterator = ids.iterator();
			if(iterator.hasNext()){
				builder.append("\"").append(iterator.next()).append("\"");
				while(iterator.hasNext()){
					builder.append(",").append("\"").append(iterator.next()).append("\"");
				}
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
