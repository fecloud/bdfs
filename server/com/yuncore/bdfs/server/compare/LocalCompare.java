package com.yuncore.bdfs.server.compare;

import java.util.List;

import com.yuncore.bdfs.server.Const;
import com.yuncore.bdfs.server.dao.LocalCompareDao;

public class LocalCompare {

	protected LocalCompareDao compareDao;

	public LocalCompare() {
		compareDao = new LocalCompareDao();
	}

	protected long getSession() {
		return Long.parseLong(System.getProperty(Const.LOCALLIST_SESSION, "0"));
	}

	/**
	 * 是否需要和以前的数据进行对比
	 * 
	 * @return
	 */
	public synchronized boolean needCompareBefore() {
		final List<Long> groupBySession = compareDao.groupBySession(compareDao
				.getCopyTableName());
		// 如果session有两个
		if (groupBySession != null && groupBySession.size() == 2) {
			return true;
		}
		return false;
	}

	/**
	 * 分发不同的部分到对应的表
	 * 
	 * @return
	 */
	private synchronized boolean dispathDeleteAndAction() {
		final List<Long> ss = compareDao.groupBySession(compareDao
				.getTableName());
		if (ss != null && !ss.isEmpty()) {
			final long cs = getSession();

			if (ss.size() == 1) {
				final long s = ss.get(0);
				if (s == cs) {
					// 把最新的扫描结果放上action表
					return compareDao.copyTableData(compareDao.getTableName(),
							compareDao.getActionTableName());
				} else {
					// 把最新的扫描结果放上delete表
					return compareDao.copyTableData(compareDao.getTableName(),
							compareDao.getDeleteTableName());
				}

			} else if (ss.size() == 2) {
				for (long s : ss) {
					if (s == cs) {
						// 把最新的扫描结果放上action表
						return compareDao.copyTableData(
								compareDao.getTableName(),
								compareDao.getActionTableName(),
								String.format("session=%s", cs));
					} else {
						// 把最新的扫描结果放上delete表
						return compareDao.copyTableData(
								compareDao.getDeleteTableName(),
								compareDao.getActionTableName());
					}
				}
			}
		}
		return false;
	}

	public synchronized boolean compare() {

		compareDao.clearTables();

		if (needCompareBefore()) {
			compareDao.copyFromLocaFile();
			compareDao.findSame();
			compareDao.deleteSame();
			dispathDeleteAndAction();
		} else {
			// 从来没有同步过,本地上传,云端下载
			compareDao.copyTableData(compareDao.getCopyTableName(),
					compareDao.getActionTableName());
		}
		compareDao.deleteBefore();
		compareDao.clearTables();

		return true;
	}

}
