package com.yuncore.bdfs.server.dao;
///**
// * 
// */
//package com.yuncore.dbpcs.db;
//
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.ArrayList;
//import java.util.List;
//
//import com.yuncore.dbpcs.entity.CloudFile;
//import com.yuncore.dbpcs.tools.PCSUtil;
//
///**
// * @author ouyangfeng
// * 
// */
//public class PCSServerRecycleDao extends CloudFileDao {
//
//	private static final String TABLENAME = "server_recycle";
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see com.yuncore.dbpcs.db.PCSBaseDao#getTableName()
//	 */
//	@Override
//	public String getTableName() {
//		return TABLENAME;
//	}
//
//	public boolean deleteAll() {
//
//		try {
//			getDB().setAutoCommit(false);
//
//			final PreparedStatement prepareStatement = getDB()
//					.prepareStatement(
//							String.format("DELETE FROM %s ", getTableName()));
//			prepareStatement.execute();
//			getDB().commit();
//			getDB().setAutoCommit(true);
//			return true;
//		} catch (SQLException e) {
//			logger.error("deleteAll", e);
//		}
//		return false;
//	}
//
//	/**
//	 * 查询过期的文件,10天可删除的
//	 * 
//	 * @return
//	 */
//	public List<CloudFile> queryExpired() {
//		final long expired_time = PCSUtil.current_time_ss()
//				- (10 * 24 * 60 * 60);
//		ArrayList<CloudFile> files = null;
//
//		try {
//			final PreparedStatement prepareStatement = getDB()
//					.prepareStatement(
//							String.format("SELECT * FROM %s WHERE %s <= ?",
//									getTableName(), server_mtime));
//			prepareStatement.setLong(1, expired_time);
//
//			final ResultSet resultSet = prepareStatement.executeQuery();
//			if (resultSet.next()) {
//				files = new ArrayList<CloudFile>();
//				files.add(buildPCSFile(resultSet));
//				while (resultSet.next()) {
//					files.add(buildPCSFile(resultSet));
//				}
//				resultSet.close();
//			}
//			prepareStatement.close();
//
//		} catch (SQLException e) {
//			logger.error("queryExpired", e);
//		}
//		return files;
//	}
//
//	/**
//	 * 查询过期的文件,10天可删除的
//	 * 
//	 * @return
//	 */
//	public List<CloudFile> queryUnExpired() {
//		final long expired_time = PCSUtil.current_time_ss()
//				- (10 * 24 * 60 * 60);
//		ArrayList<CloudFile> files = null;
//
//		try {
//			final PreparedStatement prepareStatement = getDB()
//					.prepareStatement(
//							String.format("SELECT * FROM %s WHERE %s > ?",
//									getTableName(), server_mtime));
//			prepareStatement.setLong(1, expired_time);
//
//			final ResultSet resultSet = prepareStatement.executeQuery();
//			if (resultSet.next()) {
//				files = new ArrayList<CloudFile>();
//				files.add(buildPCSFile(resultSet));
//				while (resultSet.next()) {
//					files.add(buildPCSFile(resultSet));
//				}
//				resultSet.close();
//			}
//			prepareStatement.close();
//
//		} catch (SQLException e) {
//			logger.error("queryExpired", e);
//		}
//		return files;
//	}
//
//}
