package com.yuncore.bdfs.server.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

public class DBHelper {

	Logger logger = Logger.getLogger(DBHelper.class.getSimpleName());

	private static final int DB_VERSION = 1;

	public DBHelper() {

		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			logger.error("DBHelper Class.forName(\"com.mysql.jdbc.Driver\")", e);
		}

		try {

			final int dbVersion = dbVersion();
			if (dbVersion == 0) {
				createVersion();
				updateVersion();
				onCreateDB();
			} else if (DB_VERSION != dbVersion) {
				onUpdateDB(dbVersion, DB_VERSION);
				updateVersion();
			}
		} catch (Exception e) {
			logger.error("DBHelper error", e);
		}

	}

	public synchronized Connection getConnection() {
		try {

			final String url = "jdbc:mysql://rds6v2uvvrennaa.mysql.rds.aliyuncs.com:3306/db7k81jhgfm13wla?user=db7k81jhgfm13wla&password=ouyangfeng";
//			 final String url =
//			 "jdbc:mysql://localhost:3306/fcloud?user=root&password=root&useUnicode=true&characterEncoding=UTF-8";
			// final String url =
			// "jdbc:mysql://10.0.0.8:3306/bdfs?user=root&password=fcloud&useUnicode=true&characterEncoding=UTF-8";
			final Connection conn = DriverManager.getConnection(url);
			return conn;
		} catch (Exception e) {
			logger.error("DBHelper getConnection", e);
		}
		return null;
	}

	public synchronized boolean versionExists() {

		try {
			final Connection connection = getConnection();
			final PreparedStatement prepareStatement = connection
					.prepareStatement("SHOW TABLES LIKE 'version' ");

			final ResultSet resultSet = prepareStatement.executeQuery();

			String version = null;
			if (resultSet.next()) {
				version = resultSet.getString(1);
			}
			resultSet.close();
			prepareStatement.close();
			connection.close();
			return version != null;
		} catch (SQLException e) {
			logger.error("dbVersion", e);
		}
		return false;
	}

	protected synchronized int dbVersion() {

		int dbVersion = 0;
		if (versionExists()) {
			final String sql = "SELECT version FROM version";

			try {
				final Connection connection = getConnection();
				final PreparedStatement prepareStatement = connection
						.prepareStatement(sql);

				final ResultSet resultSet = prepareStatement.executeQuery();

				if (resultSet.next()) {
					dbVersion = resultSet.getInt(1);
				}
				resultSet.close();
				prepareStatement.close();
				connection.close();
			} catch (SQLException e) {
				logger.error("dbVersion", e);
			}
		}
		return dbVersion;

	}

	private synchronized void createVersion() throws SQLException {
		logger.info("createVersion");
		final Connection connection = getConnection();
		final PreparedStatement prepareStatement = connection
				.prepareStatement("CREATE TABLE version(id BIGINT PRIMARY KEY AUTO_INCREMENT,version BIGINT);");
		connection.setAutoCommit(false);
		prepareStatement.execute();
		connection.commit();
		connection.setAutoCommit(true);
		prepareStatement.close();
		connection.close();
	}

	private synchronized void updateVersion() throws SQLException {
		logger.info("updateVersion");
		final Connection connection = getConnection();
		final PreparedStatement prepareStatement = connection
				.prepareStatement("REPLACE INTO version (id,version) values (1,?)");
		prepareStatement.setInt(1, DB_VERSION);
		connection.setAutoCommit(false);
		prepareStatement.execute();
		connection.commit();
		connection.setAutoCommit(true);
		prepareStatement.close();
		connection.close();
	}

	protected synchronized void onCreateDB() {
		logger.info("onCreateDB");

		executeSQL("CREATE TABLE localfile (id varchar(36) PRIMARY KEY,dir VARCHAR(2048) ,name VARCHAR(2048), length BIGINT,type BIGINT,fid varchar(32),session BIGINT);"); // 本地文件记录
		executeSQL("CREATE TABLE localdelete (id VARCHAR(36) PRIMARY KEY, dir VARCHAR(2048) ,name VARCHAR(2048), length BIGINT,type BIGINT,fid varchar(32),session BIGINT);");// 本地被删除了
		executeSQL("CREATE TABLE localupload (id VARCHAR(36) PRIMARY KEY, dir VARCHAR(2048) ,name VARCHAR(2048), length BIGINT,type BIGINT,fid varchar(32),session BIGINT);");// 本地要被上传的

		executeSQL("CREATE TABLE cloudfile (id VARCHAR(36) PRIMARY KEY, dir VARCHAR(2048) ,name VARCHAR(2048), length BIGINT,type BIGINT,fid varchar(32),md5 varchar(32),session BIGINT);");
		executeSQL("CREATE TABLE clouddelete (id VARCHAR(36) PRIMARY KEY,dir VARCHAR(2048) ,name VARCHAR(2048), length BIGINT,type BIGINT,fid varchar(32),md5 varchar(32),session BIGINT);");
		executeSQL("CREATE TABLE clouddownload (id VARCHAR(36) PRIMARY KEY,dir VARCHAR(2048) ,name VARCHAR(2048), length BIGINT,type BIGINT,fid varchar(32),md5 varchar(32),session BIGINT);");

		executeSQL("CREATE TABLE cloudhistory (id int PRIMARY KEY AUTO_INCREMENT,time BIGINT );");
		executeSQL("CREATE TABLE localhistory (id int PRIMARY KEY AUTO_INCREMENT,time BIGINT );");

	}

	protected void onUpdateDB(int old_version, int newversion) {
		logger.warn(String.format("onUpdateDB old_version:%s newversion:%s",
				old_version, newversion));

	}

	public synchronized boolean executeSQL(String sql) {
		try {
			final Connection connection = getConnection();
			Statement createStatement = connection.createStatement();
			connection.setAutoCommit(false);
			createStatement.execute(sql);
			connection.commit();
			connection.setAutoCommit(true);
			createStatement.close();
			connection.close();
			return true;
		} catch (SQLException e) {
			logger.error("executeSQL error", e);
		}
		return false;
	}
}
