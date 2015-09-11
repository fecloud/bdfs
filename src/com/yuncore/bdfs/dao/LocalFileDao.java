package com.yuncore.bdfs.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.yuncore.bdfs.entity.BDFSFile;
import com.yuncore.bdfs.util.Log;
import com.yuncore.bdfs.util.Stopwatch;

public class LocalFileDao extends BaseDao {

	private static String TAG = "LocalFileDao";

	private List<BDFSFile> cache = new ArrayList<BDFSFile>();

	private int size;

	private static final int CACHE_SIZE = 5000;

	@Override
	public String getTableName() {
		return "localfile";
	}

	public synchronized boolean insertAllCacaheFlush() {
		if (size != 0) {
			final boolean result = insertAll();
			if(result){
				size = 0;
				cache.clear();
			}
			return result;
		} else {
			return true;
		}
	}

	public synchronized boolean insertCache(List<BDFSFile> files) {
		if (size < CACHE_SIZE) {
			size += files.size();
			return cache.addAll(files);
		} else {
			size += files.size();
			cache.addAll(files);
			return insertAllCacaheFlush();
		}
	}

	public synchronized boolean insertAll() {
		try {

			Stopwatch stopwatch = new Stopwatch();
			stopwatch.start();
			final Connection connection = getConnection();

			final String sql = String.format(
					"INSERT INTO %s ('path','length','isdir','mtime' ,'fid','session') VALUES (?,?,?,?,?,?)",
					getTableName());
			final PreparedStatement prepareStatement = connection.prepareStatement(sql);

			for (BDFSFile f : cache) {
				prepareStatement.setString(1, f.getPath());
				prepareStatement.setLong(2, f.getLength());
				prepareStatement.setInt(3, f.isDir() ? 1 : 0);
				prepareStatement.setLong(4, f.getMtime());
				prepareStatement.setString(5, f.toFid());
				prepareStatement.setLong(6, f.getSession());
				prepareStatement.addBatch();
			}
			connection.setAutoCommit(false);
			prepareStatement.executeBatch();

			connection.commit();
			connection.setAutoCommit(true);
			connection.close();

			stopwatch.stop("LocalFileDao insertAll " + size);
			return true;
		} catch (SQLException e) {
			Log.e(TAG, "", e);
		}
		return false;
	}

	/**
	 * 是否存在一样的数据
	 * 
	 * @param file
	 * @return
	 * 
	 * 		public boolean exists(BDFSFile file) { boolean con = false; if
	 *         (null != file) { try { final Connection connection = getDB();
	 *         final PreparedStatement prepareStatement = connection
	 *         .prepareStatement(String .format(
	 *         "SELECT COUNT(*) FROM %s WHERE fid=? AND isdir=? AND length=?" ,
	 *         getTableName())); prepareStatement.setString(1, file.getfId());
	 *         prepareStatement.setInt(2, file.isDir() ? 0 : 1);
	 *         prepareStatement.setLong(3, file.getLength());
	 * 
	 *         final ResultSet executeQuery = prepareStatement.executeQuery();
	 *         con = executeQuery.next(); executeQuery.close();
	 *         prepareStatement.close(); connection.close(); } catch
	 *         (SQLException e) { logger.error("", e); } } return con; }
	 */

	protected static BDFSFile buildLocalFile(ResultSet resultSet) throws SQLException {
		final BDFSFile file = new BDFSFile();
		file.setId(resultSet.getString("id"));
		file.setPath(resultSet.getString("path"));
		file.setLength(resultSet.getLong("length"));
		file.setDir(resultSet.getBoolean("isdir"));
		file.setSession(resultSet.getLong("session"));
		return file;
	}
	
	/* (non-Javadoc)
	 * @see com.yuncore.bdfs.dao.BaseDao#getTag()
	 */
	@Override
	public String getTag() {
		return TAG;
	}

}
