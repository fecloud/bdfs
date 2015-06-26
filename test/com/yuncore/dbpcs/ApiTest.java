//package com.yuncore.dbpcs;
//
//import java.io.File;
//import java.util.Map;
//
//import com.yuncore.bdfs.api.Api;
//import com.yuncore.bdfs.api.imple.ApiImple;
//import com.yuncore.bdfs.entity.CloudFile;
//import com.yuncore.bdfs.entity.CloudMkDirResult;
//import com.yuncore.bdfs.entity.CloudPageFile;
//import com.yuncore.bdfs.entity.CloudQuota;
//import com.yuncore.bdfs.entity.CloudRmResult;
//import com.yuncore.bdfs.tools.Util;
//
//public class ApiTest extends PCSTestCase {
//
//	Api api = new ApiImple();
//
//	public void test_quota() throws Exception {
//
//		CloudQuota quota = api.quota();
//		assertNotNull(quota);
//		System.out.println(String.format("total:%s free:%s used:%s",
//				Util.byteSizeToString(quota.getTotal()),
//				Util.byteSizeToString(quota.getFree()),
//				Util.byteSizeToString(quota.getUsed())));
//	}
//
//	public void test_list() throws Exception {
//
//		CloudPageFile pageFile = api.list("/", 1, 100);
//		assertNotNull(pageFile);
//		System.out.println(pageFile.getList());
//		System.out.println(pageFile.getList().size());
//	}
//
//	public void test_mkdir() throws Exception {
//
//		CloudMkDirResult mkdir = api.mkdir("/tt/新建文件夹/"
//				+ System.currentTimeMillis());
//		assertNotNull(mkdir);
//		System.out.println(mkdir);
//	}
//
//	public void test_rmdir() throws Exception {
//
//		String filename = "/tt/新建文件夹/" + System.currentTimeMillis();
//		CloudMkDirResult mkdir = api.mkdir(filename);
//		assertNotNull(mkdir);
//		System.out.println(mkdir);
//
//		filename = "/tt/新建文件夹/" + System.currentTimeMillis();
//		mkdir = api.mkdir(filename);
//		assertNotNull(mkdir);
//		System.out.println(mkdir);
//
//		CloudRmResult rm = api.rm(filename);
//		assertNotNull(rm);
//		System.out.println(rm);
//	}
//
//	public void testEnv() {
//		System.getProperties().list(System.out);
//	}
//
//	public void testVISTBD() {
//		// PCSHttp http = new PCSHttp("https://www.baidu.com", Method.GET);
//		// try {
//		// http.exec();
//		// } catch (MalformedURLException e) {
//		// // TODO Auto-generated catch block
//		// e.printStackTrace();
//		// } catch (IOException e) {
//		// // TODO Auto-generated catch block
//		// e.printStackTrace();
//		// }
//	}
//
//	public void test_diskHomePage() throws Exception {
//
//		Map<String, String> diskHomePage = api.diskHomePage();
//		assertNotNull(diskHomePage);
//		System.out.println(diskHomePage);
//
//	}
//
//	// public void testDownload() {
//	// final String filename =
//	// "/SWEETY -樱花草-MV在线观看-高清MV-MTV歌曲-歌词-下载-音悦台-看好音乐_mp4_320x240_普通质量.mp4";
//	// api.download(Util.urlEncode(filename), new PCSDownloadFile() {
//	//
//	// @Override
//	// public String savepath(lonMap<String, List<String>> header) {
//	// final File file = new File(filename);
//	// return file.getName();
//	// }
//	//
//	// @Override
//	// public void downloadProcces(String filename, long sum, long finish,
//	// long buffer) {
//	// System.out.println(String.format(
//	// "filename:%s sum:%s finish:%s buffer:%s", filename,
//	// sum, finish, buffer));
//	//
//	// }
//	//
//	// @Override
//	// public String getTempPath(Map<String, List<String>> header) {
//	// return savepath(header) + ".temp";
//	// }
//	//
//	// @Override
//	// public void downloadError(Exception e) {
//	//
//	// }
//	//
//	// @Override
//	// public void saveResult(boolean success) {
//	// System.out.println(String.format("saveResult:%s", success));
//	// }
//	//
//	// });
//	//
//	// }
//
//	public void testDownDir() throws Exception {
//
//		testDownloadDir("/");
//
//	}
//
//	public void testDownloadDir(final String f) throws Exception {
//		CloudPageFile pageFile = api.list(f);
//		if (null != pageFile && !pageFile.getList().isEmpty()) {
//			for (CloudFile p : pageFile.getList()) {
//				// if (p.getIsdir() > 0) {
//				// testDownloadDir(p.getPath());
//				// } else {
//				// testDownloadFile(p.getPath());
//				// }
//			}
//		}
//	}
//
//	public void testDownloadFile(final String f) {
//		// api.download(Util.urlEncode(f), new PCSDownloadFile() {
//		//
//		// @Override
//		// public String getTempPath(long filesiz,
//		// Map<String, List<String>> header) {
//		// return null;
//		// }
//		//
//		// @Override
//		// public String savepath(long filesiz,
//		// Map<String, List<String>> header) {
//		// // TODO Auto-generated method stub
//		// return null;
//		// }
//		//
//		// @Override
//		// public void saveResult(boolean success) {
//		// // TODO Auto-generated method stub
//		//
//		// }
//		//
//		// @Override
//		// public void downloadProcces(String filename, long sum, long finish,
//		// long buffer) {
//		// // TODO Auto-generated method stub
//		//
//		// }
//		//
//		// @Override
//		// public void downloadError(Exception e) {
//		// // TODO Auto-generated method stub
//		//
//		// }
//		//
//		// });
//
//	}
//
//	public void testbyteSizeToString() {
//
//		File file = new File("C:\\\\Users\\\\wjh\\\\Downloads");
//		File[] listFiles = file.listFiles();
//		for (File f : listFiles) {
//			if (f.isFile())
//				System.out.println(String.format("name:%s size:%s",
//						f.getName(), Util.byteSizeToString(f.length())));
//		}
//
//	}
//
//	public void testrecyclebin() throws Exception {
//
//		CloudPageFile recyclebin = api.recyclebin();
//		assertNotNull(recyclebin);
//		System.out.println(recyclebin.getList().size());
//		for (CloudFile f : recyclebin.getList()) {
//			System.out.println(f.getAbsolutePath() + " " + f);
//		}
//	}
//
//	public void testSearch() throws Exception {
//
//		CloudPageFile search = api.search("/驱动");
//		assertNotNull(search);
//		System.out.println(search);
//
//	}
//
//	public void testSearchAll() throws Exception {
//
//		CloudPageFile search = api.searchAll("Arduino");
//		assertNotNull(search);
//		System.out.println(search.getList().size());
//
//	}
//
//	public void testIslogin() throws Exception {
//
//		boolean islogin = api.islogin();
//		assertEquals(true, islogin);
//	}
//
//	public void testlogin() throws Exception {
//
//		boolean login = api.login("3huhai", "ouyangfeng7758");
//		assertEquals(true, login);
//
//		boolean islogin = api.islogin();
//		assertEquals(true, islogin);
//	}
//
//	public void testwho() throws Exception {
//
//		String who = api.who();
//		assertEquals(who, "3huhai");
//	}
//
//	public void testlogout() throws Exception {
//
//		boolean logout = api.logout();
//		assertEquals(logout, "3huhai");
//	}
//
//	public void testfileExists() throws Exception {
//
//		CloudFile fileExists = api.fileExists("/photos");
//		assertNotNull(fileExists);
//		System.out.println(fileExists);
//	}
//
//	public void testupload() throws Exception {
//
//		String upload = api.upload(
//				"/Users/ouyangfeng/Downloads/LiveSuitV305_For_MacOSX.zip", "/");
//		assertNotNull(upload);
//		System.out.println(upload);
//	}
//
//}
