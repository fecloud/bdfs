package com.yuncore.bdfs.servlet;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
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
					System.out.println(LoadServlet.class.getName()
							+ " found lib:" + list.get(i));
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
		System.out.println(LoadServlet.class.getName() + " init");
		loadLibs();
		super.init();
	}

	protected void loadLibs() {
		getLibsURL();
		loadServlet();
		System.out.println(LoadServlet.class.getName() + " load jars");
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
			final Object result = method.invoke(object);
			System.out.println(LoadServlet.class.getName() + " version "
					+ result);
			loadLibs = true;
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
		final String result = String.format("{\"ip\":\"%s\",\"load\":%s}", ip,
				loadLibs);
		resp.getWriter().println(result);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doGet(req, resp);
	}

}
