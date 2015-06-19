package com.yuncore.bdfs.api.imple;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.yuncore.bdfs.Const;
import com.yuncore.bdfs.api.Api;
import com.yuncore.bdfs.app.Context;
import com.yuncore.bdfs.entity.CloudFile;
import com.yuncore.bdfs.entity.CloudMkDirResult;
import com.yuncore.bdfs.entity.CloudPageFile;
import com.yuncore.bdfs.entity.CloudQuota;
import com.yuncore.bdfs.entity.CloudRmResult;
import com.yuncore.bdfs.exception.ApiException;
import com.yuncore.bdfs.http.Http;
import com.yuncore.bdfs.http.Http.Method;
import com.yuncore.bdfs.http.HttpFormOutput;
import com.yuncore.bdfs.http.HttpInput;
import com.yuncore.bdfs.http.cookie.HttpCookieContainer;
import com.yuncore.bdfs.tools.CloudObject;
import com.yuncore.bdfs.tools.DownloadInputStream;
import com.yuncore.bdfs.tools.Util;

public class ApiImple implements Api {

	protected Logger logger = Logger.getLogger(ApiImple.class.getSimpleName());

	private static Context context;

	final static String TOKEN_KEY = "MYBDSTOKEN";

	private final boolean DEBUG = true;

	/**
	 * 每页数量
	 */
	private final int PAGESIZE = 2000;

	public ApiImple() {
		super();
		inStanceContext();
	}

	private void inStanceContext() {
		try {
			context = (Context) Class
					.forName(System.getProperty(Const.CONTEXT)).newInstance();
		} catch (Exception e) {
			throw new RuntimeException("not set " + Const.CONTEXT);
		}
	}

	private String getpassport() {
		try {
			final long c_time = Util.current_time_ss();
			final String url = BDfsURL.getpassport(c_time);
			final Http http = new Http(url, Method.GET);
			if (http.http()) {
				return http.result();
			}
		} catch (Exception e) {
			logger.error("getpassport error", e);
		}
		return null;
	}

	private boolean getbaidu() {
		try {
			final String url = "http://www.baidu.com";
			final Http http = new Http(url, Method.GET);
			if (http.http()) {
				if (http.getResponseCode() == HttpsURLConnection.HTTP_MOVED_TEMP) {
					String location = http.getConnet().getHeaderField(
							"Location");
					logger.debug("location:" + location);
					if (location != null) {
						final Http httpsbaidu = new Http(location, Method.GET);
						if (httpsbaidu.http()) {
							return true;
						}
					}
				} else if (http.getResponseCode() == HttpsURLConnection.HTTP_OK) {
					return true;
				}
			}
		} catch (Exception e) {
			logger.error("vistWWWBAIDUCOM error", e);
		}
		return false;
	}

	private String logincheck(String token, String username) {
		try {
			final long c_time = Util.current_time_ss();
			final String url = BDfsURL.getlogincheck(token, c_time, username);
			final Http http = new Http(url, Method.GET);
			if (http.http()) {
				return http.result();
			}
		} catch (Exception e) {
			logger.error("logincheck error", e);
		}
		return null;
	}

	private String getpublickey(String token, String username) {
		try {
			final long c_time = Util.current_time_ss();
			final String url = BDfsURL.getpublickey(token, c_time);
			final Http http = new Http(url, Method.GET);
			if (http.http()) {
				return http.result();
			}
		} catch (Exception e) {
			logger.error("logincheck error", e);
		}
		return null;
	}

	private String buildloginform(String token, long time, String username,
			String password) {
		String ex = "staticpage=http://pan.baidu.com/res/static/thirdparty/pass_v3_jump.html&charset=utf-8&token=%s&tpl=netdisk&subpro=&apiver=v3&tt=%s&codestring=&safeflg=0&u=http://pan.baidu.com/&isPhone=&quick_user=0&logintype=basicLogin&logLoginType=pc_loginBasic&idc=&loginmerge=true&username=%s&password=%s&verifycode=&mem_pass=on&rsakey=&crypttype=&ppui_logintime=2602&callback=parent.bd__pcbs__msdlhs";
		return String.format(ex, token, time, username, password);
	}

	private String passportlogin(String token, String username, String password) {
		final String url = BDfsURL.getloginurl();
		final long c_time = Util.current_time_ss();

		Http http = new Http(url, Method.POST, buildloginform(token, c_time,
				username, password));
		try {
			if (http.http()) {
				return http.result();
			}
		} catch (IOException e) {
			logger.error("passportlogin error", e);
		}
		return null;
	}

	@Override
	public boolean login(String username, String password) throws ApiException {
		// 1.判断是否登录
		if (islogin()) {
			return true;
		}

		if (!getbaidu()) {
			return false;
		}

		// 2.得到cookie
		String passport = getpassport();

		if (passport == null) {
			return false;
		}

		passport = passport.replace("bd__cbs__nflaog(", "");
		passport = passport.substring(0, passport.length() - 1);
		if (DEBUG)
			logger.debug("passport:\n" + passport);
		JSONObject object = new JSONObject(passport);
		String token = null;

		String logincheck = null;
		if (object.has("data")) {
			token = object.getJSONObject("data").getString("token");
			if (object.getJSONObject("data").has("codeString")) {
				logger.warn("login need codeString");
			}
			logincheck = logincheck(token, username);
		} else {
			return false;
		}

		if (null == logincheck)
			return false;

		String publickey = getpublickey(token, username);
		if (null == publickey)
			return false;

		publickey = publickey.replace("bd__cbs__wl95ks(", "");
		publickey = publickey.substring(0, publickey.length() - 1);

		String publickey_keystring = new JSONObject(publickey)
				.getString("pubkey");
		String publickey_key = new JSONObject(publickey).getString("key");
		if (DEBUG)
			logger.debug(String.format(
					"publickey_keystring:%s\npublickey_key:%s",
					publickey_keystring, publickey_key));

		String loginresult = passportlogin(token, username, password);
		if (null == loginresult) {
			return false;
		}
		if (DEBUG)
			logger.debug("loginresult :" + loginresult);

		return true;
	}

	@Override
	public boolean logout() {
		final Http http = new Http("clear", Method.GET);
		return http.clearCookie();
	}

	@Override
	public boolean chdir(String dir) {
		return false;
	}

	@Override
	public String pwddir() {
		return null;
	}

	@Override
	public CloudPageFile list(String dir) throws ApiException {
		int i = 1;
		CloudPageFile file = new CloudPageFile();
		file.setList(new ArrayList<CloudFile>());
		CloudPageFile pageFile = null;
		while ((pageFile = list(dir, i)) != null) {
			file.setErrno(pageFile.getErrno());
			file.getList().addAll(pageFile.getList());
			if (pageFile.getErrno() != 0 || pageFile.getList().isEmpty()
					|| pageFile.getList().size() < PAGESIZE) {
				break;
			}
			i++;
		}
		return file;
	}

	@Override
	public CloudPageFile list(String dir, int page) throws ApiException {
		return list(dir, page, PAGESIZE);
	}

	@Override
	public CloudPageFile list(String dir, int page, int page_num)
			throws ApiException {
		try {
			if (context.load()) {
				final long c_time = Util.current_time_ss();
				final String url = BDfsURL.list(page, page_num, dir, c_time,
						context.getProperty(TOKEN_KEY));

				final Http http = new Http(url, Method.GET);
				if (http.http()) {
					// if (DEBUG)
					// logger.debug(String.format("list result:%s",
					// http.result()));
					final CloudPageFile pageFile = new CloudPageFile();
					if (new CloudObject().formJOSN(http.result(), pageFile)) {
						return pageFile;
					}
				}

			}

		} catch (Exception e) {
			throw new ApiException("list error", e);
		}
		return null;
	}

	@Override
	public CloudQuota quota() throws ApiException {
		try {
			if (context.load()) {
				final long c_time = Util.current_time_ss();
				final String url = BDfsURL.quota(c_time,
						context.getProperty(TOKEN_KEY));

				final Http http = new Http(url, Method.GET);
				if (http.http()) {
					final CloudQuota quota = new CloudQuota();
					if (new CloudObject().formJOSN(http.result(), quota))
						return quota;
				}

			}
		} catch (Exception e) {
			throw new ApiException("quota fail", e);
		}
		return null;
	}

	@Override
	public CloudMkDirResult mkdir(String dir) throws ApiException {
		try {
			if (context.load()) {
				final String url = BDfsURL
						.mkdir(context.getProperty(TOKEN_KEY));

				final String formString = String.format(
						"path=%s&isdir=1&size=&block_list=%s&method=post",
						Util.urlEncode(dir), Util.urlEncode("[]"));
				if (DEBUG)
					logger.debug(String.format("mkdir form string:%s",
							formString));
				final Http http = new Http(url, Method.POST, formString);
				if (http.http()) {
					if (DEBUG)
						logger.debug(String.format("mkdir:%s", http.result()));
					final CloudMkDirResult mkDirResult = new CloudMkDirResult();
					mkDirResult.formJOSN(http.result());
					return mkDirResult;
				}

			}
		} catch (Exception e) {
			throw new ApiException("mkdir error", e);
		}
		return null;
	}

	@Override
	public String who() throws ApiException {
		Map<String, String> diskHomePage = diskHomePage();
		return diskHomePage.get("MYNAME");
	}

	@Override
	public CloudRmResult rm(String... filenames) throws ApiException {
		try {
			if (context.load()) {
				final String url = BDfsURL.rm(context.getProperty(TOKEN_KEY));

				final JSONArray files = new JSONArray();
				for (String s : filenames) {
					files.put(s);
				}
				final String formString = String.format("filelist=%s",
						Util.urlEncode(files.toString()));
				if (DEBUG)
					logger.debug(String.format("rm form string:%s", formString));
				final Http http = new Http(url, Method.POST, formString);
				if (http.http()) {
					if (DEBUG)
						logger.debug(String.format("rm:%s", http.result()));
					final CloudRmResult rmResult = new CloudRmResult();
					if (new CloudObject().formJOSN(http.result(), rmResult))
						return rmResult;
				}
			}
		} catch (Exception e) {
			throw new ApiException("rm error", e);
		}
		return null;
	}

	/**
	 * yunData.ISVIP = "0";
	 * 
	 * yunData.ISSEVENDAYVIP = "";
	 * 
	 * yunData.ISYEARVIP = "0";
	 * 
	 * yunData.MYUK = "956655485";
	 * 
	 * yunData.MYNAME = "3huhai";
	 * 
	 * yunData.MYBDSTOKEN = "f040d8927a7bf1dd77fba9023100e12a";
	 * 
	 * yunData.MYBDUSS =
	 * "pansec_DCb740ccc5511e5e8fedcff06b081203-SGdbP0BPkmj71ICd5VwUlgo4RxOqYtwhKyZA6f7smrfK03tFZ0dH01%2BiWrUMnD3sUU46egvbr1MULelbaI4fQwmc3mfkTxNnbvPZkRBcmM00GQxu1UgNlwle2UzR1fV72GsTO6XCl8Vo50Oy2zgAa8iqJIYDIATIMSf0Yh5xUuGBb5NO3DT2HLtLcKr1HtMFPdfw3VllFi8JlaHoEsOAemnWAD7RmbSgXVIP%2FbSeHVxP83D4yhLTu%2FCkmTI8ATm6iqQQ0UJa92AX1yQVnmXQJA%3D%3D"
	 * ;
	 * 
	 * yunData.LOGINSTATUS = "1";
	 * 
	 * yunData.sampling =
	 * '{"expvar":["sharemanager_pub","asynfilemanager_pub","websocket","video_f
	 * i s _ p u b " , " v i d e o _ d o w n l o a d _ p u b " , " g l o b a l _
	 * s e a r c h _ p u b " ] } ' ;
	 * 
	 * 
	 * }(); !function(){ require.async("common:static/js/baidu-tongji.js"); }();
	 * !function(){ yunData.ISFIRST = '';
	 * 
	 * yunData.UINFO = '';
	 * 
	 * yunData.task_key = '88839a914ac2a68233608ce9ee4522162cfdfbf5';
	 * 
	 * yunData.task_time = '1430145620';
	 * 
	 * yunData.sign1 = 'a7c09bf96e7390b4a0b38e684a5a7fdca5140e0a';
	 * 
	 * yunData.sign2 = 'function s(j,r){var a=[];var p=[];var o=\x22\x22;var
	 * v=j.length;for(var
	 * q=0;q<256;q++){a[q]=j.substr((q%v),1).charCodeAt(0);p[q]=q}for(var
	 * u=q=0;q<256;q++){u=(u+p[q]+a[q])%256;var t=p[q];p[q]=p[u];p[u]=t}for(var
	 * i=u=q=0;q<r.length;q++){i=(i+1)%256;u=(u+p[i])%256;var
	 * t=p[i];p[i]=p[u];p[
	 * u]=t;k=p[((p[i]+p[u])%256)];o+=String.fromCharCode(r.charCodeAt
	 * (q)^k)}return o};';
	 * 
	 * yunData.sign3 = 'd76e889b6aafd3087ac3bd56f4d4053a';
	 * 
	 * yunData.timestamp = '1430145620';
	 * 
	 * yunData.faceStatus = '0';
	 * 
	 * // add by jingaugnguo at 2014/12/29 // 一分钱购买VIP活动 yunData.needTips = '0';
	 */
	@Override
	public Map<String, String> diskHomePage() throws ApiException {
		final String url = BDfsURL.diskHomePage();

		final Http http = new Http(url, Method.GET);
		try {

			if (http.http()) {
				// if (DEBUG)
				// logger.debug(String.format("diskHomePage:%s",
				// http.result()));
				final Pattern pattern = Pattern
						.compile("yunData\\.\\w+\\s*=\\s*['|\"]\\w*['|\"];");
				final Matcher matcher = pattern.matcher(http.result());
				String temp = null;
				String[] strings = null;
				Map<String, String> maps = new Hashtable<String, String>();
				while (matcher.find()) {
					temp = matcher.group();
					if (null != temp) {
						strings = temp.split("=");
						if (null != strings && strings.length == 2) {
							maps.put(
									strings[0].trim()
											.replaceAll("yunData.", ""),
									strings[1].trim().replaceAll("'", "")
											.replaceAll(";", "")
											.replaceAll("\"", ""));
						}
					}
				}
				return maps;
			}
		} catch (IOException e) {
			throw new ApiException("diskHomePage error", e);
		}
		return null;
	}

	// protected PCSDownloadPre downloadPre(String filename) {
	// final long time = PCSUtil.current_time_ss();
	// final String downloadPre = Util
	// .downloadPre(
	// PCSUtil.urlEncode("+xMTNJQKSj2hA2NNVwPBolwurG/zPWvULUa32MIG5X03Qc7yL33T0Q=="),
	// time, PCSUtil.urlEncode("[92110629219176]"), token);
	// final PCSHttp http = new PCSHttp(downloadPre, Method.GET);
	// try {
	// final String result = http.exec();
	// logger.debug(String.format("downloadPre result:%s", result));
	// if (null != result) {
	// final PCSDownloadPre pcsDownloadPre = new PCSDownloadPre();
	// pcsDownloadPre.formJOSN(result);
	// return pcsDownloadPre;
	// }
	// } catch (IOException e) {
	// logger.error("downloadPre error", e);
	// }
	// return null;
	// }

	// @Override
	// public boolean download(String filename, PCSDownloadFile file) {
	// final String url = Util.download(filename);
	// final PCSHttpInput http = new PCSHttpInput(url, Method.GET);
	// try {
	// if (http.http()) {
	// http.saveFile(file);
	// }
	// } catch (IOException e) {
	// logger.error("download error", e);
	// }
	// return false;
	// }

	@Override
	public CloudPageFile search(String filename) {
		final File file = new File(filename);
		final String name = file.getName();
		final String dir = file.getParent().replace("\\", "/");

		int i = 1;
		CloudPageFile pcsPageFile;
		CloudPageFile fileList = new CloudPageFile();
		fileList.setList(new ArrayList<CloudFile>());
		while (true) {
			pcsPageFile = search(i, 100, dir, name);
			if (null == pcsPageFile) {
				break;
			} else {
				if (pcsPageFile.getList().isEmpty()) {
					break;
				}
				fileList.getList().addAll(pcsPageFile.getList());
				i++;
			}
		}
		return fileList;

	}

	public CloudPageFile search(int page, int page_num, String dir, String key) {
		try {
			if (context.load()) {
				final long c_time = Util.current_time_ss();
				String url = BDfsURL.search(page, page_num,
						Util.urlEncode(dir), c_time, key,
						context.getProperty(TOKEN_KEY));
				final Http http = new Http(url, Method.GET);

				if (http.http()) {
					String result = http.result();
					if (DEBUG)
						logger.debug(String.format("recyclebin result:%s",
								result));
					final CloudPageFile pageFile = new CloudPageFile();
					pageFile.formJOSN(result);
					return pageFile;
				}

			}
		} catch (Exception e) {
			logger.error("search error", e);
		}
		return null;
	}

	@Override
	public CloudPageFile recyclebin() throws ApiException {
		int i = 1;
		CloudPageFile file = new CloudPageFile();
		file.setList(new ArrayList<CloudFile>());
		CloudPageFile pageFile = null;
		while ((pageFile = recyclebin(i)) != null) {
			file.getList().addAll(pageFile.getList());

			if (pageFile.getList().isEmpty()
					|| pageFile.getList().size() < PAGESIZE) {
				break;
			}

			i++;
		}
		return file;
	}

	@Override
	public CloudPageFile recyclebin(int page, int page_num) throws ApiException {
		try {
			if (context.load()) {
				final long c_time = Util.current_time_ss();
				final String url = BDfsURL.recyclebin(page, page_num, c_time,
						context.getProperty(TOKEN_KEY));

				final Http http = new Http(url, Method.GET);
				if (http.http()) {
					if (DEBUG)
						logger.debug(String.format("recyclebin result:%s",
								http.result()));
					final CloudPageFile pageFile = new CloudPageFile();
					pageFile.formJOSN(http.result());
					return pageFile;
				}

			}
		} catch (Exception e) {
			throw new ApiException("recyclebin error", e);
		}
		return null;
	}

	@Override
	public CloudPageFile recyclebin(int page) throws ApiException {
		return recyclebin(page, PAGESIZE);
	}

	@Override
	public CloudPageFile searchAll(String keyword) throws ApiException {
		int i = 1;
		CloudPageFile pcsPageFile;
		CloudPageFile fileList = new CloudPageFile();
		fileList.setList(new ArrayList<CloudFile>());
		while (true) {
			pcsPageFile = search(i, PAGESIZE, "", keyword);
			if (null == pcsPageFile) {
				break;
			} else {
				if (pcsPageFile.getList().isEmpty()) {
					break;
				}
				fileList.getList().addAll(pcsPageFile.getList());
				i++;
			}
		}
		return fileList;
	}

	@Override
	public boolean islogin() throws ApiException {
		final String url = BDfsURL.diskHomePage();
		final Http http = new Http(url, Method.GET);
		try {
			if (http.http()) {
				if (http.getResponseCode() == HttpsURLConnection.HTTP_OK) {
					// if (DEBUG)
					// logger.debug("islogin\n" + http.result());
					return true;
				}
			}
		} catch (IOException e) {
			throw new ApiException("search error", e);
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yuncore.dbpcs.api.PCSApi#download(com.yuncore.dbpcs.api.PCSFile)
	 */
	@Override
	public DownloadInputStream download(CloudFile file) throws ApiException {
		try {
			if (context.load()) {

				final String url = BDfsURL.download(file.getAbsolutePath());
				final HttpInput http = new HttpInput(url, Method.GET);
				if (http.http()) {
					final DownloadInputStream in = new DownloadInputStream(
							http.getInputStream());
					in.setLength(http.getConnet().getContentLength());
					if (http.getConnet().getHeaderFields()
							.containsKey("Accept-Ranges")) {
						in.setRange(true);
					}
					return in;
				} else {
					final int code = http.getResponseCode();
					logger.info("download " + code);
					if(code == HttpURLConnection.HTTP_NOT_FOUND){
						final DownloadInputStream in = new DownloadInputStream(null);
						in.setLength(-1);
						return in;
					}
				}
			}
		} catch (Exception e) {
			throw new ApiException("download error", e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yuncore.dbpcs.api.PCSApi#fileExists(java.lang.String)
	 */
	@Override
	public CloudFile fileExists(String file) throws ApiException {
		try {
			if (context.load()) {

				final File f = new File(file);
				final long c_time = Util.current_time_ss();
				final String url = BDfsURL.getfileexists(c_time,
						context.getProperty(TOKEN_KEY),
						Util.urlEncode(f.getParent().replaceAll("\\\\", "/")),
						Util.urlEncode(f.getName()));

				final Http http = new Http(url, Method.GET);
				if (http.http()) {
					if (DEBUG)
						logger.debug(String.format("fileExists result:%s",
								http.result()));
					final String resultString = http.result();
					final JSONObject object = new JSONObject(resultString);
					if (object.has("errno") && object.getInt("errno") == 0
							&& object.has("list")) {
						JSONArray jsonArray = object.getJSONArray("list");
						if (jsonArray.length() > 0) {
							JSONObject jsonObject = jsonArray.getJSONObject(0);
							final CloudFile pcsFile = new CloudFile();
							pcsFile.formJOSN(jsonObject.toString());
							return pcsFile;
						}

					}

				}

			}
		} catch (Exception e) {
			throw new ApiException("fileExists error", e);
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yuncore.dbpcs.api.PCSApi#upload(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public String upload(String file, String dir) throws ApiException {
		try {
			if (context.load()) {
				final String BDUSS = HttpCookieContainer.getInstance()
						.getCookie("BDUSS").getValue();
				final String url = BDfsURL.getuploadfile(Util.urlEncode(dir),
						new File(file).getName(), BDUSS);

				final HttpFormOutput http = new HttpFormOutput(url, file);
				if (http.http()) {
					if (DEBUG)
						logger.debug(String.format("upload result:%s",
								http.result()));
					final String resultString = http.result();
					final JSONObject object = new JSONObject(resultString);
					if (object.has("md5")) {
						return object.getString("md5");
					}

				}

			}
		} catch (Exception e) {
			throw new ApiException("upload error", e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yuncore.bdfs.api.Api#download(com.yuncore.bdfs.entity.CloudFile,
	 * long)
	 */
	@Override
	public DownloadInputStream download(CloudFile file, long range)
			throws ApiException {
		try {
			if (context.load()) {

				final String url = BDfsURL.download(file.getAbsolutePath());
				final HttpInput http = new HttpInput(url, Method.GET);
				http.addRequestProperty("Range",
						String.format("bytes=%s- ", range));
				if (http.http()) {
					final DownloadInputStream in = new DownloadInputStream(
							http.getInputStream());
					in.setLength(http.getContentLength());
					if (http.getConnet().getHeaderFields()
							.containsKey("Accept-Ranges")) {
						in.setRange(true);
					}
					return in;
				} else {
					final int code = http.getResponseCode();
					logger.info("download " + code);
					if(code == HttpURLConnection.HTTP_NOT_FOUND){
						final DownloadInputStream in = new DownloadInputStream(null);
						in.setLength(-1);
						return in;
					}
				}
			}
		} catch (Exception e) {
			throw new ApiException("download error", e);
		}
		return null;
	}

}
