package com.yuncore.dbpcs;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import junit.framework.TestCase;

public class HttpClientTest extends TestCase {

	/**
	 * @param args
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
//	public void httpTest() throws ClientProtocolException, IOException {
//		// 创建HttpClient实例
//		HttpHost proxy = new HttpHost("127.0.0.1", 8888);
//		DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
//		CloseableHttpClient httpclient = HttpClients.custom()
//		        .setRoutePlanner(routePlanner)
//		        .setRedirectStrategy(new RedirectStrategy() {
//					
//					@Override
//					public boolean isRedirected(HttpRequest paramHttpRequest,
//							HttpResponse paramHttpResponse, HttpContext paramHttpContext)
//							throws ProtocolException {
//						return false;
//					}
//					
//					@Override
//					public HttpUriRequest getRedirect(HttpRequest paramHttpRequest,
//							HttpResponse paramHttpResponse, HttpContext paramHttpContext)
//							throws ProtocolException {
//						// TODO Auto-generated method stub
//						return null;
//					}
//				})
//				.setDefaultCookieSpecRegistry(new Lookup<CookieSpecProvider>() {
//					
//					@Override
//					public CookieSpecProvider lookup(String arg0) {
//						// TODO Auto-generated method stub
//						return null;
//					}
//				})
//		        .build();
//		// 创建Get方法实例
////		HttpGet httpgets = new HttpGet("https://passport.baidu.com/v2/api/?getapi&tpl=netdisk&apiver=v3&tt=" +( System.currentTimeMillis() / 1000 )+"&class=login&logintype=basicLogin&callback=bd__cbs__pwxtn7");
//		HttpGet httpgets = new HttpGet("https://www.baidu.com");
//		httpgets.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.153 Safari/537.36");
//		httpgets.addHeader("Accept", "*/*");
//		HttpResponse response = httpclient.execute(httpgets);
//		HttpEntity entity = response.getEntity();
//		System.out.println(response.getStatusLine().getStatusCode() + "\n");
//		
//		for (Header h : response.getAllHeaders()) {
//			System.out.println(h.toString());
//		}
//		System.out.println("");
//		if (entity != null) {
//			System.out.println(EntityUtils.toString(entity));
//		}
//	}	// public static String convertStreamToString(InputStream is) {
	// BufferedReader reader = new BufferedReader(new InputStreamReader(is));
	// StringBuilder sb = new StringBuilder();
	//
	// String line = null;
	// try {
	// while ((line = reader.readLine()) != null) {
	// sb.append(line + "\n");
	// }
	// } catch (IOException e) {
	// e.printStackTrace();
	// } finally {
	// try {
	// is.close();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// }
	// return sb.toString();
	// }
	
	public void datatoString() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		System.out.println(format.format(new Date(1430099432000l)));
	}
	
	public void testSign(){
		MD5 md5 = new MD5();
		System.out.println(md5.getMD5Code("8BDIMXV2-O_EBA53272C36E4350B9C4BF90A6BDA995-C_0-D_WD-WCC2EJS48507-M_50E54982CCD6-V_0004DB65000000000000000000000000000000005.2.6.1JwAxADQAMwAwADAAOQA5ADIAMgA2ACwAMQAwAC4AMAAuADAALgA2ACwAOQA5ADgAJwA="));
	}

	public void test_urlconntion() throws IOException {
		
		URL url = new URL("http://pan.baidu.com/api/list?channel=chunlei&clienttype=0&web=1&t=" +(System.currentTimeMillis() / 1000) +"&bdstoken=3f49b2e4e3ec2aba692717b292ff0fc5&_="+(System.currentTimeMillis() / 1000) +"&dir=%2F&page=1&num=20&order=name");
//		URL url = new URL("http://pan.baidu.com/disk/home");
		Proxy proxy = new Proxy(Type.HTTP, new InetSocketAddress("localhost", 8888));
		HttpURLConnection conn = (HttpURLConnection) url.openConnection(proxy );
	
		conn.setDoInput(true);
		conn.setDoOutput(true);
		conn.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.153 Safari/537.36");
		conn.addRequestProperty("Accept", "*/*");
		conn.addRequestProperty("Cookie", "PANPSC=14048334734149306599%3AMH68bVsDvKKPTY8j5a0ZoYTnJIs%2FjJJ%2BSrvFsiwiy%2BG0xfgIQs9R2n3xqEHuRetVPlDwL%2B9iKarXG9GmBZpeFdqyuBDgQha%2Bj%2FF8UySlqbi0o5SRKELDyPN5v0h6Ka7ah8WgL4aD1J%2Bzv6DonhQBcA%3D%3D; BDUSS=3JXY2ZDQU5yUVozTUNHQUVyT0UxSTNiUVF3NUJFVDJKVWV6R1AxUG1wQnZUR1ZWQVFBQUFBJCQAAAAAAAAAAAEAAABJ6TRkAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAG-~PVVvvz1Va; H_PS_PSSID=13469_13633_1432_13462_12772_13074_12824_13381_12868_13322_12692_13410_10562_12722_13439_13210_13287_13601_13161_13257_11817_13086_8498; BIDUPSID=B1F6D87BAE134F726EDFF4057ACF9323; BAIDUID=B1F6D87BAE134F726EDFF4057ACF9323:FG=1; PANWEB=1");
		conn.setRequestMethod("GET");
		
		InputStream is = null;
		ByteArrayOutputStream bos = null;
		if (conn.getResponseCode() != 200) {
//			return null;
		}
		is = conn.getInputStream();
		byte[] buffer = new byte[1024];
		// 1KB的缓冲区
		int read = -1;
		bos = new ByteArrayOutputStream();
		while ((read = is.read(buffer)) > 0) { // 读取到缓冲区
			bos.write(buffer, 0, read);
		}
		String ipmsgStr = new String(bos.toByteArray(), "UTF-8");
		System.out.println(ipmsgStr);
	}
	
}
