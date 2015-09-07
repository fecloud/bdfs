package com.yuncore.bdfs.cookie;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

import com.yuncore.bdfs.Environment;
import com.yuncore.bdfs.api.FSApi;
import com.yuncore.bdfs.api.imple.FSApiImple;
import com.yuncore.bdfs.exception.ApiException;
import com.yuncore.bdfs.util.FileGzip;

public class LoginGetCookie {

	private boolean uploadCookie;

	private String username;

	private String password;

	private String filename = Environment.getTmpDir()
			+ File.separator + "cookie.json";

	private FSApi api = new FSApiImple();

	public LoginGetCookie(boolean uploadCookie, String username, String password) {
		super();
		this.uploadCookie = uploadCookie;
		this.username = username;
		this.password = password;
	}

	public boolean getCookie() {

		try {
			if (api.login(username, password)) {
				final String cookie = readFile();
				if (cookie != null) {
					System.out.println("result cookie:");
					System.out.println(cookie);

					if (uploadCookie) {
						if (uploadCookie()) {
							System.out.println("upload cookie success");
						} else {
							System.err.println("upload cookie fail");
						}
					}
				} else {
					System.err.println("not found cookie");
				}
			}
		} catch (ApiException e) {
			System.err.println("login error");
		}
		return false;
	}

	/**
	 * 上传cookie
	 */
	private boolean uploadCookie() {
		try {
			final String file = filename + ".gzip";
			final FileGzip fileZip = new FileGzip(filename, file);
			if (fileZip.gzip()) {
				//return serverApi.uploadCookie(file);
			}
		} catch (Exception e) {
		}
		return false;
	}

	public synchronized String readFile() {
		final File file = new File(filename);
		try {
			if (file.exists()) {
				final byte[] buff = new byte[1024];
				int len = 0;
				final ByteArrayOutputStream out = new ByteArrayOutputStream();
				final FileInputStream in = new FileInputStream(file);
				while (-1 != (len = in.read(buff))) {
					out.write(buff, 0, len);
				}
				in.close();
				return new String(out.toByteArray(), "UTF-8");
			}

		} catch (Exception e) {
			System.err.println("readFile fail" + "\n" + e.getMessage());
		}
		return null;
	}

}
