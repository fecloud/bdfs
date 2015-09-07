package com.yuncore.bdfs.ctrl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.json.JSONArray;
import org.json.JSONObject;

import com.yuncore.bdfs.ClientEnv;
import com.yuncore.bdfs.entity.EntityJSON;
import com.yuncore.bdfs.util.Gzip;
import com.yuncore.bdfs.util.Log;

public class Httpd extends NanoHTTPd {

	private static final String TAG = "Httpd";

	public Httpd(int port) throws IOException {
		super(port);
		Log.w(TAG, "start httpd port:" + port);
	}

	@Override
	public Response serve(String uri, String method, Properties header,
			Properties parms, Properties files) {
		Response response = dispath(uri, method, header, parms, files);
		if (response == null) {
			response = new Response(HTTP_NOTFOUND, MIME_PLAINTEXT,
					Gzip.gzip("not found".getBytes()));
		}
		response.addHeader("Connection", "close");
		response.addHeader("Content-Encoding", "gzip");
		return response;
	}

	/**
	 * 处理httpy请求
	 * 
	 * @param uri
	 * @param method
	 * @param header
	 * @param parms
	 * @param files
	 * @return
	 */
	private Response dispath(String uri, String method, Properties header,
			Properties parms, Properties files) {
		if (null != parms) {
			final String action = parms.getProperty("action", "");
			final JSONObject object = new JSONObject();
			if (action.equalsIgnoreCase("env")) {
				printEnv(object);
			} else if (action.equalsIgnoreCase("clientEnv")) {
				printClientEnv(object);
			} else if (action.equalsIgnoreCase("threads")) {
				getThreads(object);
			} else if (action.equalsIgnoreCase("cpuinfo")) {
				object.put("code", 200);
				object.put("data", Runtime.getRuntime().availableProcessors());

			} else if (action.equalsIgnoreCase("status")) {
				object.put("code", 200);
			} else {
				object.put("code", 500);
				object.put("msg", "not support");
			}
			try {
				return new Response(HTTP_OK, MIME_JSON, Gzip.gzip(object
						.toString().getBytes("UTF-8")));
			} catch (UnsupportedEncodingException e) {
			}
		}
		return null;
	}

	/**
	 * 打印环境变量
	 * 
	 * @param object
	 */
	private void printEnv(JSONObject object) {
		final Properties properties = System.getProperties();
		final Enumeration<Object> keys = properties.keys();
		JSONObject jsonObject = new JSONObject();
		Object key = null;
		Object value = null;
		JSONObject item = null;
		while (keys.hasMoreElements()) {
			key = keys.nextElement();
			value = properties.get(key);
			if (value instanceof EntityJSON) {
				item = new JSONObject();
				((EntityJSON) value).toJSON(item);
				jsonObject.put(key.toString(), item);
			} else {
				jsonObject.put(key.toString(), value);
			}
		}
		object.put("code", 200);
		object.put("env", jsonObject);
	}

	/**
	 * 打印环境变量
	 * 
	 * @param object
	 */
	private void printClientEnv(JSONObject object) {
		final JSONObject env = ClientEnv.listJson();
		object.put("code", 200);
		object.put("data", env);
	}

	/**
	 * 取当前进程里的所有线程
	 * 
	 * @param object
	 */
	private void getThreads(JSONObject object) {
		object.put("code", 200);
		final List<String> list = getAllThreads();
		Collections.sort(list);
		if (null != list) {
			final JSONArray array = new JSONArray();
			for (String s : list) {
				array.put(s);
			}
			object.put("data", array);
		}
	}

	private static List<String> getAllThreads() {
		final List<String> list = new ArrayList<String>();
		Map<Thread, StackTraceElement[]> maps = Thread.getAllStackTraces();
		Iterator<Entry<Thread, StackTraceElement[]>> entrySet = maps.entrySet()
				.iterator();
		Entry<Thread, StackTraceElement[]> next = null;
		while (entrySet.hasNext()) {
			next = entrySet.next();
			list.add(next.getKey().getName());
		}
		return list;
	}

	public static void main(String[] args) {
		try {
			new Httpd(8080);
			Thread.sleep(1000000);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
