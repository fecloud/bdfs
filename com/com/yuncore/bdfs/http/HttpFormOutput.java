/**
 * 
 */
package com.yuncore.bdfs.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
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

	private OutputDataListener listener;

	public HttpFormOutput(String url, String file) {
		super(url, Method.POST);
		this.file = file;
	}

	public HttpFormOutput(String url, String file, OutputDataListener listener) {
		this(url, file);
		this.listener = listener;
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

	/**
	 * 取文件内容大小
	 * 
	 * @return
	 */
	protected int getInputSize() {

		try {
			final File filename = new File(file);
			filesize = filename.length();

			StringBuffer strBuf = new StringBuffer();
			strBuf.append("\r\n").append("--").append(BOUNDARY).append("\r\n");
			strBuf.append("Content-Disposition: form-data; name=\"file\"; filename=\""
					+ filename.getName() + "\"\r\n");
			strBuf.append("Content-Type:application/octet-stream\r\n\r\n");

			filestart = strBuf.toString();

			fileend = ("\r\n--" + BOUNDARY + "--\r\n");

			filesize += filestart.getBytes("UTF-8").length;
			filesize += fileend.getBytes("UTF-8").length;
		} catch (UnsupportedEncodingException e) {
		}
		return (int) filesize;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yuncore.dbpcs.http.PCSHttp#addFormData()
	 */
	@Override
	protected boolean addFormData() throws IOException {
		final OutputStream out = conn.getOutputStream();
		out.write(filestart.getBytes("UTF-8"));
		final File infile = new File(file);
		final FileInputStream in = new FileInputStream(infile);
		long commit = 0;
		int len = 0;
		// 缓存大小
		final byte[] buffer = new byte[1024 * 50];
		while ((len = in.read(buffer)) != -1) {
			out.write(buffer, 0, len);
			out.flush();
			commit += len;
			if(null != listener){
				listener.onWrite(infile.length(), commit);
			}
		}
		in.close();
		out.write(fileend.getBytes("UTF-8"));
		out.flush();
		return true;
	}

	/**
	 * 写出数据监听
	 * 
	 * @author wjh
	 *
	 */
	public interface OutputDataListener {

		/**
		 * 
		 * @param sum
		 *            总长度
		 * @param commit
		 *            已完成长度
		 */
		void onWrite(long sum, long commit);

	}

}
