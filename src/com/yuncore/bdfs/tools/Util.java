package com.yuncore.bdfs.tools;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import com.yuncore.bdfs.entity.LocalFile;

public class Util {

	private static String BYTE_SIZE_UNIT[] = { "BYTE", "KB", "MB", "GB", "TB" };

	public static long current_time_ss() {
		return System.currentTimeMillis() / 1000;
	}

	public static String readToString(InputStream in, String charset)
			throws IOException {
		if (null != in) {
			final ByteArrayOutputStream bos = new ByteArrayOutputStream();
			final byte[] buffer = new byte[1024];
			// 1KB的缓冲区
			int read = -1;
			while ((read = in.read(buffer)) > 0) { // 读取到缓冲区
				bos.write(buffer, 0, read);
			}
			return new String(bos.toByteArray(), charset);

		}
		return null;
	}

	public static String readToStringByGzip(InputStream in, String charset)
			throws IOException {
		if (null != in) {
			final ByteArrayOutputStream bos = new ByteArrayOutputStream();
			final byte[] buffer = new byte[1024];
			// 1KB的缓冲区
			int read = -1;
			while ((read = in.read(buffer)) > 0) { // 读取到缓冲区
				bos.write(buffer, 0, read);
			}

			if (bos.size() > 0) {
				byte[] ungzip = ungzip(bos.toByteArray());
				return new String(ungzip, charset);
			}

		}
		return null;
	}

	/**
	 * 把指定的字节数组用gzip解压缩
	 * 
	 * @param src
	 * @return 解压缩过的字节数组
	 */
	public static byte[] ungzip(byte[] src) {
		if (null != src) {
			final ByteArrayInputStream is = new ByteArrayInputStream(src);
			final ByteArrayOutputStream os = new ByteArrayOutputStream(
					src.length);
			try {
				final GZIPInputStream in = new GZIPInputStream(is);
				final byte[] buffer = new byte[512];
				int len = 0;
				while ((len = in.read(buffer)) != -1) {
					os.write(buffer, 0, len);
					os.flush();
				}
				in.close();
				return os.toByteArray();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		return null;
	}

	public static String urlEncode(String str) {

		try {
			return URLEncoder.encode(str, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return "";
		}
	}

	public static String byteSizeToString(long size) {
		final StringBuilder builder = new StringBuilder();
		int i = 0;
		double unit = size;
		double temp = 0;
		while ((temp = (unit / 1024)) >= 1) {
			i++;
			unit = temp;
		}
		builder.append(String.format("%.1f", unit));
		// builder.append(".").append(size % 1024);
		builder.append(BYTE_SIZE_UNIT[i]);
		return builder.toString();
	}

	/**
	 * 取文件的所在的路径
	 * 
	 * @param file
	 * @return
	 */
	public static String getUnixPath(String file) {
		if (null != file) {
			int lastIndexOf = file.lastIndexOf("/");
			if (lastIndexOf == 0) {
				return "/";
			} else {
				return file.substring(0, lastIndexOf);
			}
		}
		return null;
	}

	/**
	 * 取文件的所在的文件
	 * 
	 * @param file
	 * @return
	 */
	public static String getUnixFileName(String file) {
		if (null != file) {
			int lastIndexOf = file.lastIndexOf("/");
			return file.substring(lastIndexOf + 1);
		}
		return null;
	}

	public static <T> List<T> toList(T[] ts) {
		final List<T> list = new ArrayList<T>();
		for (T f : ts) {
			list.add(f);
		}
		return list;
	}

	public static <T> List<T> findSame(List<T> one, List<T> two) {

		final List<T> setSame = new ArrayList<T>();

		if (!one.isEmpty() && two.isEmpty()) {
			final Set<T> set = new HashSet<T>();

			set.addAll(one);

			for (T t : two) {
				if (!set.add(t)) {
					setSame.add(t);
				}
			}
		}
		return setSame;
	}

	/**
	 * md5加密
	 * 
	 * @param s
	 * @return
	 */
	public final static String md5(String s) {
		try {
			byte[] btInput = s.getBytes();
			// 获得MD5摘要算法的 MessageDigest 对象
			MessageDigest mdInst = MessageDigest.getInstance("MD5");
			// 使用指定的字节更新摘要
			mdInst.update(btInput);
			// 获得密文
			byte[] md = mdInst.digest();
			final StringBuilder hexString = new StringBuilder();
			// 字节数组转换为 十六进制 数
			for (int i = 0; i < md.length; i++) {
				String shaHex = Integer.toHexString(md[i] & 0xFF);
				// if (shaHex.length() < 2) {
				// hexString.append(0);
				// }
				hexString.append(shaHex);
			}
			return hexString.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * md5加密
	 * 
	 * @param s
	 * @return
	 */
	public final static long md5Long(String s) {
		try {
			final byte[] btInput = s.getBytes();
			// 获得MD5摘要算法的 MessageDigest 对象
			final MessageDigest mdInst = MessageDigest.getInstance("MD5");
			// 使用指定的字节更新摘要
			mdInst.update(btInput);
			// 获得密文
			final byte[] md = mdInst.digest();
			// Long.
			System.out.println(md.length);
		} catch (Exception e) {
		}
		return 0l;
	}

	/**
	 * 读取目录
	 * 
	 * @param dir
	 * @return
	 */
	public static List<LocalFile> listFiles(String dir, long session) {
		final File file = new File(dir);
		if (file.exists() && file.isDirectory()) {
			File[] listFiles = file.listFiles();
			if (null != listFiles) {
				final List<LocalFile> list = new ArrayList<LocalFile>();
				LocalFile localFile = null;
				for (File f : listFiles) {
					localFile = new LocalFile();
					localFile.setDir(f.getParent());
					if (f.isFile()) {
						localFile.setLength(f.length());
					}
					localFile.setName(f.getName());
					localFile.setType(f.isFile() ? 0 : 1);
					localFile.setSession(session);
					localFile.setfId(localFile.toFid());
					list.add(localFile);
				}

				return list;
			}
		}
		return null;
	}

	public static boolean deleteEqs(List<LocalFile> file, List<LocalFile> same) {
		boolean result = true;

		for (LocalFile s : same) {
			for (LocalFile l : file) {
				if (l.equals(s)) {
					file.remove(l);
					break;
				}
			}
		}

		return result;
	}

	/**
	 * 把指定的字节数组用gzip压缩
	 * 
	 * @param src
	 * @return 压缩过的字节数组
	 */
	public static byte[] gzip(byte[] src) {
		if (null != src) {
			final ByteArrayOutputStream os = new ByteArrayOutputStream(
					src.length);
			try {
				final GZIPOutputStream gzipOutputStream = new GZIPOutputStream(
						os);
				gzipOutputStream.write(src);
				gzipOutputStream.flush();
				gzipOutputStream.close();
				return os.toByteArray();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static String formatTime(long time) {
		final SimpleDateFormat format = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		return format.format(new Date(time));
	}

	public static boolean rmDirFile(String dir) {
		final File dirFile = new File(dir);
		if (dirFile.exists()) {
			final File[] listFiles = dirFile.listFiles();
			if (listFiles != null) {
				for (File f : listFiles) {
					if (!f.delete()) {
						return false;
					}
				}
			}
		}
		return false;
	}

	/**
	 * 取当前进程里的所有线程
	 * 
	 * @return
	 */
	public static List<String> getAllThreads() {
		final List<String> list = new ArrayList<String>();
		Map<Thread, StackTraceElement[]> maps = Thread.getAllStackTraces();
		Iterator<Entry<Thread, StackTraceElement[]>> entrySet = maps.entrySet()
				.iterator();
		Entry<Thread, StackTraceElement[]> next = null;
		while (entrySet.hasNext()) {
			next = entrySet.next();
			list.add(next.getKey().getName());
		}
		return list;
	}

	public static String execmd(String cmd) throws Exception {
		final StringBuffer buffer = new StringBuffer();
		buffer.append(cmd).append("\n");
		Process exec = Runtime.getRuntime().exec(buffer.toString());
		final InputStream in = exec.getInputStream();
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		final byte bs[] = new byte[1024];
		int len = -1;
		while (-1 != (len = in.read(bs))) {
			out.write(bs, 0, len);
		}
		exec.waitFor();
		return new String(out.toByteArray());
	}

	public static void main(String[] args) {
		md5Long("test");
	}
}
