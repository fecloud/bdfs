/**
 * @(#) ServiceCmd.java Created on Sep 9, 2015
 *
 * 
 */
package com.yuncore.bdfs;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import com.yuncore.bdfs.util.Log;

/**
 * The class <code>ServiceCmd</code>
 * 
 * @author Feng OuYang
 * @version 1.0
 */
public class BDFSService extends ShutDownBDFSService {

	private static final String TAG = "BDFSService";

	public BDFSServiceListener listener;

	public BDFSService(BDFSServiceListener listener) {
		this.listener = listener;
	}

	public synchronized void start() {
		final int port = Integer.parseInt(System.getProperty(SERVICE_PORT,
				"18081"));
		try {

			final ServerSocket serverSocket = new ServerSocket();
			serverSocket.bind(new InetSocketAddress("127.0.0.1", port));
			if (listener != null) {
				listener.onStart();
			}

			final Socket accept = serverSocket.accept();
			Log.w(TAG, "request shutdown service");
			if (null != listener) {
				listener.onStop();
			}

			accept.getOutputStream().write("1".getBytes("UTF-8"));
			accept.getOutputStream().flush();

			accept.close();
			Thread.sleep(1000);
			serverSocket.close();
			Log.w(TAG, "service exit");
			System.exit(0);
		} catch (Exception e) {
			System.exit(1);
		}
	}

	/**
	 * @return the listener
	 */
	public BDFSServiceListener getBDFSServiceListener() {
		return listener;
	}

	/**
	 * @param listener
	 *            the listener to set
	 */
	public void setBDFSServiceListener(BDFSServiceListener listener) {
		this.listener = listener;
	}

	/**
	 * 当收到请求关闭程序时 The class <code>ShutDownSystem</code>
	 * 
	 * @author Feng OuYang
	 * @version 1.0
	 */
	public interface BDFSServiceListener {

		void onStart();

		void onStop();

	}

}
