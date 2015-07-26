/**
 * 
 */
package com.yuncore.bdfs.http;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

/**
 * @author ouyangfeng
 * 
 */
public class HttpFormOutput extends Http {

	private String file;

	private String filestart;
	
	private String fileend;
	
	private long filesize;
	
	private String BOUNDARY = "---------------------------"
			+ new Random().nextLong();

	public HttpFormOutput(String url, String file) {
		super(url, Method.POST);
		this.file = file;
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yuncore.dbpcs.http.PCSHttp#addRequestProperty()
	 */
	@Override
	protected void addRequestProperty() {
		conn.setRequestProperty("Content-Type",
				"multipart/form-data; boundary=" + BOUNDARY);
		conn.setFixedLengthStreamingMode(getInputSize());
	}
	
	protected int getInputSize() {
		
		final File filename = new File(file);
		filesize = filename.length();
		
		StringBuffer strBuf = new StringBuffer();
		strBuf.append("\r\n").append("--").append(BOUNDARY).append("\r\n");
		strBuf.append("Content-Disposition: form-data; name=\"file\"; filename=\""
				+ filename.getName() + "\"\r\n");
		strBuf.append("Content-Type:application/octet-stream\r\n\r\n");
		
		filestart = strBuf.toString();
		
		fileend = ("\r\n--" + BOUNDARY + "--\r\n");
		
		filesize += filestart.getBytes().length;
		filesize += fileend.getBytes().length;
		return (int) filesize;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yuncore.dbpcs.http.PCSHttp#addFormData()
	 */
	@Override
	protected boolean addFormData() throws IOException {
		OutputStream out = new DataOutputStream(conn.getOutputStream());  
		out.write(filestart.getBytes());
		FileInputStream in = new FileInputStream(file);
		int bytes = 0;
		byte[] bufferOut = new byte[1024 * 512];
		while ((bytes = in.read(bufferOut)) != -1) {
			out.write(bufferOut, 0, bytes);
			out.flush();
		}
		in.close();
		out.write(fileend.getBytes());
		out.flush();
		out.close();
		return true;
	}
}
