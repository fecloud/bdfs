package com.yuncore.bdfs.servlet;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoadServlet extends HttpServlet {

	private boolean loadLibs;

	protected URL[] liburls;

	private static final long serialVersionUID = 1L;

	private String version;

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
					i(LoadServlet.class.getName() + " found lib:" + list.get(i));
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
		i(LoadServlet.class.getName() + " init");
		loadLibs();
		super.init();
	}

	protected void loadLibs() {
		getLibsURL();
		loadServlet();
		i(LoadServlet.class.getName() + " load jars");
	}

	protected void loadServlet() {
		if (null == liburls) {
			return;
		}
		final URLClassLoader classLoader = new URLClassLoader(liburls);
		try {
			final Class<?> loadClass = classLoader
					.loadClass("com.yuncore.bdfs.server.Version");
			final Method method = loadClass.getMethod("version");
			final Object object = loadClass.newInstance();
			version = method.invoke(object).toString();

			i(LoadServlet.class.getName() + " version " + version);
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
		final String ip = getRemortIP(req);
		final String result = String.format(
				"{\"ip\":\"%s\",\"load\":%s,\"version\":\"%s\"}", ip, loadLibs,
				version);
		resp.getWriter().println(result);
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
