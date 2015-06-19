/**
 * 
 */
package com.yuncore.dbpcs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import junit.framework.TestCase;

import com.yuncore.bdfs.Const;
import com.yuncore.bdfs.api.Api;
import com.yuncore.bdfs.api.ServerApi;
import com.yuncore.bdfs.api.imple.ApiImple;
import com.yuncore.bdfs.api.imple.ServerApiImple;
import com.yuncore.bdfs.app.imple.ClientContextImle;
import com.yuncore.bdfs.entity.CloudFile;
import com.yuncore.bdfs.http.cookie.FileCookieContainer;
import com.yuncore.bdfs.tools.DownloadInputStream;

/**
 * @author ouyangfeng
 *
 */
public class ServerApiTest extends TestCase {

	private String syncdir = "/Users/ouyangfeng/Downloads";

	private ServerApi serverApi;

	private Api api;

	private void setEnv() {
		System.setProperty(Const.SYNCDIR, syncdir);
		System.setProperty(Const.DATA, String.format("%s%s%s", syncdir,
				File.separator, Const.DATA_DIR));
		// System.setProperty("http_proxy", "localhost:8888");
		System.setProperty(Const.TMP, String.format("%s%s%s",
				System.getProperty(Const.DATA), File.separator, Const.TMP));
		System.setProperty(Const.CONTEXT, ClientContextImle.class.getName());
		System.setProperty(Const.COOKIECONTAINER,
				FileCookieContainer.class.getName());

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		setEnv();
		serverApi = new ServerApiImple();
		api = new ApiImple();
		super.setUp();
	}

	public void testGetDownload() throws Exception {
		while (true) {
			CloudFile download = serverApi.getDownload();
			assertNotNull(download);
			System.out.println(download);

			if (download.isFile()) {
				DownloadInputStream in = api.download(download);
				final byte[] buffer = new byte[1024];
				int len = -1;
				FileOutputStream stream = new FileOutputStream(
						download.getName());
				while (-1 != (len = in.read(buffer))) {
					stream.write(buffer, 0, len);
				}
				in.close();
				stream.flush();
				stream.close();

			}
			boolean deldownload = serverApi.deldownload(download.getId());
			assertEquals(deldownload, true);
		}

	}

}
