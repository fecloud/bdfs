package com.yuncore.bdfs.server;

import java.io.InputStream;
import java.util.Map;

public class WebExecute {

	public void onCreate() {
		System.out.println("onCreate");
	}

	public String onRequest(Map<String, String> params,InputStream body) {
		System.out.println("onRequest");
		return "13131";
	}

}
