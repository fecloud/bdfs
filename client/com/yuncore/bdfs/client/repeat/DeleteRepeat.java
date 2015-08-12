/**
 * @(#) DeleteRepeat.java Created on 2015-8-12
 *
 * 
 */
package com.yuncore.bdfs.client.repeat;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.yuncore.bdfs.client.api.FSApi;
import com.yuncore.bdfs.client.entity.CloudRmResult;
import com.yuncore.bdfs.exception.ApiException;

/**
 * The class <code>DeleteRepeat</code>
 * 
 * @author Feng OuYang
 * @version 1.0
 */
public class DeleteRepeat extends Thread {

	boolean flag = true;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {

		try {
			Connection connection = DriverManager.getConnection(URL);
			final Scanner scanner = new Scanner(System.in);
			String line = null;
			while (flag) {
				String md5 = findSameMd5(connection);
				if (null != md5) {
					final List<String> fs = getSameMd5(connection, md5);
					if (!fs.isEmpty()) {
						System.out
								.println("---------------------------------------------------");
						System.out.println(String.format(
								"发现%s个md5相同的文件,请选择要删除的,按q退出、跳过0", fs.size()));
						for (int i = 0; i < fs.size(); i++) {
							System.out.println(String.format("%s.%s", (i + 1),
									fs.get(i)));
						}
						line = scanner.nextLine();
						if (line.startsWith("q")) {
							flag = false;
							System.exit(1);
							break;
						} else if (line.length() == 0
								|| line.trim().startsWith("0")) {
							System.out.println("跳过");
						} else {
							System.out.println("删除文件中");
							final String[] deletes = findDelete(line, fs);
							if (null != deletes && deletes.length > 0) {
								if (!deleteFiles(deletes)) {
									continue;
								}
							}
						}
						deleteMd5(connection, md5);
					}
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	final static String URL = "jdbc:mysql://rds6v2uvvrennaa.mysql.rds.aliyuncs.com:3306/db7k81jhgfm13wla?user=db7k81jhgfm13wla&password=ouyangfeng";

	static {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public List<String> getSameMd5(Connection connection, String md5)
			throws SQLException {
		PreparedStatement prepareStatement = connection
				.prepareStatement(String
						.format("SELECT path FROM clouddownload where md5=\"%s\" order by path",
								md5));
		ResultSet executeQuery = prepareStatement.executeQuery();
		final List<String> list = new ArrayList<String>();
		while (executeQuery.next()) {
			list.add(executeQuery.getString(1));
		}

		executeQuery.close();
		prepareStatement.close();
		return list;
	}

	public boolean deleteMd5(Connection connection, String md5) {
		try {
			PreparedStatement prepareStatement = connection
					.prepareStatement(String.format(
							"DELETE FROM sames where md5=\"%s\"", md5));
			int executeUpdate = prepareStatement.executeUpdate();
			prepareStatement.close();
			return executeUpdate > 0;
		} catch (SQLException e) {
		}
		return false;
	}

	public String[] findDelete(String line, List<String> fs) {
		if (line.trim().length() > 0) {
			final String l = line.trim();
			final List<String> list = new ArrayList<>();
			for (int i = 0; i < l.length(); i++) {
				char charAt = l.charAt(0);
				if (charAt > '0' && charAt <= '9') {

					int parseInt = Integer.parseInt("" + charAt);
					if (parseInt <= fs.size()) {
						list.add(fs.get(parseInt - 1));
					}
				}
			}
			if (list.size() > 0) {
				final String[] files = new String[list.size()];
				list.toArray(files);
				return files;
			}
		}
		return null;
	}

	public String findSameMd5(Connection connection) throws SQLException {

		PreparedStatement prepareStatement = connection
				.prepareStatement("SELECT md5 FROM sames where md5<>\"\"  LIMIT 0,1");
		ResultSet executeQuery = prepareStatement.executeQuery();
		String result = null;
		if (executeQuery.next()) {
			result = executeQuery.getString(1);
		}

		executeQuery.close();
		prepareStatement.close();
		return result;
	}

	public boolean deleteFiles(String[] files) {
		final FSApi api = new com.yuncore.bdfs.client.api.imple.FSApiImple();
		try {
			CloudRmResult rm = api.rm(files);
			if (null != rm && rm.getErrno() == 0) {
				System.out.println("删除文件成功");
				return true;
			}
		} catch (ApiException e) {
			e.printStackTrace();
		}
		System.out.println("删除文件失败");
		return false;
	}

}
