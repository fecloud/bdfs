package com.yuncore.bdfs.client.ctrl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONObject;

import com.yuncore.bdfs.client.ClientEnv;
import com.yuncore.bdfs.client.util.Log;

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
			response = new Response(HTTP_NOTFOUND, MIME_PLAINTEXT, "not found");
		}
		response.addHeader("Connection", "close");
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
			} else if (action.equals("threads")) {
				getThreads(object);
			} else if (action.equals("cpuinfo")) {
				object.put("code", 200);
				object.put("data", Runtime.getRuntime().availableProcessors());
			} else {
				object.put("code", 500);
				object.put("msg", "not support");
			}
			return new Response(HTTP_OK, MIME_JSON, object.toString());
		}
		return null;
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
		object.put("data", new String(arrayOutputStream.toByteArray()));
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
