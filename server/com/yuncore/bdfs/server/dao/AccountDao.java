/**
 * 
 */
package com.yuncore.bdfs.server.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.yuncore.bdfs.entity.Account;

/**
 * @author ouyangfeng
 * 
 */
public class AccountDao extends BaseDao {

	Logger logger = Logger.getLogger(AccountDao.class.getSimpleName());

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yuncore.bdfs.db.BaseDao#getTableName()
	 */
	@Override
	public String getTableName() {
		return "account";
	}

	public synchronized Account getAccount() {
		try {
			final Connection connection = getDB();
			final PreparedStatement prepareStatement = connection
					.prepareStatement("SELECT username,password FROM "
							+ getTableName());
			final ResultSet resultSet = prepareStatement.executeQuery();
			Account result = null;
			if (resultSet.next()) {
				result = new Account();
				result.setPassword(resultSet.getString("password"));
				result.setUsername(resultSet.getString("username"));
			}
			resultSet.close();
			prepareStatement.close();
			connection.close();
			return result;
		} catch (SQLException e) {
			logger.error("", e);
		}
		return null;
	}

}
