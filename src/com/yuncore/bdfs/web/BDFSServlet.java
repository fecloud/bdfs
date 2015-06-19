/**
 *  本地扫描上传
 */
package com.yuncore.bdfs.web;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Collections;
import java.util.List;
import java.util.zip.GZIPInputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.yuncore.bdfs.db.AccountDao;
import com.yuncore.bdfs.db.CloudFileDeleteDao;
import com.yuncore.bdfs.db.CloudHistoryDao;
import com.yuncore.bdfs.db.CookieDao;
import com.yuncore.bdfs.db.DownloadDao;
import com.yuncore.bdfs.db.LocalHistoryDao;
import com.yuncore.bdfs.entity.Account;
import com.yuncore.bdfs.entity.LocalFile;
import com.yuncore.bdfs.entity.LocalHistory;
import com.yuncore.bdfs.server.localflle.UploadLocalFile;
import com.yuncore.bdfs.tools.Util;

/**
 * @author ouyangfeng
 * 
 */
public class BDFSServlet extends HttpServlet {

	Logger logger = Logger.getLogger(BDFSServlet.class.getSimpleName());

	private static UploadLocalFile uploadLocalFile = new UploadLocalFile();

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest
	 * , javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		logger.debug("doPost");
		doGet(req, resp);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		logger.debug("doGet");
		resp.setHeader("Content-Encoding", "gzip");
		if (checkAction(req, resp)) {
			dispath(req, resp);
		}
	}

	private void dispath(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		final String action = req.getParameter("action");
		logger.debug("action:" + action);
		final JSONObject object = new JSONObject();
		if (action.equals("status")) {
			object.put("code", 200);
		} else if (action.equals("uploadlocal")) {
			dispathUploadLocal(req, resp, object);
		} else if (action.equals("env")) {
			printEnv(object);
		} else if (action.equals("account")) {
			getAccount(object);
		} else if (action.equals("tmpdir")) {
			listTmpDir(object);
		} else if (action.equals("rmtmp")) {
			rmTmp(object);
		} else if (action.equals("getdownload")) {
			getDownLoad(object);
		} else if (action.equals("deldownload")) {
			delDownLoad(req, object);
		} else if (action.equals("getclouddelete")) {
			getCloudDelete(object);
		} else if (action.equals("delclouddelete")) {
			delCloudDelete(req, object);
		} else if (action.equals("threads")) {
			getThreads(object);
		} else if (action.equals("cpuinfo")) {
			object.put("code", 200);
			object.put("data", Runtime.getRuntime().availableProcessors());
		} else if (action.equals("cookie")) {
			getCookie(object);
		} else if (action.equals("shell")) {
			execShell(req, object);
		} else if (action.equals("cloudhistory")) {
			cloudHistory(req, object);
		} else if (action.equals("localhistory")) {
			localHistory(req, object);
		} else {
			object.put("code", 500);
			object.put("msg", "not support");
		}
		resp.setContentType("application/json;charset=UTF-8");
		resp.getOutputStream().write(Util.gzip(object.toString().getBytes()));
	}

	private void localHistory(HttpServletRequest req, JSONObject object) {
		final String count = req.getParameter("count");
		List<LocalHistory> list = null;
		if (null != count) {
			try {
				list = new LocalHistoryDao()
						.getHistory(Integer.parseInt(count));
			} catch (Exception e) {
				object.put("code", 500);
				return;
			}
		} else {
			list = new LocalHistoryDao().getHistory();
		}
		object.put("code", 200);
		if (null != list && !list.isEmpty()) {
			final JSONArray array = new JSONArray();
			for (LocalHistory history : list) {
				array.put(history.toJSONObject());
			}
			object.put("data", array);
		}

	}

	private void cloudHistory(HttpServletRequest req, JSONObject object) {
		final String count = req.getParameter("count");
		List<LocalHistory> list = null;
		if (null != count) {
			try {
				list = new CloudHistoryDao()
						.getHistory(Integer.parseInt(count));
			} catch (Exception e) {
				object.put("code", 500);
				return;
			}
		} else {
			list = new CloudHistoryDao().getHistory();
		}

		object.put("code", 200);
		if (null != list && !list.isEmpty()) {

			final JSONArray array = new JSONArray();
			for (LocalHistory history : list) {
				array.put(history.toJSONObject());
			}
			object.put("data", array);
		}

	}

	/**
	 * 执行shell
	 * 
	 * @param object
	 */
	private void execShell(HttpServletRequest req, JSONObject object) {
		String command = req.getParameter("cmd");
		if (null != command && command.trim().length() > 0) {
			String result;
			try {
				result = Util.execmd(command);
				object.put("code", 200);
				object.put("data", result);
			} catch (Exception e) {
				logger.error("execShell", e);
				object.put("code", 500);
				object.put("msg", e.getMessage());
			}

		} else {
			object.put("code", 400);
			object.put("msg", "cmd is null");
		}

	}

	/**
	 * @param object
	 */
	private void getCookie(JSONObject object) {
		final String jsString = new CookieDao().getCookie();
		object.put("code", 200);
		if (null != jsString)
			object.put("data", jsString);
	}

	private void delCloudDelete(HttpServletRequest req, JSONObject object) {
		final String id = req.getParameter("id");
		if (id == null) {
			object.put("code", 500);
			object.put("msg", "not found id");
		} else {
			final boolean delete = new CloudFileDeleteDao().delete(id);
			object.put("code", 200);
			object.put("data", delete);

		}
	}

	private void delDownLoad(HttpServletRequest req, JSONObject object) {
		final String id = req.getParameter("id");
		if (id == null) {
			object.put("code", 500);
			object.put("msg", "not found id");
		} else {
			final boolean delete = new DownloadDao().delete(id);
			object.put("code", 200);
			object.put("data", delete);

		}
	}

	private void getCloudDelete(JSONObject object) {
		object.put("code", 200);
		final LocalFile file = new CloudFileDeleteDao().query();
		if (null != file) {
			object.put("data", file.toJSON());
		}
	}

	/**
	 * 取当前进程里的所有线程
	 * 
	 * @param object
	 */
	private void getThreads(JSONObject object) {
		object.put("code", 200);
		final List<String> list = Util.getAllThreads();
		Collections.sort(list);
		if (null != list) {
			final JSONArray array = new JSONArray();
			for (String s : list) {
				array.put(s);
			}
			object.put("data", array);
		}
	}

	/**
	 * 取一个下载任务
	 */
	private void getDownLoad(JSONObject object) {
		object.put("code", 200);
		final LocalFile file = new DownloadDao().query();
		if (null != file) {
			object.put("data", file.toJSON());
		}
	}

	/**
	 * 清除tmp目录所有文件
	 * 
	 * @param object
	 */
	private void rmTmp(JSONObject object) {
		final File dir = new File(System.getProperty("java.io.tmpdir"));
		logger.debug("rmTmp dir:" + dir.getAbsolutePath());
		if (dir.exists()) {
			final File[] list = dir.listFiles();
			object.put("code", 200);
			if (null != list && list.length > 0) {
				final JSONArray array = new JSONArray();
				JSONObject once = null;
				for (File s : list) {
					once = new JSONObject();
					once.put("name", s.getAbsolutePath());
					once.put("size", s.length());
					once.put("time", Util.formatTime(s.lastModified()));
					array.put(once);
					s.delete();
				}
				object.append("files", array);
			}
		} else {
			object.put("code", 500);
			object.put("msg", "java.io.tmpdir not exists");
		}
	}

	/**
	 * 列出tmp目录所有文件
	 * 
	 * @param object
	 */
	private void listTmpDir(JSONObject object) {
		final File dir = new File(System.getProperty("java.io.tmpdir"));
		logger.debug("listTmpDir dir:" + dir.getAbsolutePath());
		if (dir.exists()) {
			final File[] list = dir.listFiles();
			object.put("code", 200);
			if (null != list && list.length > 0) {
				final JSONArray array = new JSONArray();
				JSONObject once = null;
				for (File s : list) {
					once = new JSONObject();
					once.put("name", s.getAbsolutePath());
					once.put("size", s.length());
					once.put("time", Util.formatTime(s.lastModified()));
					array.put(once);
				}
				object.append("files", array);
			}
		} else {
			object.put("code", 500);
			object.put("msg", "java.io.tmpdir not exists");
		}
	}

	/**
	 * 取帐号密码
	 * 
	 * @param object
	 */
	private void getAccount(JSONObject object) {
		AccountDao accountDao = new AccountDao();
		Account account = accountDao.getAccount();
		if (null != account) {
			object.put("code", 200);
			account.toJSON(object);
		} else {
			object.put("code", 500);
			object.put("msg", "not account");
		}
	}

	/**
	 * 打印环境变量
	 * 
	 * @param object
	 */
	private void printEnv(JSONObject object) {
		final ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(arrayOutputStream);
		System.getProperties().list(out);
		object.put("code", 200);
		object.put("env", new String(arrayOutputStream.toByteArray()));
	}

	/**
	 * 检查action
	 * 
	 * @param req
	 * @param resp
	 * @return
	 * @throws IOException
	 */
	private boolean checkAction(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		final JSONObject object = new JSONObject();
		final String action = req.getParameter("action");
		if (null != action) {
			return true;
		} else {
			object.put("code", 400);
			object.put("msg", "check action");
		}
		resp.setContentType("application/json;charset=UTF-8");
		resp.getOutputStream().write(Util.gzip(object.toString().getBytes()));
		return false;
	}

	/**
	 * 本地文件系统上传
	 * 
	 * @param req
	 * @param resp
	 * @param object
	 * @throws IOException
	 */
	private void dispathUploadLocal(HttpServletRequest req,
			HttpServletResponse resp, JSONObject object) throws IOException {

		final int contentLength = req.getContentLength();
		if (req.getMethod().equalsIgnoreCase("POST") && contentLength != -1) {
			if (saveFileAndCompare(req)) {
				object.put("code", 200);
			} else {
				object.put("code", 500);
			}
		} else {
			object.put("code", 400);
			object.put("msg", "not support get or not data");
		}
	}

	/**
	 * 保存文件
	 * 
	 * @param req
	 * @return
	 */
	private boolean saveFileAndCompare(HttpServletRequest req) {
		try {
			logger.debug("saveFileAndCompare");
			final GZIPInputStream in = new GZIPInputStream(req.getInputStream());
			final String filename = System.getProperty("java.io.tmpdir")
					+ File.separator + System.currentTimeMillis() + ".txt";
			logger.debug(filename);
			final FileOutputStream out = new FileOutputStream(filename);
			final byte[] buffer = new byte[102400];
			int len = -1;
			while (-1 != (len = in.read(buffer))) {
				out.write(buffer, 0, len);
			}
			out.flush();
			out.close();
			uploadLocalFile.addTask(filename);
			logger.debug("saveFileAndCompare end");
		} catch (IOException e) {
			logger.error("", e);
			return false;
		}
		return true;
	}

}
