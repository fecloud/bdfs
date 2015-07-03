package com.yuncore.dbpcs;

import com.yuncore.bdfs.server.files.local.UploadLocalFile;

import junit.framework.TestCase;

public class TT extends TestCase{

	private static UploadLocalFile uploadLocalFile = new UploadLocalFile();
	
	public void testTT() throws InterruptedException{
		final String filename = "E:\\cocos2d-x-3.3\\.bdsync\\localfiles";
		uploadLocalFile.addTask(filename);
		Thread.sleep(10000000);
	}
	
	
}
