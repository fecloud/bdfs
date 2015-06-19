package com.yuncore.bdfs.api.imple;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public final class BDfsURL {

	public static final String APPID = "250528";
	/**
	 * 查询容量url
	 * 
	 * @param time
	 * @param bdstoken
	 *            http://pan.baidu.com/api/quota?checkexpire=1&checkfree=1&_=
	 *            1430116954276&bdstoken=9608e7d
	 *            11ef485da756a554a64092c0d&channel=chunlei&clienttype=0&web=1&app_id=25052
	 *            8
	 * @return
	 */
	public static String quota(long time, String bdstoken) {
		final String ex_url = "http://pan.baidu.com/api/quota?checkexpire=1&checkfree=1&_=%s&bdstoken=%s&channel=chunlei&clienttype=0&web=1&app_id=%s";
		return String.format(ex_url, time, bdstoken, APPID);
	}

	/**
	 * 文件列表url
	 * 
	 * @param page
	 * @param dir
	 * @param time
	 * @param bdstoken
	 *            http://pan.baidu.com/api/list?channel=chunlei&clienttype=0&web
	 *            =1&num=100&page=1&dir=%2F%E6%96%B0%E5%BB%BA%E6%96%87%E4%BB%B6%
	 *            E5
	 *            %A4%B9&order=time&desc=1&showempty=0&_=1430121832330&bdstoken
	 *            =9608e7d
	 *            11ef485da756a554a64092c0d&channel=chunlei&clienttype=0&web=1&app_id=25052
	 *            8
	 * @return
	 */
	public static String list(int page, int page_num, String dir, long time,
			String bdstoken) {
		final String ex_url = "http://pan.baidu.com/api/list?channel=chunlei&clienttype=0&web=1&num=%s&page=%s&dir=%s&order=time&desc=1&showempty=0&_=%s&bdstoken=%s&channel=chunlei&clienttype=0&web=1&app_id=%s";
		try {
			final String result = String.format(ex_url, page_num, page,
					URLEncoder.encode(dir, "UTF-8"), time, bdstoken, APPID);
			return result;
		} catch (UnsupportedEncodingException e) {
			
		}
		return "";
	}

	/**
	 * 建立文件夹
	 * 
	 * @param bdstoken
	 *            http://pan.baidu.com/api/create?a=commit&bdstoken=6f
	 *            1ec5596e1dc4bbd656f69976bbbd64&channel=chunlei&clienttype=0&web=1&app_id=25052
	 *            8
	 * @return
	 */

	public static String mkdir(String bdstoken) {
		final String ex_url = "http://pan.baidu.com/api/create?a=commit&bdstoken=%s&channel=chunlei&clienttype=0&web=1&app_id=%s";
		return String.format(ex_url, bdstoken, APPID);
	}

	/**
	 * 删除文件或文件夹
	 * 
	 * @param bdstoken
	 *            http://pan.baidu.com/api/filemanager?channel=chunlei&
	 *            clienttype=0&web=1&opera=delete&async=2&bdstoken=6f
	 *            1ec5596e1dc4bbd656f69976bbbd64&channel=chunlei&clienttype=0&web=1&app_id=25052
	 *            8
	 * @return
	 */
	public static String rm(String bdstoken) {
		final String ex_url = "http://pan.baidu.com/api/filemanager?channel=chunlei&clienttype=0&web=1&opera=delete&async=2&bdstoken=%s&channel=chunlei&clienttype=0&web=1&app_id=%s";
		return String.format(ex_url, bdstoken, APPID);
	}

	/**
	 * 到page 取参数
	 */
	public static String diskHomePage() {
		return "http://pan.baidu.com/disk/home";
	}

	/**
	 * 取下载前的链接
	 * 
	 * @param sign
	 * @param time
	 * @param filelist
	 * @param bdstoken
	 * @return
	 */
	public static String downloadPre(String sign, long time, String filelist,
			String bdstoken) {
		final String ex_url = "http://pan.baidu.com/api/download?sign=%s&timestamp=%s&fidlist=%s&type=dlink&bdstoken=%s&channel=chunlei&clienttype=0&web=1&app_id=%s";
		return String.format(ex_url, sign, time, filelist, bdstoken, APPID);
	}

	/**
	 * 取文件下载地址
	 * 
	 * @param path
	 *            http://c.pcs.baidu.com/rest/2.0/pcs/file?method=download&
	 *            app_id=250528&path=%2Ftt%2Ftest.js
	 * @return
	 */
	public static String download(String path) {
		try{
			final String ex_url = "http://c.pcs.baidu.com/rest/2.0/pcs/file?method=download&app_id=%s&path=%s";
			return String.format(ex_url, APPID, URLEncoder.encode(path, "UTF-8"));
		}catch(UnsupportedEncodingException e){
			return "";
		}
	}

	/**
	 * 
	 * @param time
	 * @param filename
	 *            http://pan.baidu.com/api/search?channel=chunlei&clienttype=0&
	 *            web
	 *            =1&num=100&page=1&dir=&order=time&desc=1&showempty=0&key=test
	 *            &searchPath=null&recursion=1&_=1430375612447&bdstoken=6f
	 *            1ec5596e1dc4bbd656f69976bbbd64&channel=chunlei&clienttype=0&web=1&app_id=25052
	 *            8
	 * @return
	 */
	public static String search(int page, int page_num, String dir, long time,
			String filename, String bdstoken) {
		final String ex_url = "http://pan.baidu.com/api/search?channel=chunlei&clienttype=0&web=1&num=%s&page=%s&dir=%s&order=time&desc=1&showempty=0&key=%s&searchPath=null&recursion=1&_=%s&bdstoken=%s&channel=chunlei&clienttype=0&web=1&app_id=%s";
		return String.format(ex_url, page_num, page, dir, filename, time,
				bdstoken, APPID);
	}

	/**
	 * 取回收站里面文件
	 * 
	 * @param page
	 * @param page_num
	 * @param time
	 * @param bdstokenhttp
	 *            ://pan.baidu.com/api/recycle/list?channel=chunlei&clienttype=0
	 *            &web=1&num=100&page=1&order=time&desc=1&_=1430372511177&
	 *            bdstoken=6f
	 *            1ec5596e1dc4bbd656f69976bbbd64&channel=chunlei&clienttype=0&web=1&app_id=25052
	 *            8
	 * @return
	 */
	public static String recyclebin(int page, int page_num, long time,
			String bdstoken) {
		final String ex_url = "http://pan.baidu.com/api/recycle/list?channel=chunlei&clienttype=0&web=1&num=%s&page=%s&order=time&desc=1&_=%s&bdstoken=%s&channel=chunlei&clienttype=0&web=1&app_id=%s";
		return String.format(ex_url, page_num, page, time, bdstoken, APPID);
	}

	/**
	 * 
	 * @param time
	 * @return
	 */
	public static String getpassport(long time) {
		final String ex_url = "https://passport.baidu.com/v2/api/?getapi&tpl=netdisk&apiver=v3&tt=%s&class=login&logintype=basicLogin&callback=bd__cbs__nflaog";
		return String.format(ex_url, time);
	}

	public static String getlogincheck(String token, long time, String username) {
		final String ex_url = "https://passport.baidu.com/v2/api/?logincheck&token=%s&tpl=netdisk&apiver=v3&tt=%s&username=%s&isphone=false&callback=bd__cbs__q4ztud";
		return String.format(ex_url, token, time, username);
	}

	public static String getpublickey(String token, long time) {
		final String ex_url = "https://passport.baidu.com/v2/getpublickey?&token=%s&tpl=netdisk&apiver=v3&tt=%s&callback=bd__cbs__wl95ks";
		return String.format(ex_url, token, time);
	}

	public static String getloginurl() {
		return "https://passport.baidu.com/v2/api/?login";
	}

	public static String getfileexists(long time, String bdstoken, String dir,
			String filename) {
		final String ex_url = "http://pan.baidu.com/api/search?channel=chunlei&clienttype=0&web=1&t=%s&bdstoken=%s&dir=%s&key=%s";
		return String.format(ex_url, time, bdstoken, dir, filename);
	}

	/**
	 * 上传文件
	 * http://c.pcs.baidu.com/rest/2.0/pcs/file?method=upload&app_id=250528&
	 * ondup=overwrite&dir=%2F&filename=libhttpd-1.4.tar.gz&BDUSS=
	 * FVOMUZJcy0zSHpaVWVsZExKUTE4YnlUa2U5U0FBQ3hGLUYzVm92Vk5kbX5nNE5WQVFBQUFBJCQAAAAAAAAAAAEAAAApOaIBM2h1aGFpAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAL
	 * ~2W1W~9ltVV
	 * 
	 * @return
	 */
	public static String getuploadfile(String dir, String filename, String BDUSS) {
		final String ex_url = "http://c.pcs.baidu.com/rest/2.0/pcs/file?method=upload&app_id=%s&ondup=overwrite&dir=%s&filename=%s&BDUSS=%s";
		return String.format(ex_url, APPID, dir, filename, BDUSS);
	}
}
