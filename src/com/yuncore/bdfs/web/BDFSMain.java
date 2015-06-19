/**
 * 
 */
package com.yuncore.bdfs.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.yuncore.bdfs.server.BDFSServer;
import com.yuncore.bdfs.tools.Util;

/**
 * @author ouyangfeng
 * 
 */
public class BDFSMain extends HttpServlet {

	Logger logger = Logger.getLogger(BDFSMain.class.getSimpleName());

	private static BDFSServer bdfsServer;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void startServer() {
		logger.warn("startServer");
		if (bdfsServer == null) {
			bdfsServer = new BDFSServer();
			bdfsServer.start();
		}
	}

	@Override
	public void init() throws ServletException {
		startServer();
		super.init();
	}

	public static void main(String[] args) {
		try {
			new BDFSMain().init();
		} catch (ServletException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		logger.debug("doGet");
		resp.setHeader("Content-Encoding", "gzip");
		final JSONObject object = new JSONObject();
		startServer();
		if(null != bdfsServer){
			object.put("code", 200);
			object.put("msg", "working");
		}else {
			object.put("code", 500);
			object.put("msg", "not start");
		}
		resp.setContentType("application/json;charset=UTF-8");
		resp.getOutputStream().write(Util.gzip(object.toString().getBytes()));
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doGet(req, resp);
	}

}
