/**
 * 
 */
package com.yuncore.bdfs.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.yuncore.bdfs.entity.Account;
import com.yuncore.bdfs.util.Log;

/**
 * @author ouyangfeng
 * 
 */
public class AccountDao extends BaseDao {

	static String TAG = "AccountDao";

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
			final Connection connection = getConnection();
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
			Log.e(TAG, "", e);
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see com.yuncore.bdfs.dao.BaseDao#getTag()
	 */
	@Override
	public String getTag() {
		return TAG;
	}

}
