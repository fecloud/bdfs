package com.yuncore.dbpcs;

import java.io.File;

import junit.framework.TestCase;

public class PCSTestCase extends TestCase {

	static {
		
		System.setProperty("syncdir", "/Users/ouyangfeng/tmp");
		System.setProperty("username", "3huhai");
		System.setProperty("password", "ouyangfeng7758");
//		System.setProperty("http_proxy", "localhost:8888");
		System.setProperty("recycle", String.format("%s%s%s", System.getProperty("syncdir"),File.separator,"recycle"));
		
	}
	
}
