//package com.yuncore.dbpcs.sync.recycle;
//
//import org.apache.log4j.Logger;
//
//import com.yuncore.dbpcs.api.PCSApi;
//import com.yuncore.dbpcs.api.imple.PCSApiImple;
//import com.yuncore.dbpcs.db.PCSServerRecycleDao;
//import com.yuncore.dbpcs.entity.CloudFile;
//import com.yuncore.dbpcs.entity.CloudPageFile;
//import com.yuncore.dbpcs.tools.FileRM;
//
///**
// * 从服务器端读取回收站删除文件
// * 
// * @author ouyangfeng
// * 
// */
//public class PCSServerRecycleService implements Runnable {
//
//	Logger logger = Logger.getLogger(PCSServerRecycleService.class
//			.getSimpleName());
//
//	private PCSApi pcsApi = new PCSApiImple();
//
//	private volatile boolean flag;
//
//	private Thread service;
//
//	private int interval = 30000;
//
//	private PCSServerRecycleDao recycleDao = new PCSServerRecycleDao();
//
//	public PCSServerRecycleService(PCSApi pcsApi) {
//		super();
//		this.pcsApi = pcsApi;
//	}
//
//	public PCSServerRecycleService() {
//	}
//
//	public PCSServerRecycleService(PCSApi pcsApi, int interval) {
//		super();
//		this.pcsApi = pcsApi;
//		this.interval = interval;
//	}
//
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see java.lang.Thread#run()
//	 */
//	@Override
//	public void run() {
//		logger.debug(String.format("%s start...",
//				PCSServerRecycleService.class.getSimpleName()));
//		while (flag) {
//			doWork();
//			synchronized (this) {
//				try {
//					wait(interval);
//				} catch (InterruptedException e) {
//					logger.error("", e);
//				}
//			}
//		}
//		logger.debug(String.format("%s exit",
//				PCSServerRecycleService.class.getName()));
//	}
//
//	protected void doWork() {
//
////		deleteExpiredFiles();
//
//		if (pcsApi != null) {
//			final CloudPageFile recyclebin = pcsApi.recyclebin();
//			if (recyclebin != null && recyclebin.getList() != null) {
//				for (CloudFile file : recyclebin.getList()) {
//
//					// 移动到回收站
//					String scr = System.getProperty("syncdir")
//							+ file.getAbsolutePath();
//					new FileRM(scr).rm();
//				}
//				//清除表里面的所有数据,然后重新插入
//				if (recycleDao.clear()) {
//					recycleDao.insertAll(recyclebin.getList());
//				}
//			}
//		}
//
//	}
//
//	/**
//	 * 删除到期的文件,服务器到期也会删除,不到期的保存10天
//	 */
////	private void deleteExpiredFiles() {
////
////		final List<PCSFile> queryExpired = recycleDao.queryExpired();
////		if (null != queryExpired) {
////
////			final FileRM fileRM = new FileRM();
////			for (PCSFile f : queryExpired) {
////				final String path = String.format("%s%s%s",
////						System.getProperty("recycle"), File.separator,
////						f.getAbsolutePath());
////				logger.debug(String.format("delete file:", path));
////				fileRM.setPath(path);
////				fileRM.rm();
////			}
////
////			// 删除记录
////			recycleDao.deleteFiles(queryExpired);
////		}
////
////	}
//
//	/**
//	 * 启动服务
//	 */
//	public synchronized void startService() {
//
//		if (service == null) {
//			service = new Thread(this);
//			this.flag = true;
//			service.setName(PCSServerRecycleService.class.getSimpleName());
//			service.start();
//		}
//
//	}
//
//	/**
//	 * 停止服务
//	 */
//	public synchronized void stopService() {
//		this.flag = false;
//		notifyAll();
//	}
//
//	public synchronized boolean isStop() {
//		return flag == false;
//	}
//
//	public synchronized boolean isSart() {
//		return flag;
//	}
//
//}
