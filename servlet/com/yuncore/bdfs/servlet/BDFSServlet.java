package com.yuncore.bdfs.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BDFSServlet extends HttpServlet {

	private boolean loadLibs;

	protected URL[] liburls;

	private static final long serialVersionUID = 1L;

	private String version;

	private Method onRequest;

	private Object executeObject;

	protected void getLibsURL() {
		try {
			final Enumeration<?> initParameterNames = getInitParameterNames();

			final List<String> list = new ArrayList<String>();
			if (initParameterNames != null) {
				String name = null;
				while (initParameterNames.hasMoreElements()) {
					name = initParameterNames.nextElement().toString();
					if (name.startsWith("lib")) {
						list.add(getInitParameter(name));
					}
				}
			}

			if (!list.isEmpty()) {

				liburls = new URL[list.size()];
				for (int i = 0; i < list.size(); i++) {
					i(BDFSServlet.class.getName() + " found lib:" + list.get(i));
					liburls[i] = new URL(list.get(i));
				}
			}
		} catch (MalformedURLException e) {
			liburls = null;
			e.printStackTrace();
		}

	}

	@Override
	public void init() throws ServletException {
		i(BDFSServlet.class.getName() + " init");
		loadLibs();
		super.init();
	}

	protected void loadLibs() {
		getLibsURL();
		loadServlet();
		i(BDFSServlet.class.getName() + " load jars");
	}

	protected void loadServlet() {
		if (null == liburls || loadLibs) {
			return;
		}

		final URLClassLoader classLoader = new URLClassLoader(liburls);
		try {
			final Class<?> versionClass = classLoader
					.loadClass("com.yuncore.bdfs.server.Version");
			final Method method = versionClass.getMethod("version");
			final Object object = versionClass.newInstance();
			version = method.invoke(object).toString();

			i(BDFSServlet.class.getName() + " version " + version);

			final Class<?> execClass = classLoader
					.loadClass("com.yuncore.bdfs.server.WebExecute");

			final Method init = execClass.getMethod("onCreate");

			executeObject = execClass.newInstance();

			init.invoke(executeObject);

			onRequest = execClass.getMethod("onRequest", Map.class,
					InputStream.class);

			loadLibs = true;
			classLoader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public String getRemortIP(HttpServletRequest request) {
		if (request.getHeader("x-forwarded-for") == null) {
			return request.getRemoteAddr();
		}
		return request.getHeader("x-forwarded-for");
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.setContentType("application/json;charset=UTF-8");
		final String action = req.getParameter("action");
		if (null == action || action.trim().length() == 0) {
			final String ip = getRemortIP(req);
			final String result = String.format(
					"{\"ip\":\"%s\",\"load\":%s,\"version\":\"%s\"}", ip,
					loadLibs, version);
			resp.getWriter().println(result);
		} else {
			// 交给代理处理
			try {
				final Object result = onRequest.invoke(executeObject,
						req.getParameterMap(), req.getInputStream());
				resp.getWriter().println(result);
			} catch (Exception e) {
				throw new IOException(e);
			}
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doGet(req, resp);
	}

	public void i(String msg) {
		final StringBuilder builder = new StringBuilder();
		final SimpleDateFormat format = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss,SSS");
		builder.append(format.format(new Date())).append(" ");
		builder.append(msg);
		System.out.println(builder.toString());
	}

}
